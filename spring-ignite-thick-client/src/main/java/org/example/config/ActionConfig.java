package org.example.config;

import org.apache.ignite.IgniteQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Configuration
public class ActionConfig {
    @Bean
    @ConditionalOnProperty(prefix = "ignite", name = "action", havingValue = "consumer")
    public Consumer<IgniteQueue<Integer>> consumerAction() {
            return (IgniteQueue<Integer> intQueue) -> {
                int value = intQueue.take();
                System.out.println(LocalDateTime.now() + "Received value from queue " + value);
            };
    }

    @Bean
    @ConditionalOnProperty(prefix = "ignite", name = "action", havingValue = "producer")
    public Consumer<IgniteQueue<Integer>> producerAction() {
        return (IgniteQueue<Integer> intQueue) -> {
            boolean result = intQueue.add(123);

            System.out.println(LocalDateTime.now() + " added to queue: 123 with result " + result);
        };
    }

    @Bean
    @ConditionalOnProperty(prefix = "ignite", name = "action", havingValue = "cleaner")
    public Consumer<IgniteQueue<Integer>> cleanerAction() {
        return (IgniteQueue<Integer> intQueue) -> {
            intQueue.close();
            System.out.println("Queue closed");
            System.exit(1);
        };
    }
}
