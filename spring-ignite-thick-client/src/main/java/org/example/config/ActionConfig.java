package org.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class ActionConfig {
    @Bean
    public Supplier<Void> actionToBeDone(Ignite ignite) {
        String action = System.getProperty("mode");
        Supplier<Void> actionSupplier;
        if(action.equals("consumer")) {
            actionSupplier = () -> {
                IgniteQueue<Integer> intQueue = ignite.queue("TempQueue",0,new CollectionConfiguration());
                int value = intQueue.take();
                System.out.println("Received value from queue " + value);
                return null;
            };

        } else {
            actionSupplier = () -> {
                IgniteQueue<Integer> intQueue = ignite.queue("TempQueue",0,new CollectionConfiguration());
                intQueue.add(123);
                return null;
            };
        }
        return actionSupplier;
    }
}
