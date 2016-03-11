import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.cache.ComponentCacheFactory;
import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.session.ComponentSqlSessionManager;
import com.synaptix.mybatis.component.session.factory.ICacheFactory;
import com.synaptix.mybatis.component.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.component.session.factory.IResultMapFactory;
import com.synaptix.mybatis.component.statement.*;
import com.synaptix.mybatis.guice.SimpleMyBatisModule;
import com.synaptix.mybatis.guice.configuration.ComponentConfigurationProvider;
import com.synaptix.mybatis.guice.session.ComponentSqlSessionManagerProvider;
import mapper.UserMapper;
import model.IUser;
import model.UserBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

import java.util.Properties;

public class MainSQLServer {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.SQL_Server_2005_MS_Driver);

                install(new SimpleMyBatisModule());

                lazyLoadingEnabled(true);
                aggressiveLazyLoading(false);

                useConfigurationProvider(ComponentConfigurationProvider.class);
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                bindObjectFactoryType(ComponentObjectFactory.class);
                addInterceptorClass(ExamplePlugin.class);

                addMapperClass(UserMapper.class);

                Names.bindProperties(binder(), createTestProperties());
                bind(FooService.class).in(Singleton.class);

                Multibinder<IResultMapFactory> resultMapFactoryMultibinder = Multibinder.newSetBinder(binder(), IResultMapFactory.class);
                resultMapFactoryMultibinder.addBinding().to(ComponentResultMapFactory.class);

                Multibinder<IMappedStatementFactory> mappedStatementFactoryMultibinder = Multibinder.newSetBinder(binder(), IMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(FindEntityByIdMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(FindComponentsByMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(FindComponentsByJoinTableMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(InsertMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(UpdateMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(DeleteMappedStatementFactory.class);

                Multibinder<ICacheFactory> cacheFactoryMultibinder = Multibinder.newSetBinder(binder(), ICacheFactory.class);
                cacheFactoryMultibinder.addBinding().to(ComponentCacheFactory.class);

                bind(ComponentSqlSessionManager.class).toProvider(ComponentSqlSessionManagerProvider.class).in(Scopes.SINGLETON);
            }

            private Properties createTestProperties() {
                Properties myBatisProperties = new Properties();
                myBatisProperties.setProperty("mybatis.environment.id", "test");
                myBatisProperties.setProperty("JDBC.host", "192.168.11.134");
                myBatisProperties.setProperty("JDBC.schema", "test");
                myBatisProperties.setProperty("JDBC.username", "test");
                myBatisProperties.setProperty("JDBC.password", "test");
                myBatisProperties.setProperty("JDBC.autoCommit", "false");
                return myBatisProperties;
            }
        });

        FooService fooService = injector.getInstance(FooService.class);
        fooService.init("init-script-sqlserver.sql");

        IUser user = fooService.findById(IUser.class, IdFactory.IdString.from("1"));
        //IUser user2 = fooService.findById(IUser.class, IdFactory.IdString.from("1"));

        System.out.println(user);

        //System.out.println(user.getVersion());

        IUser user2 = UserBuilder.newBuilder().id(IdFactory.IdString.from("10")).login("test").build();
        System.out.println(fooService.insert(user2));
        System.out.println(user2);
/*
        user = fooService.findById(IUser.class, user2.getId());
        System.out.println(user);

        System.out.println(fooService.delete(IUser.class, user));
        user = fooService.findById(IUser.class, user2.getId());
        System.out.println(user);*/
    }
}

