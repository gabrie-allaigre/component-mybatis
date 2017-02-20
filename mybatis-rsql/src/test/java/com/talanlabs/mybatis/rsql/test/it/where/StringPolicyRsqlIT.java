package com.talanlabs.mybatis.rsql.test.it.where;

import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.engine.where.registry.DefaultComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import com.talanlabs.mybatis.rsql.test.it.config.DefaultNlsColumnHandler;
import cz.jirutka.rsql.parser.RSQLParser;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StringPolicyRsqlIT {

    private static ComponentRsqlVisitor<ICountry> nothingCountryComponentRsqlVisitor;
    private static ComponentRsqlVisitor<ICountry> upperCountryComponentRsqlVisitor;
    private static ComponentRsqlVisitor<ICountry> specialCountryComponentRsqlVisitor;
    private static RSQLParser rsqlParser;

    private EngineContext engineContext;

    @BeforeClass
    public static void initGlobal() {
        nothingCountryComponentRsqlVisitor = new ComponentRsqlVisitor<>(ICountry.class,
                new DefaultComparisonOperatorManagerRegistry(RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).build()));
        upperCountryComponentRsqlVisitor = new ComponentRsqlVisitor<>(ICountry.class, new DefaultComparisonOperatorManagerRegistry(
                RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).stringPolicy(new AlwaysUpperStringPolicy()).build()));
        specialCountryComponentRsqlVisitor = new ComponentRsqlVisitor<>(ICountry.class,
                new DefaultComparisonOperatorManagerRegistry(RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).build()));

        rsqlParser = new RSQLParser();
    }

    @Before
    public void init() {
        engineContext = EngineContext.newBulder().defaultTablePrefix("t").build();
    }

    @Test
    public void testNothingStringRsql() {
        SqlResult res = rsqlParser.parse("code!=FrA").accept(nothingCountryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("t.CODE <> #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FrA");
    }

    @Test
    public void testNothingNlsStringRsql() {
        SqlResult res = rsqlParser.parse("name==FrA").accept(nothingCountryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("NVL(j0.MEANING, t.NAME) = #{3,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("3", "FrA");
    }

    @Test
    public void testUpperStringRsql() {
        SqlResult res = rsqlParser.parse("code!=fra").accept(upperCountryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("UPPER(t.CODE) <> #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testUpperNlsStringRsql() {
        SqlResult res = rsqlParser.parse("name==fra").accept(upperCountryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("UPPER(NVL(j0.MEANING, t.NAME)) = #{3,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("3", "FRA");
    }

    @Test
    public void testSpecialCharacterRsql() {
        SqlResult res = rsqlParser.parse("name==fra").accept(upperCountryComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("UPPER(NVL(j0.MEANING, t.NAME)) = #{3,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("3", "FRA");
    }
}
