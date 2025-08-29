package org.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringIgniteConfig {

    // fat client
    @Bean
    public Ignite igniteClient() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true); // важно: клиентский режим

        return Ignition.start(cfg);
    }
}
