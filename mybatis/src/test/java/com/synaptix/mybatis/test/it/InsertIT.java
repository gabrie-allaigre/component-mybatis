package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.CountryBuilder;
import com.synaptix.mybatis.test.data.ICountry;
import com.synaptix.mybatis.test.data.IUser;
import com.synaptix.mybatis.test.data.UserBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class InsertIT extends AbstractIntegration {

    @Test
    public void testInsert() {
        IUser user = UserBuilder.newBuilder().login("Gabriel").build();
        int i = sqlSessionManager.insert(StatementNameHelper.buildInsertKey(IUser.class), user);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(user.getId()).isNotNull();
        Assertions.assertThat(user.getVersion()).isEqualTo(0);
    }

    @Test
    public void testInsertComponent() {
        IUser user = UserBuilder.newBuilder().login("Gabriel").build();
        int i = componentSqlSessionManager.insert(user);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(user.getId()).isNotNull();
        Assertions.assertThat(user.getVersion()).isEqualTo(0);
    }

    @Test
    public void testInsertNlsColumn() {
        ICountry country = CountryBuilder.newBuilder().code("FRA").name("France").build();
        int i = componentSqlSessionManager.insert(country);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(country.getId()).isNotNull();
        Assertions.assertThat(country.getVersion()).isEqualTo(0);

        ICountry country2 = componentSqlSessionManager.findById(ICountry.class,country.getId());
        Assertions.assertThat(country2.getId()).isEqualTo(country.getId());
        Assertions.assertThat(country2.getVersion()).isEqualTo(country.getVersion());
        Assertions.assertThat(country2.getCode()).isEqualTo(country.getCode());
        Assertions.assertThat(country2.getName()).isEqualTo(country.getName());
    }
}
