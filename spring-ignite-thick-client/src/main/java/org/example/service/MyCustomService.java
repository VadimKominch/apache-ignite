package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.services.Service;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class MyCustomService implements Service, Serializable {
    @SpringResource
    private Ignite ignite;

    @Override
    public void execute() throws Exception {
        ignite.cacheNames().stream().forEach(System.out::println);
    }
}
