package com.talanlabs.mybatis.test.it;

import com.talanlabs.mybatis.component.cache.ComponentCacheFactory;
import com.talanlabs.mybatis.component.factory.ComponentObjectFactory;
import com.talanlabs.mybatis.component.factory.ComponentProxyFactory;
import com.talanlabs.mybatis.component.resultmap.ComponentResultMapFactory;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.ComponentSqlSessionManager;
import com.talanlabs.mybatis.component.statement.DeleteComponentsByMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.DeleteEntityByIdMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.DeleteMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.FindComponentsByJoinTableMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.FindComponentsByMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.FindEntityByIdMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.FindNlsColumnMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.InsertMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.UpdateMappedStatementFactory;
import com.talanlabs.mybatis.simple.handler.IdTypeHandler;
import com.talanlabs.mybatis.simple.observer.TracableTriggerObserver;
import com.talanlabs.mybatis.test.it.config.DefaultNlsColumnHandler;
import com.talanlabs.mybatis.test.it.config.DefaultUserByHandler;
import com.talanlabs.mybatis.test.it.mapper.NlsMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;

public abstract class AbstractHSQLIntegration {

    protected static ComponentConfiguration configuration;

    protected static SqlSessionManager sqlSessionManager;

    protected static ComponentSqlSessionManager componentSqlSessionManager;

    protected static DefaultNlsColumnHandler defaultNlsColumnHandler;

    @BeforeClass
    public static void beforeClass() {
        Environment environment = new Environment.Builder("test").dataSource(new PooledDataSource(null, "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:mybatis-guice_TEST", "sa", ""))
                .transactionFactory(new JdbcTransactionFactory()).build();

        ComponentConfiguration componentConfiguration = new ComponentConfiguration(environment);
        componentConfiguration.setObjectFactory(new ComponentObjectFactory());
        componentConfiguration.setProxyFactory(new ComponentProxyFactory());
        componentConfiguration.setLazyLoadingEnabled(true);
        componentConfiguration.setAggressiveLazyLoading(false);
        defaultNlsColumnHandler = new DefaultNlsColumnHandler();
        componentConfiguration.setNlsColumnHandler(defaultNlsColumnHandler);

        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindEntityByIdMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindComponentsByMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindComponentsByJoinTableMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindNlsColumnMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new InsertMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new UpdateMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteEntityByIdMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteComponentsByMappedStatementFactory());

        componentConfiguration.getResultMapFactoryRegistry().registry(new ComponentResultMapFactory());

        componentConfiguration.getCacheFactoryRegistry().registry(new ComponentCacheFactory());

        componentConfiguration.getTriggerDispatcher().addTriggerObserver(new TracableTriggerObserver(new DefaultUserByHandler()));

        componentConfiguration.getTypeHandlerRegistry().register(IdTypeHandler.class);

        componentConfiguration.addMapper(NlsMapper.class);

        configuration = componentConfiguration;

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        sqlSessionManager = SqlSessionManager.newInstance(sqlSessionFactory);

        componentSqlSessionManager = ComponentSqlSessionManager.newInstance(sqlSessionManager);
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
