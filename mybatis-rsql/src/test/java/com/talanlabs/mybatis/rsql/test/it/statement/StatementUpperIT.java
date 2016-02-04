package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.CountryFields;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StatementUpperIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();

        beforeClass();
    }

    @Test
    public void testSimpleFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==fra");
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("FRA");
    }

    @Test
    public void testNlsFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "name==FROMAGE");
        Assertions.assertThat(countries).isNotNull().hasSize(1);
    }
}
