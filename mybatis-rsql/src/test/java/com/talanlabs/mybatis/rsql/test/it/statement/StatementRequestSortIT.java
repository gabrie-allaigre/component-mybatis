package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.Request;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.AddressFields;
import com.talanlabs.mybatis.rsql.test.data.CountryFields;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import com.talanlabs.mybatis.rsql.test.data.PersonFields;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StatementRequestSortIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();
        beforeClass();
    }

    @Test
    public void testRequestSortCode1() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("-code").build());
        Assertions.assertThat(countries).isNotNull().hasSize(2).extracting(CountryFields.code).containsSequence("ESP", "ENG");
    }

    @Test
    public void testRequestSortCode2() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("+code").build());
        Assertions.assertThat(countries).isNotNull().hasSize(2).extracting(CountryFields.code).containsSequence("ENG", "ESP");
    }

    @Test
    public void testRequestSortCode3() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==*S*").sort("-name").build());
        Assertions.assertThat(countries).isNotNull().hasSize(2).extracting(CountryFields.code).containsSequence("USA", "ESP");
    }

    @Test
    public void testComplexeRequestSort1() {
        List<IPerson> persons = sqlSessionManager
                .selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), Request.newBuilder().rsql("firstName==Gabriel,firstName==Ra*").sort("address.city").build());
        Assertions.assertThat(persons).isNotNull().hasSize(2).extracting(PersonFields.address).extracting(AddressFields.city).containsSequence("London", "Versailles");
    }

    @Test
    public void testComplexeSort2() {
        List<IPerson> persons = sqlSessionManager
                .selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), Request.newBuilder().rsql("firstName==Gabriel,firstName==Ra*").sort("-address.city").build());
        Assertions.assertThat(persons).isNotNull().hasSize(2).extracting(PersonFields.address).extracting(AddressFields.city).containsSequence("Versailles", "London");
    }
}
