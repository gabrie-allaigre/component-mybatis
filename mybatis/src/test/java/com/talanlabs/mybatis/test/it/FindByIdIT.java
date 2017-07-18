package com.talanlabs.mybatis.test.it;

import com.talanlabs.entity.factory.IdFactory;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.test.data.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class FindByIdIT extends AbstractHSQLIntegration {

    @Test
    public void testFindUserById() {
        defaultNlsColumnHandler.setLanguageCode(null);
        IUser user = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(user.getId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(user.getVersion()).isEqualTo(0);
        softAssertions.assertThat(user.getCountryCode()).isEqualTo("FRA");
        softAssertions.assertThat(user.getCountryId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(user.getAddressId()).isEqualTo(IdFactory.IdString.from("2"));
        softAssertions.assertThat(user.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();
    }

    @Test
    public void testAssociation() {
        defaultNlsColumnHandler.setLanguageCode(null);
        IUser user = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));

        IAddress address = user.getAddress();

        Assertions.assertThat(address).isNotNull();
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(address.getId()).isEqualTo(IdFactory.IdString.from("2"));
        softAssertions.assertThat(address.getVersion()).isEqualTo(0);
        softAssertions.assertThat(address.getCity()).isEqualTo("Valence");
        softAssertions.assertThat(address.getCountryId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(address.getPostalZip()).isEqualTo("26000");
        softAssertions.assertThat(address.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();

        ICountry country = address.getCountry();
        Assertions.assertThat(country).isNotNull();
        softAssertions.assertThat(country.getId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(country.getVersion()).isEqualTo(0);
        softAssertions.assertThat(country.getCode()).isEqualTo("FRA");
        softAssertions.assertThat(country.getName()).isEqualTo("france");
        softAssertions.assertThat(country.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();
    }

    @Test
    public void testCollection() {
        IUser user = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));

        Assertions.assertThat(user.getGroups()).isNotNull().hasSize(2);

        Assertions.assertThat(user.getAddresses()).isNotNull().hasSize(3);
    }

    @Test
    public void testNlsColumn() {
        defaultNlsColumnHandler.setLanguageCode("fra");
        ICountry country = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ICountry.class), IdFactory.IdString.from("1"));

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(country.getId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(country.getVersion()).isEqualTo(0);
        softAssertions.assertThat(country.getCode()).isEqualTo("FRA");
        softAssertions.assertThat(country.getName()).isEqualTo("Fromage");
        softAssertions.assertThat(country.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();
    }

    @Test
    public void testFindUserComponentById() {
        defaultNlsColumnHandler.setLanguageCode(null);
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(user.getId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(user.getVersion()).isEqualTo(0);
        softAssertions.assertThat(user.getCountryCode()).isEqualTo("FRA");
        softAssertions.assertThat(user.getCountryId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(user.getAddressId()).isEqualTo(IdFactory.IdString.from("2"));
        softAssertions.assertThat(user.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();
    }

    @Test
    public void testOrderBy() {
        ITrain train = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ITrain.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(train.getWagons()).isNotNull().hasSize(5).extracting("position").containsExactly(5, 1, 2, 4, 3);
        Assertions.assertThat(train.getWagons()).isNotNull().hasSize(5).extracting("code").containsSequence("000000000001", "000000000002", "000000000003", "000000000004", "000000000005");

        ITrain2 train2 = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ITrain2.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(train2.getWagons()).isNotNull().hasSize(5).extracting("position").containsExactly(1, 2, 3, 4, 5);
        Assertions.assertThat(train2.getWagons()).isNotNull().hasSize(5).extracting("code").containsExactly("000000000002", "000000000003", "000000000005", "000000000004", "000000000001");

        ITrain3 train3 = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ITrain3.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(train3.getWagons()).isNotNull().hasSize(5).extracting("position").containsExactly(5, 4, 3, 2, 1);
        Assertions.assertThat(train3.getWagons()).isNotNull().hasSize(5).extracting("code").containsExactly("000000000001", "000000000004", "000000000005", "000000000003", "000000000002");

        ITrain4 train4 = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ITrain4.class), IdFactory.IdString.from("2"));
        Assertions.assertThat(train4.getWagons()).isNotNull().hasSize(5).extracting("position").containsExactly(1, 2, 3, 3, 3);
        Assertions.assertThat(train4.getWagons()).isNotNull().hasSize(5).extracting("code").containsExactly("000000000001", "000000000002", "000000000001", "000000000003", "000000000004");
    }

    @Test
    public void testTypeHandlerCustom() {
        defaultNlsColumnHandler.setLanguageCode("fra");
        ICountry2 country = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ICountry2.class), IdFactory.IdString.from("1"));

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(country.getId()).isEqualTo(IdFactory.IdString.from("1"));
        softAssertions.assertThat(country.getVersion()).isEqualTo(0);
        softAssertions.assertThat(country.getCode()).isEqualTo("test");
        softAssertions.assertThat(country.getName()).isEqualTo("Fromage");
        softAssertions.assertThat(country.getCreatedBy()).isEqualTo("GABY");
        softAssertions.assertAll();
    }

    @Test
    public void test2() {
        defaultNlsColumnHandler.setLanguageCode("fra");
        IWagon wagon = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IWagon.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(wagon).isNotNull();
        Assertions.assertThat(wagon.getContainers()).isNotNull().hasSize(3);
    }
}
