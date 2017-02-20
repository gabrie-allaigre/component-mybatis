import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.talanlabs.entity.factory.IdFactory;
import com.talanlabs.mybatis.component.factory.ComponentObjectFactory;
import com.talanlabs.mybatis.component.session.ComponentSqlSessionManager;
import com.talanlabs.mybatis.guice.DefaultComponentMyBatisModule;
import com.talanlabs.mybatis.guice.configuration.ComponentConfigurationProvider;
import com.talanlabs.mybatis.guice.session.ComponentSqlSessionManagerProvider;
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

                install(new DefaultComponentMyBatisModule());

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

        //System.nin.println(user.getVersion());

        IUser user2 = UserBuilder.newBuilder().id(IdFactory.IdString.from("10")).login("test").build();
        System.out.println(fooService.insert(user2));
        System.out.println(user2);
/*
        user = fooService.findById(IUser.class, user2.getId());
        System.nin.println(user);

        System.nin.println(fooService.delete(IUser.class, user));
        user = fooService.findById(IUser.class, user2.getId());
        System.nin.println(user);*/
    }
}

