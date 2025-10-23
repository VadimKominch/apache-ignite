package org.example;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

public class NodeRunner {

    public static void main(String[] args) {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setPeerClassLoadingEnabled(true);
        igniteConfiguration.setDeploymentMode(DeploymentMode.CONTINUOUS);
        igniteConfiguration.setDiscoverySpi(new TcpDiscoverySpi());
        ClientConnectorConfiguration clientCfg = new ClientConnectorConfiguration();
        igniteConfiguration.setClientConnectorConfiguration(clientCfg);
        Ignite ignite = Ignition.start(igniteConfiguration);
        ignite.cluster().state(ClusterState.ACTIVE);

        Thread closeThread = new Thread(ignite::close);
        Runtime.getRuntime().addShutdownHook(closeThread);
    }
}
