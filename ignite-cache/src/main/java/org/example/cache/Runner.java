package org.example.cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

public class Runner {
    public static void main(String[] args) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();

        try(Ignite ignite = Ignition.start()) {

            System.out.println("Node started");

            CacheConfiguration<Integer, String> cacheConfiguration = new CacheConfiguration<>("mycache");
            cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
            cacheConfiguration.setBackups(1); // кол-во реплик

            IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheConfiguration);
            cache.put(1, "Hello");
            cache.put(2, "Ignite");
            cache.put(3, "Cluster!");

            System.out.println("Entry 1 " + cache.get(1));
            System.out.println("Entry 2 " + cache.get(2));

            System.out.println("Cache size is " + cache.size());
            Thread.sleep(5000);

        } catch (InterruptedException ignored) { }
    }
}
