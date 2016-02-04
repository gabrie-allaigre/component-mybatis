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

public class ComparisonOperatorNotEqualRsqlIT {

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
    public void testSimpleStringRsql() {
        SqlResult res = rsqlParser.parse("firstName!=FRA").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME <> #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testStartStringRsql() {
        SqlResult res = rsqlParser.parse("firstName!=FRA*").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME not like #{0,javaType=java.lang.String} || '%'");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testEndStringRsql() {
        SqlResult res = rsqlParser.parse("firstName!=*FRA").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME not like '%' || #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testContains1StringRsql() {
        SqlResult res = rsqlParser.parse("firstName!=*FRA*").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME not like '%' || #{0,javaType=java.lang.String} || '%'");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA");
    }

    @Test
    public void testContains2StringRsql() {
        SqlResult res = rsqlParser.parse("firstName!=*F*A*").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("FIRST_NAME not like '%' || #{0,javaType=java.lang.String} || '%' || #{1,javaType=java.lang.String} || '%'");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "F").containsEntry("1", "A");
    }

    @Test
    public void testThreeStringRsql() {
        SqlResult res = rsqlParser.parse("(firstName!=FRA,firstName!=ENG);lastName!=GAB").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("((FIRST_NAME <> #{0,javaType=java.lang.String} OR FIRST_NAME <> #{1,javaType=java.lang.String}) AND LAST_NAME <> #{2,javaType=java.lang.String})");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "FRA").containsEntry("1", "ENG").containsEntry("2", "GAB");
    }

    @Test
    public void testSimpleIntegerRsql() {
        SqlResult res = rsqlParser.parse("age!=10").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("AGE <> #{0,javaType=int}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 10);
    }

    @Test
    public void testSimpleFloatRsql() {
        SqlResult res = rsqlParser.parse("height!=180.5").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("HEIGHT <> #{0,javaType=float}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 180.5f);
    }

    @Test
    public void testSimpleDoubleRsql() {
        SqlResult res = rsqlParser.parse("weight!=75.25").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("WEIGHT <> #{0,javaType=double}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", 75.25);
    }

    @Test
    public void testContainsDoubleRsql() {
        SqlResult res = rsqlParser.parse("weight!=7*").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("WEIGHT not like #{0,javaType=java.lang.String} || '%'");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "7");
    }

    @Test
    public void testSimpleEnumRsql() {
        SqlResult res = rsqlParser.parse("sexe!=WOMAN").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("SEXE <> #{0,javaType=com.talanlabs.mybatis.rsql.test.data.IPerson.Sexe}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", IPerson.Sexe.WOMAN);
    }

    @Test
    public void testContainsEnumRsql() {
        SqlResult res = rsqlParser.parse("sexe!=*MAN").accept(personComponentRsqlVisitor, engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.sql).isEqualTo("SEXE not like '%' || #{0,javaType=java.lang.String}");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "MAN");
    }
}
