package org.example.config;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.springframework.boot.autoconfigure.IgniteConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringIgniteConfig {

    // fat client
    @Bean
    public IgniteConfigurer igniteCfg() {
        return config -> {
            config.setClientMode(true);
        };
    }
}
