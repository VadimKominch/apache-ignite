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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class IgniteCommandLineRunner implements CommandLineRunner {
    private final Ignite ignite;
    private final Consumer<IgniteQueue<Integer>> actionToBeDone;

    public IgniteCommandLineRunner(Ignite ignite, Consumer<IgniteQueue<Integer>> actionToBeDone) {
        this.ignite = ignite;
        this.actionToBeDone = actionToBeDone;
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
        System.out.println("Creating queue or getting reference to it");
        return ignite.queue(name,0,new CollectionConfiguration());
    }

    @Override
    public void run(String... args) throws Exception {
//        putBinaryToCache();
//        deployService();
        boolean recreate = false;
        ARTExceptionLogManager logManager = new ARTExceptionLogManager(); // replace with ArtExceptionLogManager
        IgniteQueue<Integer> intQueue = createQueue("TempQueue");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
                actionToBeDone.accept(intQueue);
                logManager.clear();
            } catch (IllegalStateException e) {
                if (e.getCause() instanceof CacheStoppedException exception) {
//                    Collection<String> caches = ignite.cacheNames().stream().filter(el -> el.contains("datastructures")).toList();
//                    ignite.resetLostPartitions(caches);
                    System.out.println("Cache was stopped. Queue need to be recreated");
                    recreate = true;
                }

                //in case of restart will always be false
                if( !logManager.contains(e)) {
                    if(intQueue.removed()) {
                        recreate = true;
                    }
                    System.out.println(e.getMessage());
                }
                logManager.add(e);
            } catch (Exception e) {
                if(!logManager.contains(e)) {
                    System.out.println(e.getClass());
                    System.out.println(e.getMessage());
                    logManager.add(e);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    System.out.println("Before break");
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if(recreate) {
                Thread.sleep(15000);
                intQueue = createQueue("TempQueue");
                recreate = false;
                logManager.clear();
            }
        }
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

class ARTExceptionLogManager {
    private final Set<Exception> includedExceptions;

    public ARTExceptionLogManager() {
        this.includedExceptions = ConcurrentHashMap.newKeySet();
    }

    public void add(Exception ex) {
        includedExceptions.add(ex);
    }

    public boolean contains(Exception ex) {
        return includedExceptions.contains(ex);
    }

    public void clear() {
        includedExceptions.clear();
    }
}