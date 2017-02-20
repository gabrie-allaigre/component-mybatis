package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.Request;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.CountryFields;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StatementRequestIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();

        beforeClass();
    }

    @Test
    public void testSimple() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().build());
        Assertions.assertThat(countries).isNotNull().hasSize(7);
    }

    @Test
    public void testSimpleFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==FRA").build());
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("FRA");
    }

    @Test
    public void testLikeFind1Code() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==F\\\\%*").build());
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("F%");
    }

    @Test
    public void testLike2FindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==F_*").build());
        Assertions.assertThat(countries).isNotNull().hasSize(2).extracting(CountryFields.code).containsExactly("FRA", "F%");
    }

    @Test
    public void testLike3FindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==F\\\\_*").build());
        Assertions.assertThat(countries).isNotNull().hasSize(0);
    }
}
