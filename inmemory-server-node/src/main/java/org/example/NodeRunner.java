package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

public class NodeRunner {

    private static IgniteQueue<Integer> createQueue(Ignite ignite, String name) {
        return ignite.queue(name,0,new CollectionConfiguration());
    }

    public static void main(String[] args) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        igniteConfiguration.setDeploymentMode(DeploymentMode.CONTINUOUS);
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi());
        ClientConnectorConfiguration clientCfg = new ClientConnectorConfiguration();
        igniteConfiguration.setClientConnectorConfiguration(clientCfg);
        Ignite ignite = Ignition.start(igniteConfiguration);

        Thread closeThread = new Thread(()-> ignite.close());
        Runtime.getRuntime().addShutdownHook(closeThread);
    }
}
