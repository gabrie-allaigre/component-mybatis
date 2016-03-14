package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.test.data.ICountry;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DeleteIT extends AbstractIntegration {

    @Test
    public void testDelete() {
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        int i = componentSqlSessionManager.delete(user);

        Assertions.assertThat(i).isEqualTo(1);

        user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNull();
    }

    @Test
    public void testDeleteNls() {
        ICountry country = componentSqlSessionManager.findById(ICountry.class, IdFactory.IdString.from("1"));
        int i = componentSqlSessionManager.delete(country);

        Assertions.assertThat(i).isEqualTo(1);

        country = componentSqlSessionManager.findById(ICountry.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(country).isNull();

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("tableName", "T_COUNTRY");
        parameter.put("id", IdFactory.IdString.from("1"));
        Assertions.assertThat(sqlSessionManager.<Integer>selectOne("com.synaptix.mybatis.test.it.mapper.NlsMapper.countNlsColumns", parameter)).isEqualTo(0);
    }
}
