import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.engine.where.registry.DefaultComparisonOperatorManagerRegistry;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import model.ICountry;
import model.IPerson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainRsql {

    public static void main(String[] args) throws UnsupportedEncodingException {
        IRsqlConfiguration rsqlConfiguration = RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).build();

        ComponentRsqlVisitor<ICountry> countryComponentRsqlVisitor = new ComponentRsqlVisitor<>(ICountry.class, new DefaultComparisonOperatorManagerRegistry(rsqlConfiguration));
        ComponentRsqlVisitor<IPerson> personComponentRsqlVisitor = new ComponentRsqlVisitor<>(IPerson.class, new DefaultComparisonOperatorManagerRegistry(rsqlConfiguration));

        Node node = new RSQLParser().parse("name==" + URLEncoder.encode("\\*USA*", "UTF-8"));

        SqlResult res = node.accept(countryComponentRsqlVisitor, EngineContext.newBulder().defaultTablePrefix("t").build());
        System.out.println(res.joins);
        System.out.println(res.sql);
        System.out.println(res.parameterMap);
    }
}
