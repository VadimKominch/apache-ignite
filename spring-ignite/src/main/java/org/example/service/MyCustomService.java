package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.services.Service;

import java.io.Serializable;
/**
 * Class to be manually added to classpath to be deployed in cluster.
 * */
public class MyCustomService implements Service, Serializable {
    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public void execute() throws Exception {
        System.out.println("Before service execution");
        ignite.cacheNames().stream().forEach(System.out::println);
        System.out.println("After service execution");
    }
}
