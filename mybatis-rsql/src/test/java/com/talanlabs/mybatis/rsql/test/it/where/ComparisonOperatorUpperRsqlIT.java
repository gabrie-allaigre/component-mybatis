package com.talanlabs.mybatis.rsql.test.it.where;

import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.engine.where.registry.DefaultComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.where.registry.IComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import com.talanlabs.mybatis.rsql.test.it.config.DefaultNlsColumnHandler;
import cz.jirutka.rsql.parser.RSQLParser;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ComparisonOperatorUpperRsqlIT {

    private static ComponentRsqlVisitor<IPerson> personComponentRsqlVisitor;
    private static ComponentRsqlVisitor<ICountry> countryComponentRsqlVisitor;
    private static RSQLParser rsqlParser;

    private EngineContext engineContext;

    @BeforeClass
    public static void initGlobal() {
        IComparisonOperatorManagerRegistry comparisonOperatorManagerRegistry = new DefaultComparisonOperatorManagerRegistry(
                RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).stringPolicy(new AlwaysUpperStringPolicy()).build());
        personComponentRsqlVisitor = new ComponentRsqlVisitor<>(IPerson.class, comparisonOperatorManagerRegistry);
        countryComponentRsqlVisitor = new ComponentRsqlVisitor<>(ICountry.class, comparisonOperatorManagerRegistry);

        rsqlParser = new RSQLParser();
    }

    @Before
    public void init() {
        engineContext = EngineContext.newBulder().defaultTablePrefix("t").build();
    }

    @Test
    public void testUpperStringRsql() {
        SqlResult res = rsqlParser.parse("firstName==fra").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("UPPER(t.FIRST_NAME) = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testUpperStringRsql1() {
        SqlResult res = rsqlParser.parse("name==Fra").accept(countryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly(
                "T_NLS j0 ON j0.TABLE_NAME = #{0,javaType=java.lang.String} AND j0.COLUMN_NAME = #{1,javaType=java.lang.String} AND j0.LANGUAGE_CODE = #{2,javaType=java.lang.String} AND j0.TABLE_ID = t.ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("UPPER(NVL(j0.MEANING, t.NAME)) = #{3,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "T_COUNTRY").containsEntry("1", "NAME").containsEntry("2", "fra").containsEntry("3", "FRA");
    }
}
