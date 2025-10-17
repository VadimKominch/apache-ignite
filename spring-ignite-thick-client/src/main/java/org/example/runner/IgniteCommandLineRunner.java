package org.example.runner;

import org.apache.ignite.*;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.internal.processors.cache.CacheStoppedException;
import org.apache.ignite.services.ServiceConfiguration;
import org.example.model.Person;
import org.example.service.MyCustomService;
import org.example.task.PrintTask;
import org.h2.store.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final Ignite ignite;

    public IgniteCommandLineRunner(Ignite ignite) {
        this.ignite = ignite;
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
        ignite.services().deploy(getServiceConfig());
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

    private IgniteQueue<Integer> createQueue(String name) {
         return ignite.queue(name,0,new CollectionConfiguration());
    }

    @Override
    public void run(String... args) throws Exception {
//        putBinaryToCache();
//        deployService();
        boolean recreate = false;
        IgniteQueue<Integer> intQueue = createQueue("TempQueue");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
                intQueue.take();
            } catch (IllegalStateException e) {
                if (e.getCause() instanceof CacheStoppedException exception) {
                    Collection<String> caches = ignite.cacheNames().stream().filter(el -> el.contains("datastructures")).toList();
                    ignite.resetLostPartitions(caches);
                    recreate = true;
                }
//                System.out.println("queue was removed: "+ intQueue.removed()); //in case of reconnecting will always be false
            } catch (Exception e) {
                System.out.println("common exception");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    System.out.println("Before break");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if(recreate)
                intQueue = createQueue("TempQueue");
        }
        System.out.println("Command line runner");
    }

    private ServiceConfiguration getServiceConfig() {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setName("eventHandlerService");
        cfg.setMaxPerNodeCount(1);
        cfg.setService(new MyCustomService());
        return cfg;
    }
}
