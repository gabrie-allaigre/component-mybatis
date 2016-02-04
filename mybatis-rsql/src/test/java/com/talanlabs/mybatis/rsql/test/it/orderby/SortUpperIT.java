package com.talanlabs.mybatis.rsql.test.it.orderby;

import com.talanlabs.mybatis.rsql.configuration.RsqlConfigurationBuilder;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.orderby.ComponentSortVisitor;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.DefaultSortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.ISortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.sort.SortParser;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import com.talanlabs.mybatis.rsql.test.it.config.DefaultNlsColumnHandler;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SortUpperIT {

    private static ComponentSortVisitor<IPerson> personComponentSortVisitor;
    private static SortParser sortParser;

    private EngineContext engineContext;

    @BeforeClass
    public static void initGlobal() {
        ISortDirectionManagerRegistry sortDirectionManagerRegistry = new DefaultSortDirectionManagerRegistry(
                RsqlConfigurationBuilder.newBuilder().nlsColumnRsqlHandler(new DefaultNlsColumnHandler()).stringPolicy(new AlwaysUpperStringPolicy()).build());
        personComponentSortVisitor = new ComponentSortVisitor<>(IPerson.class, sortDirectionManagerRegistry);

        sortParser = new SortParser();
    }

    @Before
    public void init() {
        engineContext = EngineContext.newBulder().defaultTablePrefix("t").build();
    }

    @Test
    public void testUpperStringSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("firstName"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).isEmpty();
        Assertions.assertThat(res.sql).isEqualTo("UPPER(t.FIRST_NAME) ASC");
        Assertions.assertThat(res.parameterMap).isEmpty();
    }

    @Test
    public void testUpperSort() {
        SqlResult res = personComponentSortVisitor.visit(sortParser.parse("-address.country.name"), engineContext);

        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.joins).extracting("sql", String.class).containsExactly("T_ADDRESS j0 ON j0.ID = t.ADDRESS_ID", "T_COUNTRY j1 ON j1.ID = j0.COUNTRY_ID",
                "T_NLS j2 ON j2.TABLE_NAME = #{0,javaType=java.lang.String} AND j2.COLUMN_NAME = #{1,javaType=java.lang.String} AND j2.LANGUAGE_CODE = #{2,javaType=java.lang.String} AND j2.TABLE_ID = j1.ID");
        Assertions.assertThat(res.joins).extracting("type", SqlResult.Join.Type.class).containsExactly(SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter, SqlResult.Join.Type.LeftOuter);
        Assertions.assertThat(res.sql).isEqualTo("UPPER(NVL(j2.MEANING, j1.NAME)) DESC");
        Assertions.assertThat(res.parameterMap).containsEntry("0", "T_COUNTRY").containsEntry("1", "NAME").containsEntry("2", "fra");
    }
}
