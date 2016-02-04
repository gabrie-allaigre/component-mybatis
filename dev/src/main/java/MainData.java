import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import java.sql.Connection;
import java.sql.SQLException;

public class MainData {

    public static void main(String[] args) throws Exception {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSourceClassName("org.hsqldb.jdbc.JDBCDataSource");
        hikariConfig.addDataSourceProperty("url", "jdbc:hsqldb:mem:gaby");
        hikariConfig.addDataSourceProperty("user", "sa");
        hikariConfig.addDataSourceProperty("password", "sa");
        hikariConfig.setAutoCommit(false);
        hikariConfig.setMaximumPoolSize(10);

        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

        try (final Connection c = hikariDataSource.getConnection()) {
            Liquibase liquibase = new Liquibase("changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(hikariDataSource.getConnection()));
            liquibase.update(new Contexts());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException("Error during liquibase execution", e);
        }

        IDataSet dataSet = new FlatXmlDataFileLoader().load("/dataset.xml");
        IDatabaseTester databaseTester = new DataSourceDatabaseTester(hikariDataSource);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();

        databaseTester.onTearDown();

    }

}
