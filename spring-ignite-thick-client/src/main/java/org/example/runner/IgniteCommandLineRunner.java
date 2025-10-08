package org.example.runner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.services.ServiceConfiguration;
import org.example.model.Person;
import org.example.service.MyCustomService;
import org.example.task.PrintTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final Ignite ignite;

    public IgniteCommandLineRunner(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void run(String... args) throws Exception {
        var key = ignite.binary().builder(Object.class.getSimpleName())
                .setField("iteration", 1)
                .build();
        var value = ignite.binary().builder(Object.class.getSimpleName())
                .setField("element", "elementStr")
                .setField("exception","exception")
                .build();
        ignite.getOrCreateCache("errors").put(key,value);

        System.out.println("Executing compute task command line runner");
        IgniteCompute compute = ignite.compute();
        List<PrintTask> computeList = new ArrayList<>();
        for (String word : "Print words on different cluster nodes".split(" ")) {
            computeList.add(new PrintTask(word));
        }
        String result = compute.call(computeList).stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println("Computing result is " + result);

        System.out.println("Topology nodes size is " + ignite.cluster().nodes().size());
        ignite.services().deploy(getServiceConfig());
        IgniteCache<Integer, Person> cache = ignite.cache("person-cache");
        ScanQuery<Integer,Person> scanQuery = new ScanQuery<>((k,v) -> !v.getName().isEmpty());

        List<Person> personList = new ArrayList<>();

//        try(QueryCursor<AffinityKey<List<Long>>,List<List>> cursor = cache.query(scanQuery)) {
        try(QueryCursor<Cache.Entry<Integer, Person>> cursor = cache.query(scanQuery)) {
            Iterator<Cache.Entry<Integer, Person>> iterator = cursor.iterator();
            while (iterator.hasNext()) {
                Cache.Entry<Integer, Person> entry = iterator.next();
                personList.add(entry.getValue());
            }
        }
        System.out.println();
    }

    private ServiceConfiguration getServiceConfig() {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setName("eventHandlerService");
        cfg.setMaxPerNodeCount(1);
        cfg.setService(new MyCustomService());
        return cfg;
    }
}
