package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeleteIT extends AbstractIntegration {

    @Test
    public void testDelete() {
        IUser user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));
        int i = sqlSessionManager.delete(StatementNameHelper.buildDeleteKey(IUser.class), user);

        Assertions.assertThat(i).isEqualTo(1);

        user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNull();
    }
}
