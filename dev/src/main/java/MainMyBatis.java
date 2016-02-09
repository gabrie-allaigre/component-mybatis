import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.FindComponentsByMappedStatementFactory;
import com.synaptix.mybatis.component.statement.FindEntityByIdMappedStatementFactory;
import com.synaptix.mybatis.guice.SimpleSynaptixMyBatisModule;
import com.synaptix.mybatis.guice.SynaptixConfigurationProvider;
import com.synaptix.mybatis.guice.registry.GuiceMappedStatementFactoryRegistry;
import com.synaptix.mybatis.guice.registry.GuiceResultMapFactoryRegistry;
import com.synaptix.mybatis.component.factory.ComponentObjectFactory;
import com.synaptix.mybatis.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.session.factory.IResultMapFactory;
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

                Multibinder<IResultMapFactory> resultMapFactoryMultibinder = Multibinder.newSetBinder(binder(), IResultMapFactory.class);
                resultMapFactoryMultibinder.addBinding().to(ComponentResultMapFactory.class);

                Multibinder<IMappedStatementFactory> mappedStatementFactoryMultibinder = Multibinder.newSetBinder(binder(), IMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(FindEntityByIdMappedStatementFactory.class);
                mappedStatementFactoryMultibinder.addBinding().to(FindComponentsByMappedStatementFactory.class);
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

        System.out.println(user.getCountry());
        //System.out.println(fooService.findUserById(IdFactory.IdString.from("1")));
        //System.out.println(fooService.findUserByLogin("sandra"));
    }
}

