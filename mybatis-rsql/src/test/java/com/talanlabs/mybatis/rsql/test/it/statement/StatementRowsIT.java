package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.Request;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StatementRowsIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();

        beforeClass();
    }

    @Test
    public void testSimpleRows1() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rows(Request.Rows.of(0, 2)).build());
        Assertions.assertThat(countries).isNotNull().hasSize(2);
    }

    @Test
    public void testSimpleRows2() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().sort("code").rows(Request.Rows.of(0, 3)).build());
        Assertions.assertThat(countries).isNotNull().hasSize(3).extracting("code").containsExactly("CHI", "ENG", "ESP");
    }

    @Test
    public void testSimpleRows3() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().sort("code").rows(Request.Rows.of(1, 3)).build());
        Assertions.assertThat(countries).isNotNull().hasSize(3).extracting("code").containsExactly("ENG", "ESP", "FRA");
    }

    @Test
    public void testSimpleRows4() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().sort("-code").rows(Request.Rows.of(1, 3)).build());
        Assertions.assertThat(countries).isNotNull().hasSize(3).extracting("code").containsExactly("ITA", "FRA", "ESP");
    }
}
