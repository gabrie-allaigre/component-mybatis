import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.component.session.ComponentSqlSessionManager;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import com.synaptix.mybatis.component.session.observer.ITriggerObserver;
import com.synaptix.mybatis.guice.DefaultComponentMyBatisModule;
import com.synaptix.mybatis.guice.configuration.ComponentConfigurationProvider;
import com.synaptix.mybatis.guice.session.ComponentSqlSessionManagerProvider;
import com.synaptix.mybatis.simple.observer.TracableTriggerObserver;
import mapper.NlsMapper;
import mapper.UserMapper;
import model.CountryBuilder;
import model.ICountry;
import org.apache.commons.lang3.reflect.ConstructorUtils;
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

                install(new DefaultComponentMyBatisModule());

                lazyLoadingEnabled(true);
                aggressiveLazyLoading(false);

                useConfigurationProvider(ComponentConfigurationProvider.class);
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                bindObjectFactoryType(ComponentObjectFactory.class);
                addInterceptorClass(ExamplePlugin.class);

                addMapperClass(UserMapper.class);
                addMapperClass(NlsMapper.class);

                Names.bindProperties(binder(), createTestProperties());
                bind(FooService.class).in(Singleton.class);

                bind(DefaultNlsColumnHandler.class).in(Singleton.class);
                bind(INlsColumnHandler.class).to(DefaultNlsColumnHandler.class).in(Singleton.class);

                bind(DefaultUserByHandler.class).in(Singleton.class);
                bind(TracableTriggerObserver.IUserByHandler.class).to(DefaultUserByHandler.class).in(Singleton.class);

                Multibinder<ITriggerObserver> triggerObserverMultibinder = Multibinder.newSetBinder(binder(), ITriggerObserver.class);
                triggerObserverMultibinder.addBinding().toConstructor(ConstructorUtils.getAccessibleConstructor(TracableTriggerObserver.class, TracableTriggerObserver.IUserByHandler.class));

                bind(ComponentSqlSessionManager.class).toProvider(ComponentSqlSessionManagerProvider.class).in(Scopes.SINGLETON);
            }

            private Properties createTestProperties() {
                Properties myBatisProperties = new Properties();
                myBatisProperties.setProperty("mybatis.environment.id", "test");
                myBatisProperties.setProperty("JDBC.schema", "mybatis-guice_TEST");
                myBatisProperties.setProperty("JDBC.username", "sa");
                myBatisProperties.setProperty("JDBC.password", "");
                myBatisProperties.setProperty("JDBC.autoCommit", "false");
                return myBatisProperties;
            }
        });

        DefaultNlsColumnHandler defaultNlsColumnHandler = injector.getInstance(DefaultNlsColumnHandler.class);
        defaultNlsColumnHandler.setLanguageCode("fra");

        DefaultUserByHandler defaultUserByHandler = injector.getInstance(DefaultUserByHandler.class);
        defaultUserByHandler.setUserBy("GABY");

        FooService fooService = injector.getInstance(FooService.class);
        fooService.init("init-script.sql");

        ICountry country = CountryBuilder.newBuilder().code("FRA").name("France").build();
        System.out.println(fooService.insert(country));
        System.out.println(country);
        System.out.println(fooService.findById(ICountry.class, country.getId()));

        defaultNlsColumnHandler.setLanguageCode("eng");
        ICountry country1 = fooService.findById(ICountry.class, country.getId());
        System.out.println(country1);
        country1.setName("French");
        fooService.update(country1);

        System.out.println(fooService.findById(ICountry.class, country.getId()));

        defaultNlsColumnHandler.setLanguageCode("fra");
        ICountry country2 = fooService.findById(ICountry.class, country.getId());
        System.out.println(country2);
        country2.setName("Frommage");
        fooService.update(country2);

        defaultNlsColumnHandler.setLanguageCode("eng");
        System.out.println(fooService.findById(ICountry.class, country.getId()));
    }
}

