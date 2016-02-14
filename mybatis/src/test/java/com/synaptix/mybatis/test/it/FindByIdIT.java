package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.FindEntityByIdMappedStatementFactory;
import com.synaptix.mybatis.session.SynaptixConfiguration;
import com.synaptix.mybatis.session.registry.MappedStatementFactoryRegistryBuilder;
import com.synaptix.mybatis.session.registry.ResultMapFactoryRegistryBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;

public class FindByIdIT {

    protected Configuration configuration;

    @Before
    public void toto() {
        Environment environment = new Environment.Builder("test").dataSource(new PooledDataSource(null, "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:mybatis-guice_TEST", "sa", ""))
                .transactionFactory(new JdbcTransactionFactory()).build();

        SynaptixConfiguration synaptixConfiguration = new SynaptixConfiguration(environment);
        synaptixConfiguration.setMappedStatementFactoryRegistry(MappedStatementFactoryRegistryBuilder.newBuilder().addMappedStatementFactory(new FindEntityByIdMappedStatementFactory()).build());
        synaptixConfiguration.setResultMapFactoryRegistry(ResultMapFactoryRegistryBuilder.newBuilder().addResultMapFactory(new ComponentResultMapFactory()).build());
        synaptixConfiguration.setProxyFactory(new ComponentProxyFactory());

        this.configuration = synaptixConfiguration;
    }

    @Test
    public void testFindById() {

    }
}
