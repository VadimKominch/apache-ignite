package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Runner {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Runner.class, args);
        Ignite ignite = (Ignite) context.getBean("ignite");
        ignite.cluster().state(ClusterState.ACTIVE);
    }
}
