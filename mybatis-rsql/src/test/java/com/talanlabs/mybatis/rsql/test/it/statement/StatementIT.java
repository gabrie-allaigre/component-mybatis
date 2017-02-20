package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.CountryFields;
import com.talanlabs.mybatis.rsql.test.data.IAddress;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StatementIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        beforeClass();
    }

    @Test
    public void testSimple() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class));
        Assertions.assertThat(countries).isNotNull().hasSize(7);
    }

    @Test
    public void testSimpleFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==FRA");
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("FRA");
    }

    @Test
    public void testSimpleNotFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==TOTO");
        Assertions.assertThat(countries).isNotNull().hasSize(0);
    }

    @Test
    public void testSimpleLikeFindCode() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==F*");
        Assertions.assertThat(countries).isNotNull().hasSize(2);
    }

    @Test
    public void testComplexeLikeFindCode1() {
        List<IAddress> addresses = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IAddress.class), "postalZip==78*");
        Assertions.assertThat(addresses).isNotNull().hasSize(2);
    }

    @Test
    public void testComplexeLikeFindCode2() {
        List<IAddress> addresses = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IAddress.class), "postalZip==78*;city==Versailles");
        Assertions.assertThat(addresses).isNotNull().hasSize(1);
    }

    @Test
    public void testComplexeLikeFindCode3() {
        List<IAddress> addresses = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IAddress.class), "postalZip==78*,city==Valence");
        Assertions.assertThat(addresses).isNotNull().hasSize(3);
    }

    @Test
    public void testSimpleGreater() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "age>30");
        Assertions.assertThat(persons).isNotNull().hasSize(3);
    }

    @Test
    public void testSimpleLower1() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "age<4");
        Assertions.assertThat(persons).isNotNull().hasSize(1);
    }

    @Test
    public void testSimpleLower2() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "age<=4");
        Assertions.assertThat(persons).isNotNull().hasSize(2);
    }

    @Test
    public void testSimpleGreaterLower() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "age>=36;age<37");
        Assertions.assertThat(persons).isNotNull().hasSize(1);
    }

    @Test
    public void testSimpleLower3() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "birthday>=2000-01-01");
        Assertions.assertThat(persons).isNotNull().hasSize(2);
    }

    @Test
    public void testSimpleIn() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "firstName=in=(Gabriel,Sandra)");
        Assertions.assertThat(persons).isNotNull().hasSize(2);
    }

    @Test
    public void testSimpleOut() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "firstName=nin=(Gabriel,Sandra,Laureline)");
        Assertions.assertThat(persons).isNotNull().hasSize(2);
    }

    @Test
    public void testJoinSimple() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "address.city==Versailles");
        Assertions.assertThat(persons).isNotNull().hasSize(2);
    }

    @Test
    public void testJoinSimple2() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "address.city==Versailles,address.city==Valence");
        Assertions.assertThat(persons).isNotNull().hasSize(3);
    }

    @Test
    public void testJoinDouble() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "addressBis.city==Versailles");
        Assertions.assertThat(persons).isNotNull().hasSize(0);
    }

    @Test
    public void testJoinMultiple() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "address2.city==Versailles");
        Assertions.assertThat(persons).isNotNull().hasSize(0);
    }

    @Test
    public void testNlsSimple1() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "name==Fromage");
        Assertions.assertThat(countries).isNotNull().hasSize(1);
    }

    @Test
    public void testNlsSimple2() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "name==chine");
        Assertions.assertThat(countries).isNotNull().hasSize(1);
    }

    @Test
    public void testNlsSimple3() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "name==e*,name==U*");
        Assertions.assertThat(countries).isNotNull().hasSize(3);
    }

    @Test
    public void testNlsSimple4() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "name==Fromage,name==chine");
        Assertions.assertThat(countries).isNotNull().hasSize(2);
    }
}
