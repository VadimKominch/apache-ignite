package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObject;
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
        ignite.cacheNames().forEach(System.out::println);
        IgniteCache<Object, BinaryObject> errors = ignite.getOrCreateCache("errors").withKeepBinary();
        var key = ignite.binary().builder(Object.class.getSimpleName())
                .setField("iteration", 1)
                .build();
        BinaryObject binaryError = errors.get(key);
        String value = binaryError.type().field("element").value(binaryError);
        System.out.println(value);

        System.out.println("After service execution");
    }
}
