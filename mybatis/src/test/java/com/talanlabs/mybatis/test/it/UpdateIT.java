package com.talanlabs.mybatis.test.it;

import com.talanlabs.entity.factory.IdFactory;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.test.data.CountryBuilder;
import com.talanlabs.mybatis.test.data.ICountry;
import com.talanlabs.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class UpdateIT extends AbstractHSQLIntegration {

    @Test
    public void testUpdate() {
        IUser user = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("2"));
        user.setLogin("Toto");
        int i = sqlSessionManager.update(StatementNameHelper.buildUpdateKey(IUser.class), user);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(user.getVersion()).isEqualTo(1);

        user = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("2"));
        Assertions.assertThat(user.getLogin()).isEqualTo("Toto");
    }

    @Test
    public void testUpdateComponent() {
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        user.setLogin("Toto");
        int i = componentSqlSessionManager.update(user);

        sqlSessionManager.commit();

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(user.getVersion()).isEqualTo(1);

        user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user.getLogin()).isEqualTo("Toto");
    }

    @Test
    public void testUpdateNlsColumn() {
        ICountry country = componentSqlSessionManager.findById(ICountry.class, IdFactory.IdString.from("1"));
        country.setName("Test");
        int i = componentSqlSessionManager.update(country);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(country.getId()).isNotNull();
        Assertions.assertThat(country.getVersion()).isEqualTo(1);

        ICountry country2 = componentSqlSessionManager.findById(ICountry.class, country.getId());
        Assertions.assertThat(country2.getId()).isEqualTo(country.getId());
        Assertions.assertThat(country2.getVersion()).isEqualTo(country.getVersion());
        Assertions.assertThat(country2.getCode()).isEqualTo(country.getCode());
        Assertions.assertThat(country2.getName()).isEqualTo(country.getName());
    }

    @Test
    public void testUpdateNlsColumn2() {
        defaultNlsColumnHandler.setLanguageCode("fra");

        ICountry country = CountryBuilder.newBuilder().code("FRA").name("France").build();
        int i = componentSqlSessionManager.insert(country);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(country.getId()).isNotNull();

        ICountry country1 = componentSqlSessionManager.findById(ICountry.class, country.getId());
        country1.setName("Test");
        i = componentSqlSessionManager.update(country1);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(componentSqlSessionManager.findById(ICountry.class, country.getId()).getName()).isEqualTo("Test");

        sqlSessionManager.commit();
        sqlSessionManager.clearCache();

        defaultNlsColumnHandler.setLanguageCode("eng");

        ICountry country2 = componentSqlSessionManager.findById(ICountry.class, country.getId());
        Assertions.assertThat(country2.getName()).isEqualTo("France");
        country2.setName("French");
        i = componentSqlSessionManager.update(country2);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(componentSqlSessionManager.findById(ICountry.class, country.getId()).getName()).isEqualTo("French");

        sqlSessionManager.commit();
        sqlSessionManager.clearCache();

        defaultNlsColumnHandler.setLanguageCode("fra");

        Assertions.assertThat(componentSqlSessionManager.findById(ICountry.class, country.getId()).getName()).isEqualTo("Test");

        sqlSessionManager.commit();
        sqlSessionManager.clearCache();

        defaultNlsColumnHandler.setLanguageCode("spa");

        Assertions.assertThat(componentSqlSessionManager.findById(ICountry.class, country.getId()).getName()).isEqualTo("French");

    }
}
