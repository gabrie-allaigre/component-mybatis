package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.component.cache.ComponentCacheFactory;
import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.*;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.registry.CacheFactoryRegistryBuilder;
import com.synaptix.mybatis.session.registry.MappedStatementFactoryRegistryBuilder;
import com.synaptix.mybatis.session.registry.ResultMapFactoryRegistryBuilder;
import com.synaptix.mybatis.simple.handler.IdTypeHandler;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;

public abstract class AbstractIntegration {

    protected static Configuration configuration;

    protected static SqlSessionManager sqlSessionManager;

    @BeforeClass
    public static void beforeClass() {
        Environment environment = new Environment.Builder("test").dataSource(new PooledDataSource(null, "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:mybatis-guice_TEST", "sa", ""))
                .transactionFactory(new JdbcTransactionFactory()).build();

        ComponentConfiguration componentConfiguration = new ComponentConfiguration(environment);
        componentConfiguration.setMappedStatementFactoryRegistry(MappedStatementFactoryRegistryBuilder.newBuilder().addMappedStatementFactory(new FindEntityByIdMappedStatementFactory())
                .addMappedStatementFactory(new FindComponentsByMappedStatementFactory()).addMappedStatementFactory(new FindComponentsByJoinTableMappedStatementFactory())
                .addMappedStatementFactory(new InsertMappedStatementFactory()).addMappedStatementFactory(new UpdateMappedStatementFactory())
                .addMappedStatementFactory(new DeleteMappedStatementFactory()).build());
        componentConfiguration.setResultMapFactoryRegistry(ResultMapFactoryRegistryBuilder.newBuilder().addResultMapFactory(new ComponentResultMapFactory()).build());
        componentConfiguration.setCacheFactoryRegistry(CacheFactoryRegistryBuilder.newBuilder().addCacheFactory(new ComponentCacheFactory()).build());
        componentConfiguration.setObjectFactory(new ComponentObjectFactory());
        componentConfiguration.setProxyFactory(new ComponentProxyFactory());
        componentConfiguration.setLazyLoadingEnabled(true);
        componentConfiguration.setAggressiveLazyLoading(false);
        componentConfiguration.getTypeHandlerRegistry().register(IdTypeHandler.class);

        configuration = componentConfiguration;

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);
    }

    @AfterClass
    public static void afterClass() {
        sqlSessionManager = null;
        configuration = null;
    }

    @Before
    public void startSqlSession() {
        sqlSessionManager.startManagedSession();

        ScriptRunner scriptRunner = new ScriptRunner(sqlSessionManager.getConnection());
        scriptRunner.setLogWriter(null);
        try {
            scriptRunner.runScript(Resources.getResourceAsReader("init-script.sql"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void closeSqlSession() {
        sqlSessionManager.close();
    }

}
