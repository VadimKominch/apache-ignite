package org.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Bean
    @Qualifier("client-node")
    public Ignite igniteClient(IgniteConfiguration cfg) {
        return Ignition.start(cfg);
    }

}
