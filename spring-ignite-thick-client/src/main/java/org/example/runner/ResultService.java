package org.example.runner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.internal.processors.cache.CacheStoppedException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class ResultService {
    private final Ignite ignite;
    private final Consumer<IgniteQueue<Integer>> actionToBeDone;

    public ResultService(Ignite ignite, Consumer<IgniteQueue<Integer>> actionToBeDone) {
        this.ignite = ignite;
        this.actionToBeDone = actionToBeDone;
    }

    private IgniteQueue<Integer> createQueue(String name) {
        System.out.println("Creating queue or getting reference to it");
//        ignite.services().serviceDescriptors().contains("123456789");
        return ignite.queue(name,0,new CollectionConfiguration());
    }

    public void transferResult() throws InterruptedException {
        boolean recreate = false;
        ConcurrentHashMap.KeySetView<Object, Boolean> logManager = ConcurrentHashMap.newKeySet(); // replace with ArtExceptionLogManager
        IgniteQueue<Integer> intQueue = createQueue("TempQueue");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
                actionToBeDone.accept(intQueue);
                logManager.clear();
            } catch (IllegalStateException e) {
                if (e.getCause() instanceof CacheStoppedException exception) {
                    // no active services was found
                    boolean isRedeploy = ignite.services().serviceDescriptors().stream().anyMatch(el -> el.name().equals("eventHandlerService"));
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
    }
}
