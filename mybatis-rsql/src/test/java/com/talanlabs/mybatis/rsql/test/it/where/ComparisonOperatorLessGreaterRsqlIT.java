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

public class ComparisonOperatorLessGreaterRsqlIT {

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
    public void testSimpleAgeGtRsql() {
        SqlResult res = rsqlParser.parse("age=gt=12").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE > #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 12);
    }

    @Test
    public void testSimpleAgeGeRsql() {
        SqlResult res = rsqlParser.parse("age=ge=12").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE >= #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 12);
    }

    @Test
    public void testSimpleAgeLtRsql() {
        SqlResult res = rsqlParser.parse("age=lt=12").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE < #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 12);
    }

    @Test
    public void testSimpleAgeLeRsql() {
        SqlResult res = rsqlParser.parse("age=le=12").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE <= #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 12);
    }

    @Test
    public void testSimpleAgeLe2Rsql() {
        SqlResult res = rsqlParser.parse("age<=12").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE <= #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 12);
    }
}
