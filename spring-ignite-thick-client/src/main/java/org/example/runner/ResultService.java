package org.example.runner;

import jakarta.annotation.PreDestroy;
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
    private IgniteQueue<Integer> intQueue;

    public ResultService(Ignite ignite, Consumer<IgniteQueue<Integer>> actionToBeDone) {
        this.ignite = ignite;
        this.actionToBeDone = actionToBeDone;
        this.intQueue = createQueue();
    }

    private IgniteQueue<Integer> createQueue() {
        System.out.println("Creating queue or getting reference to it");
        return ignite.queue("TempQueue",0,new CollectionConfiguration());
    }

    @PreDestroy
    public void destroy() {
        if(intQueue != null) {
            System.out.println("Closing queue");
            intQueue.close();
        }
    }

    public void transferResult() {
        boolean recreate = false;
        ConcurrentHashMap.KeySetView<Class<? extends Exception>, Boolean> logManager = ConcurrentHashMap.newKeySet(); // replace with ArtExceptionLogManager

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
                actionToBeDone.accept(intQueue);
                logManager.clear();
            } catch (IllegalStateException e) {
//                boolean isRedeploy = ignite.services().serviceDescriptors().stream().anyMatch(el -> el.name().equals("eventHandlerService"));
                if(!logManager.contains(e.getClass())) {
                    if (e.getCause() instanceof CacheStoppedException exception) {
                        // no active services was found
                        System.out.println("Cache was stopped. Queue need to be recreated");
                        recreate = true;
                    }
                    System.out.println(e.getMessage());
                }
                logManager.add(e.getClass());
            } catch (Exception e) {
                if(!logManager.contains(e.getClass())) {
                    System.out.println(e.getClass());
                    System.out.println(e.getMessage());
                    logManager.add(e.getClass());
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
                System.out.println("Recreating queue from recreate flag");
                intQueue = createQueue();
                recreate = false;
//                logManager.clear();
            }
        }
    }
}
