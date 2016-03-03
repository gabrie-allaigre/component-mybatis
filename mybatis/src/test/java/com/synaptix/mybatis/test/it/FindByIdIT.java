package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IAddress;
import com.synaptix.mybatis.test.data.ICountry;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class FindByIdIT extends AbstractIntegration {

    @Test
    public void testFindUserById() {
        IUser user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));

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
        IUser user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));

        IAddress address = user.getAddress();;

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
}
