package org.example.config;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@EnableCaching
public class SpringIgniteConfig {

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        return cfg;
    }

    // thick client
//    @Bean
//    @Qualifier("client-node")
//    public Ignite igniteClient(IgniteConfiguration cfg) {
//        return Ignition.start(cfg);
//    }

    @Bean
    public SpringCacheManager cacheManager(IgniteConfiguration cfg) {
        SpringCacheManager mgr = new SpringCacheManager();

        mgr.setConfiguration(cfg);

        return mgr;
    }

//    thin client
    @Bean
    public IgniteClient igniteClient() {
        ClientConfiguration cfg = new ClientConfiguration();
        cfg.setAddresses(
                "10.203.32.51:10800",
                "172.25.224.1:10800",
                "localhost:10800");
        return Ignition.startClient(cfg);
    }
//
//    @Bean
//    public IgniteClientSpringCacheManager cacheManager(IgniteClient cli) {
//        return new IgniteClientSpringCacheManager().setClientInstance(cli);
//    }
}
