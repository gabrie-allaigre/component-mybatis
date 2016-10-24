package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.ICountry;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DeleteByIdIT extends AbstractIntegration {

    @Test
    public void testDeleteEntityById() {
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNotNull();

        int i = sqlSessionManager.delete(StatementNameHelper.buildDeleteEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(i).isEqualTo(1);

        user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNull();
    }
}
