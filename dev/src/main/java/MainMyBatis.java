import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.ComponentMappedStatementFactory;
import com.synaptix.mybatis.component.ComponentResultMapFactory;
import com.synaptix.mybatis.reflection.factory.ComponentObjectFactory;
import guice.SimpleSynaptixMyBatisModule;
import mapper.UserMapper;
import model.IUser;
import org.apache.ibatis.session.Configuration;
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

                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                bindObjectFactoryType(ComponentObjectFactory.class);
                addInterceptorClass(ExamplePlugin.class);

                addMapperClass(UserMapper.class);

                Names.bindProperties(binder(), createTestProperties());
                bind(FooService.class).in(Singleton.class);
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

        Configuration configuration = injector.getInstance(Configuration.class);

        ComponentResultMapFactory componentResultMapHelper = new ComponentResultMapFactory(configuration);
        ComponentMappedStatementFactory componentMappedStatementFactory = new ComponentMappedStatementFactory(configuration);

        configuration.addResultMap(componentResultMapHelper.createComponentResultMap(IUser.class));
        configuration.addMappedStatement(componentMappedStatementFactory.createComponentFindEntityByIdMappedStatement(IUser.class));

        FooService fooService = injector.getInstance(FooService.class);
        fooService.init();

        fooService.findById(IUser.class, IdFactory.IdString.from("1"));

        //System.out.println(fooService.findUserById(IdFactory.IdString.from("1")));
        //System.out.println(fooService.findUserByLogin("sandra"));
    }
}

