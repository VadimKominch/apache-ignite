package org.example.config;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Set;

@Component
@ConditionalOnMissingBean(IgniteClusterMeterBinder.class)
public class MBeanIgniteClusterMeterBinder implements MeterBinder {

    private final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();


    @Override
    public void bindTo(MeterRegistry registry) {

        // ==== Cache Metrics ====
        Set<ObjectName> cacheMBeans = null;
        try {
            cacheMBeans = mbs.queryNames(new ObjectName("org.apache:group=cache,*"), null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        for (ObjectName name : cacheMBeans) {
            String cacheName = name.getKeyProperty("name");
            Tag cacheTag = Tag.of("cache", cacheName);

            registerGauge( registry,"ignite.cache.size", "Cache size", name, "CacheSize", cacheTag);
            registerGauge(registry,"ignite.cache.hits", "Cache hits", name, "CacheHits", cacheTag);
            registerGauge(registry,"ignite.cache.misses", "Cache misses", name, "CacheMisses", cacheTag);
        }

        // ==== Ignite Node Metrics ====
        Set<ObjectName> igniteMBeans = null;
        try {
            igniteMBeans = mbs.queryNames(new ObjectName("org.apache:name=ignite,*"), null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        for (ObjectName name : igniteMBeans) {
            Gauge.builder("ignite.node.active", () -> getBooleanAsDouble(name, "Active"))
                    .description("Cluster active flag")
                    .register(registry);

            registerGauge(registry,"ignite.node.clients", "Client node count", name, "TotalClientNodes");
        }

        // ==== Cluster Metrics ====
        Set<ObjectName> clusterMBeans = null;
        try {
            clusterMBeans = mbs.queryNames(new ObjectName("org.apache:name=cluster,*"), null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        for (ObjectName name : clusterMBeans) {
            registerGauge(registry,"ignite.cluster.clients", "Total client nodes", name, "TotalClientNodes");
            registerGauge(registry,"ignite.cluster.servers", "Total server nodes", name, "TotalServerNodes");
        }
    }

    private void registerGauge(MeterRegistry registry,String metricName, String desc, ObjectName bean, String attr, Tag... tags) {
        Gauge.builder(metricName, () -> getDouble(bean, attr))
                .description(desc)
                .tags(Arrays.asList(tags))
                .register(registry);
    }

    private double getDouble(ObjectName bean, String attr) {
        try {
            Object val = mbs.getAttribute(bean, attr);
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    private double getBooleanAsDouble(ObjectName bean, String attr) {
        try {
            Object val = mbs.getAttribute(bean, attr);
            if (val instanceof Boolean) {
                return ((Boolean) val) ? 1.0 : 0.0;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }
}

