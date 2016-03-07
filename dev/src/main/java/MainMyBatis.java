import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.cache.ComponentCacheFactory;
import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.*;
import com.synaptix.mybatis.guice.SimpleSynaptixMyBatisModule;
import com.synaptix.mybatis.guice.SynaptixConfigurationProvider;
import com.synaptix.mybatis.guice.registry.GuiceCacheFactoryRegistry;
import com.synaptix.mybatis.guice.registry.GuiceMappedStatementFactoryRegistry;
import com.synaptix.mybatis.guice.registry.GuiceResultMapFactoryRegistry;
import com.synaptix.mybatis.session.factory.ICacheFactory;
import com.synaptix.mybatis.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.session.factory.IResultMapFactory;
import com.synaptix.mybatis.session.registry.ICacheFactoryRegistry;
import com.synaptix.mybatis.session.registry.IMappedStatementFactoryRegistry;
import com.synaptix.mybatis.session.registry.IResultMapFactoryRegistry;
import mapper.UserMapper;
import model.IUser;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

import java.util.Properties;

public class MainMyBatis {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.HSQLDB_IN_MEMORY_NAMED);

                install(new SimpleSynaptixMyBatisModule());

                lazyLoadingEnabled(true);
                aggressiveLazyLoading(false);

                useConfigurationProvider(SynaptixConfigurationProvider.class);
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                bindObjectFactoryType(ComponentObjectFactory.class);
                addInterceptorClass(ExamplePlugin.class);

                addMapperClass(UserMapper.class);

                Names.bindProperties(binder(), createTestProperties());
                bind(FooService.class).in(Singleton.class);

                bind(IResultMapFactoryRegistry.class).to(GuiceResultMapFactoryRegistry.class).in(Singleton.class);
                bind(IMappedStatementFactoryRegistry.class).to(GuiceMappedStatementFactoryRegistry.class).in(Singleton.class);
                bind(ICacheFactoryRegistry.class).to(GuiceCacheFactoryRegistry.class).in(Singleton.class);

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
            }

            private Properties createTestProperties() {
                Properties myBatisProperties = new Properties();
                myBatisProperties.setProperty("mybatis.environment.id", "test");
                myBatisProperties.setProperty("JDBC.schema", "mybatis-guice_TEST");
                myBatisProperties.setProperty("derby.create", "true");
                myBatisProperties.setProperty("JDBC.username", "sa");
                myBatisProperties.setProperty("JDBC.password", "");
                myBatisProperties.setProperty("JDBC.autoCommit", "false");
                return myBatisProperties;
            }
        });

        FooService fooService = injector.getInstance(FooService.class);
        fooService.init();

        IUser user = fooService.findById(IUser.class, IdFactory.IdString.from("1"));
        IUser user2 = fooService.findById(IUser.class, IdFactory.IdString.from("1"));


        //System.out.println(user.getVersion());
/*
        IUser user2 = UserBuilder.newBuilder().id(IdFactory.IdString.from("10")).login("test").build();
        System.out.println(fooService.insert(IUser.class, user2));
        System.out.println(user2);

        user = fooService.findById(IUser.class, user2.getId());
        System.out.println(user);

        System.out.println(fooService.delete(IUser.class, user));
        user = fooService.findById(IUser.class, user2.getId());
        System.out.println(user);*/
    }
}

