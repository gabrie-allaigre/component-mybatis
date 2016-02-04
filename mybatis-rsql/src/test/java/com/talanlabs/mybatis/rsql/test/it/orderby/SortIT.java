package com.talanlabs.mybatis.rsql.test.it.orderby;

import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.orderby.ComponentSortVisitor;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.DefaultSortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.ISortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.sort.SortParser;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import com.talanlabs.mybatis.rsql.test.it.config.DefaultNlsColumnHandler;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SortIT {

    private static ComponentSortVisitor<IPerson> personComponentSortVisitor;
    private static SortParser sortParser;

    private EngineContext engineContext;

    @BeforeClass
    public static void initGlobal() {
        ISortDirectionManagerRegistry sortDirectionManagerRegistry = new DefaultSortDirectionManagerRegistry(
                RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).build());
        personComponentSortVisitor = new ComponentSortVisitor<>(IPerson.class, sortDirectionManagerRegistry);

        sortParser = new SortParser();
    }

    @Before
    public void init() {
        engineContext = EngineContext.newBulder().defaultTablePrefix("t").build();
    }

    @Test
    public void testSimpleStringSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringAscSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("+firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringAscNullsFisrtSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("++firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME ASC NULLS FIRST");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringAscNullsLastSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("+-firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME ASC NULLS LAST");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringDescSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME DESC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringDescNullsFirstSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-+firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME DESC NULLS FIRST");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleStringDescNullsLastSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("--firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME DESC NULLS LAST");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testTwoStringSort1() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-firstName,lastName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME DESC, t.LAST_NAME ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testTwoStringSort2() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("+firstName,-lastName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.FIRST_NAME ASC, t.LAST_NAME DESC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleIntSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("+age"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.AGE ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testSimpleDateSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-birthday"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("t.BIRTHDAY DESC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testComplexeSort1() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-address.city"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ADDRESS j0 ON j0.ID = t.ADDRESS_ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("j0.CITY DESC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testComplexeSort2() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-address2.city"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ASSO_PERSON_ADDRESS j0_0 ON j0_0.PERSON_ID = t.ID", "T_ADDRESS j0 ON j0.ID = j0_0.ADDRESS_ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("j0.CITY DESC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testComplexeSort3() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("address.country.code"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ADDRESS j0 ON j0.ID = t.ADDRESS_ID", "T_COUNTRY j1 ON j1.ID = j0.COUNTRY_ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("j1.CODE ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testComplexeSort4() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-address.country.name"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ADDRESS j0 ON j0.ID = t.ADDRESS_ID", "T_COUNTRY j1 ON j1.ID = j0.COUNTRY_ID",
                "T_NLS j2 ON j2.TABLE_NAME = #{0,javaType=java.lang.String} AND j2.COLUMN_NAME = #{1,javaType=java.lang.String} AND j2.LANGUAGE_CODE = #{2,javaType=java.lang.String} AND j2.TABLE_ID = j1.ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("NVL(j2.MEANING, j1.NAME) DESC");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "T_COUNTRY").containsEntry("1", "NAME").containsEntry("2", "fra");
    }

    @Test
    public void testComplexeSort5() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-address.country.name,-address2.city,+address.city"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ADDRESS j0 ON j0.ID = t.ADDRESS_ID", "T_COUNTRY j1 ON j1.ID = j0.COUNTRY_ID",
                "T_NLS j2 ON j2.TABLE_NAME = #{0,javaType=java.lang.String} AND j2.COLUMN_NAME = #{1,javaType=java.lang.String} AND j2.LANGUAGE_CODE = #{2,javaType=java.lang.String} AND j2.TABLE_ID = j1.ID",
                "T_ASSO_PERSON_ADDRESS j3_0 ON j3_0.PERSON_ID = t.ID", "T_ADDRESS j3 ON j3.ID = j3_0.ADDRESS_ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class)
                .containsExactly(SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("NVL(j2.MEANING, j1.NAME) DESC, j3.CITY DESC, j0.CITY ASC");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "T_COUNTRY").containsEntry("1", "NAME").containsEntry("2", "fra");
    }
}
