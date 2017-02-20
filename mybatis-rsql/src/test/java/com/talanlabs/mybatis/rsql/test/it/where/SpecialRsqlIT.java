package com.talanlabs.mybatis.rsql.test.it.where;

import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.engine.where.registry.DefaultComparisonOperatorManagerRegistry;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import cz.jirutka.rsql.parser.RSQLParser;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SpecialRsqlIT {

    private static ComponentRsqlVisitor<IPerson> personComponentRsqlVisitor;
    private static RSQLParser rsqlParser;

    private EngineContext engineContext;

    @BeforeClass
    public static void initGlobal() {
        personComponentRsqlVisitor = new ComponentRsqlVisitor<>(IPerson.class, new DefaultComparisonOperatorManagerRegistry(RsqlConfigurationBuilder.newBuilder().build()));

        rsqlParser = new RSQLParser();
    }

    @Before
    public void init() {
        engineContext = EngineContext.newBulder().defaultTablePrefix("").build();
    }

    @Test
    public void testDoubleQuoteStringRsql() {
        SqlResult res = rsqlParser.parse("firstName==\"FRA\"").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testQuoteStringRsql() {
        SqlResult res = rsqlParser.parse("firstName=='FRA'").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testReservedStringRsql() {
        SqlResult res = rsqlParser.parse("firstName=='==FRAorand,;'").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "==FRAorand,;");
    }

    @Test
    public void testQuoteQuoteStringRsql() {
        SqlResult res = rsqlParser.parse("firstName=='\\'FRA\"'").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "'FRA\"");
    }

    @Test
    public void testStarStringRsql() {
        SqlResult res = rsqlParser.parse("firstName=='\\\\*FRA\\\\*'").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME = #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "*FRA*");
    }

    @Test
    public void testLikeStringRsql() {
        SqlResult res = rsqlParser.parse("firstName=='**FRA**'").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME like '%' || #{0,javaType=java.lang.String} || '%' ESCAPE '\\'");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }
}
