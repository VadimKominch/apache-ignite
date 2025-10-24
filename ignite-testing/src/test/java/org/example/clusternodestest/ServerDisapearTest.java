package org.example.clusternodestest;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.processors.cache.CacheStoppedException;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class ServerDisapearTest {
    private Ignite clientNode;

    @Test
    public void testServerReconnection() {
        String serverNodeName = "Server-node-1";
        String clientNodeName = "Client-node-1";

        Thread serverThread = new Thread(()-> {
            Ignition.start(serverConfig(serverNodeName));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
            Ignition.stop(serverNodeName, true);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}
            Ignition.start(serverConfig(serverNodeName));
        });
        serverThread.start();


        clientNode = Ignition.start(clientConfig(clientNodeName));
        IgniteQueue<Integer> queue = clientNode.queue("TempQueue",0, new CollectionConfiguration());
        Exception raised = assertThrows(IllegalStateException.class, ()-> {
            while(true) {
                try {
                    Thread.sleep(5000);
                    queue.take();
                } catch(IllegalStateException e) {
                    throw e;
                } catch (Exception e) {}
            }
        });
        Assertions.assertInstanceOf(CacheStoppedException.class, raised.getCause());
    }

    @Test
    public void testCacheWasClosed() {
        String serverNodeName = "Server-node-"+ Math.round(Math.random()*100);
        String clientNodeNameFirst = "Client-node-"+ Math.round(Math.random()*100);

        Thread serverThread = new Thread(() -> {
            Ignition.start(serverConfig(serverNodeName));
        });
        Thread clientQueueCleaner = new Thread(() -> {
            IgniteQueue<Integer> queue = clientNode.queue("TempQueue",0, new CollectionConfiguration());
            queue.close();
        });

        serverThread.start();
        clientNode = Ignition.start(clientConfig(clientNodeNameFirst));
        IgniteQueue<Integer> queue = clientNode.queue("TempQueue",0, new CollectionConfiguration());
        clientQueueCleaner.start();
        Exception raised = assertThrows(IllegalStateException.class, ()-> {
            while(true) {
                try {
                    Thread.sleep(5000);
                    queue.take();
                } catch(IllegalStateException e) {
                    throw e;
                } catch (Exception ignored) {}
            }
        });
        Assertions.assertTrue(raised.getMessage().contains("Queue has been removed from cache"));
    }

    private IgniteConfiguration serverConfig(String instanceName) {
        IgniteConfiguration serverConfig = new IgniteConfiguration();
        serverConfig.setClientMode(false);
        serverConfig.setIgniteInstanceName(instanceName);
        serverConfig.setPeerClassLoadingEnabled(true);
        serverConfig.setDeploymentMode(DeploymentMode.CONTINUOUS);
        serverConfig.setDiscoverySpi(new TcpDiscoverySpi());
        ClientConnectorConfiguration clientCfg = new ClientConnectorConfiguration();
        serverConfig.setClientConnectorConfiguration(clientCfg);
        return serverConfig;
    }

    private IgniteConfiguration clientConfig(String instanceName) {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(true);
        cfg.setDiscoverySpi(new TcpDiscoverySpi());
        cfg.setDeploymentMode(DeploymentMode.CONTINUOUS);
        cfg.setPeerClassLoadingEnabled(true);
        cfg.setIgniteInstanceName(instanceName);
        return cfg;
    }
}
