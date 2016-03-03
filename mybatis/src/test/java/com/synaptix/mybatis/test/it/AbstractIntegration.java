package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.FindComponentsByJoinTableMappedStatementFactory;
import com.synaptix.mybatis.component.statement.FindComponentsByMappedStatementFactory;
import com.synaptix.mybatis.component.statement.FindEntityByIdMappedStatementFactory;
import com.synaptix.mybatis.session.SynaptixConfiguration;
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

        SynaptixConfiguration synaptixConfiguration = new SynaptixConfiguration(environment);
        synaptixConfiguration.setMappedStatementFactoryRegistry(MappedStatementFactoryRegistryBuilder.newBuilder().addMappedStatementFactory(new FindEntityByIdMappedStatementFactory())
                .addMappedStatementFactory(new FindComponentsByMappedStatementFactory()).addMappedStatementFactory(new FindComponentsByJoinTableMappedStatementFactory()).build());
        synaptixConfiguration.setResultMapFactoryRegistry(ResultMapFactoryRegistryBuilder.newBuilder().addResultMapFactory(new ComponentResultMapFactory()).build());
        synaptixConfiguration.setObjectFactory(new ComponentObjectFactory());
        synaptixConfiguration.setProxyFactory(new ComponentProxyFactory());
        synaptixConfiguration.getTypeHandlerRegistry().register(IdTypeHandler.class);

        configuration = synaptixConfiguration;

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
