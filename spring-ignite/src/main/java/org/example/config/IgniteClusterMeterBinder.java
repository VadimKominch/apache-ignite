package org.example.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class IgniteClusterMeterBinder implements MeterBinder {
    private final Ignite ignite;

    public IgniteClusterMeterBinder(Ignite ignite) {
        this.ignite = ignite;
    }

    @Override
    public void bindTo(MeterRegistry registry) {

        // ==== Cache Metrics ====
        Collection<String> cacheNames = ignite.cacheNames();

        for (String cacheName : cacheNames) {
            IgniteCache<?,?> cache = ignite.cache(cacheName);
            Tag cacheTag = Tag.of("cache", cacheName);
            CacheMetrics metrics = cache.metrics();
            registerGauge( registry,"ignite.cache.size", "Cache size", (double)metrics.getCacheSize(), cacheTag);
            registerGauge(registry,"ignite.cache.hits", "Cache hits", (double)metrics.getCacheHits(), cacheTag);
            registerGauge(registry,"ignite.cache.misses", "Cache misses", (double)metrics.getCacheMisses(), cacheTag);
        }

        // ==== Ignite Node Metrics ====
        Gauge.builder("ignite.node.active", () -> getBooleanAsDouble(ignite.cluster().state().active()))
                .description("Cluster active flag")
                .register(registry);
        // ==== Cluster Metrics ====

        registerGauge(registry,"ignite.cluster.clients", "Total client nodes", (double)ignite.cluster().forClients().nodes().size());
        registerGauge(registry,"ignite.cluster.servers", "Total server nodes", (double)ignite.cluster().forServers().nodes().size());
    }

    private void registerGauge(MeterRegistry registry,String metricName, String desc, Double value, Tag... tags) {
        Gauge.builder(metricName, () -> value)
                .description(desc)
                .tags(Arrays.asList(tags))
                .register(registry);
    }

    private double getBooleanAsDouble(Boolean value) {
        return value ? 1.0 : 0.0;
    }
}

