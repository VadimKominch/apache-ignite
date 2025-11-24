package org.example.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.services.ServiceConfiguration;
import org.springframework.stereotype.Service;

@Service
public class DeployService {

    private Ignite ignite;

    public DeployService(Ignite ignite) {
        this.ignite = ignite;
    }

    public boolean deploy(String serviceName) {
        ServiceConfiguration cfg = new ServiceConfiguration();
        cfg.setName(serviceName);
        cfg.setMaxPerNodeCount(1);
        cfg.setService(new MyCustomService());
        ignite.services().deploy(cfg);
        return true;
    }
}
