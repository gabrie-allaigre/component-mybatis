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

public class ComparisonOperatorInOutRsqlIT {

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
    public void testSimpleInRsql() {
        SqlResult res = rsqlParser.parse("firstName=in=(FRA,ITA,ESP)").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME IN (#{0,javaType=java.lang.String}, #{1,javaType=java.lang.String}, #{2,javaType=java.lang.String})");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA").containsEntry("1", "ITA").containsEntry("2", "ESP");
    }

    @Test
    public void testSimpleOutRsql() {
        SqlResult res = rsqlParser.parse("firstName=nin=(FRA,ITA,ESP)").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME NOT IN (#{0,javaType=java.lang.String}, #{1,javaType=java.lang.String}, #{2,javaType=java.lang.String})");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA").containsEntry("1", "ITA").containsEntry("2", "ESP");
    }
}
