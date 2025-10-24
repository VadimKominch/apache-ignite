package org.example.runner;

import org.apache.ignite.*;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.services.ServiceConfiguration;
import org.example.model.Person;
import org.example.service.MyCustomService;
import org.example.task.PrintTask;
import org.h2.store.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final Ignite ignite;
    private final ResultService service;

    public IgniteCommandLineRunner(Ignite ignite, ResultService service) {
        this.ignite = ignite;
        this.service = service;
    }

    private void computeTask() {
        System.out.println("Executing compute task command line runner");
        IgniteCompute compute = ignite.compute();
        List<PrintTask> computeList = new ArrayList<>();
        for (String word : "Print words on different cluster nodes".split(" ")) {
            computeList.add(new PrintTask(word));
        }
        String result = compute.call(computeList).stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println("Computing result is " + result);
    }

    private void deployService() {
        System.out.println("Topology nodes size is " + ignite.cluster().nodes().size());
        System.out.println("Service MyCustomService is deployed into cluster");
        ignite.services().deploy(getServiceConfig());
        boolean isRedeploy = ignite.services().serviceDescriptors().stream().anyMatch(el -> el.name().equals("eventHandlerService"));
        System.out.println("Result of deploy is " + isRedeploy);
    }

    public void putBinaryToCache() {
        var key = ignite.binary().builder(Object.class.getSimpleName())
                .setField("iteration", 1)
                .build();
        var value = ignite.binary().builder(Object.class.getSimpleName())
                .setField("element", "elementStr")
                .setField("exception","exception")
                .build();
        ignite.getOrCreateCache("errors").put(key,value);
        ignite.getOrCreateCache("errors").put(1,1);
        // alternative for put a batch to cache
        final IgniteDataStreamer<Integer, Data> dataStreamer = ignite.dataStreamer("errors");


        //get value
        IgniteCache<Object, BinaryObject> errorsCache = ignite.cache("errors").withKeepBinary();
        BinaryObject binaryError = errorsCache.get(key);
        System.out.println(binaryError);
    }

    public void executeScanQuery() {
        IgniteCache<Integer, Person> cache = ignite.cache("person-cache");
        ScanQuery<Integer,Person> scanQuery = new ScanQuery<>((k,v) -> !v.getName().isEmpty());
        System.out.println("Total person count in cache is " + cache.size());
        List<Person> personList = new ArrayList<>();

        try(QueryCursor<Cache.Entry<Integer, Person>> cursor = cache.query(scanQuery)) {
            for (Cache.Entry<Integer, Person> entry : cursor) {
                personList.add(entry.getValue());
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
//        putBinaryToCache();
        deployService();
        service.transferResult();
        System.out.println("Command line runner execution finished");
    }

    private void reconnectOnClientDisconnect(IgniteClientDisconnectedException e) {
        IgniteClientDisconnectedException cause = (IgniteClientDisconnectedException) e.getCause();
        cause.reconnectFuture().get();
    }

    private ServiceConfiguration getServiceConfig() {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setName("eventHandlerService");
        cfg.setMaxPerNodeCount(1);
        cfg.setService(new MyCustomService());
        return cfg;
    }
}
