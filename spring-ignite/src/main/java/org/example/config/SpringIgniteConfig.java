package org.example.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.BasicJdbcDialect;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.ClientConnectorConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.metric.jmx.JmxMetricExporterSpi;
import org.example.model.Person;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.cache.configuration.Factory;
import javax.sql.DataSource;
import java.sql.Types;

@Configuration
public class SpringIgniteConfig {

    @Bean
    @Qualifier("server-node")
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        ClientConnectorConfiguration clientCfg = new ClientConnectorConfiguration();
        cfg.setClientConnectorConfiguration(clientCfg);

        CacheConfiguration<Integer, Person> cacheConfiguration = new CacheConfiguration<>("person-cache");
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setStatisticsEnabled(true);
        cfg.setMetricExporterSpi(
                new JmxMetricExporterSpi()
        );
        // CacheStore
        CacheJdbcPojoStoreFactory<Integer, Person> factory = new CacheJdbcPojoStoreFactory<>();
        factory.setDialect(new BasicJdbcDialect());

        factory.setDataSourceFactory(getDataSourceFactory());

        JdbcType employeeType = getJdbcType();

        factory.setTypes(employeeType);

        cacheConfiguration.setCacheStoreFactory(factory);
        cacheConfiguration.setReadThrough(true);
        cacheConfiguration.setWriteThrough(true);
        cacheConfiguration.setWriteBehindEnabled(true);
        cacheConfiguration.setWriteBehindFlushSize(2);
        cacheConfiguration.setWriteBehindFlushFrequency(50000);
        cacheConfiguration.setIndexedTypes(Integer.class, Person.class);
        cfg.setCacheConfiguration(cacheConfiguration);

        cfg.setPeerClassLoadingEnabled(true);
        cfg.setDeploymentMode(DeploymentMode.CONTINUOUS);
        cfg.setDiscoverySpi(new TcpDiscoverySpi());
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        Ignite ignite = Ignition.start(cfg);
        ignite.cluster().state(ClusterState.ACTIVE);
        return ignite;
    }

    private static JdbcType getJdbcType() {
        JdbcType employeeType = new JdbcType();
        employeeType.setCacheName("person-cache");
        employeeType.setDatabaseTable("person");
        employeeType.setKeyType(Integer.class);
        employeeType.setKeyFields(new JdbcTypeField(Types.INTEGER, "id", Integer.class, "id"));
        employeeType.setValueFields(
                new JdbcTypeField(Types.INTEGER, "id", Integer.class, "id"),
                new JdbcTypeField(Types.VARCHAR, "name", String.class, "name"),
                new JdbcTypeField(Types.INTEGER, "city_id", String.class, "cityId")
        );
        employeeType.setValueType(Person.class);

        return employeeType;
    }

    private static Factory<DataSource> getDataSourceFactory() {
        return () -> {
            DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
            driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
            driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/testdb");
            driverManagerDataSource.setUsername("user");
            driverManagerDataSource.setPassword("pass");
            return driverManagerDataSource;
        };
    }
}
