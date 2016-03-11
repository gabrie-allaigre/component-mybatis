package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class UpdateIT extends AbstractIntegration {

    @Test
    public void testUpdate() {
        IUser user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("2"));
        user.setLogin("Toto");
        int i = sqlSessionManager.update(StatementNameHelper.buildUpdateKey(IUser.class), user);

        Assertions.assertThat(i).isEqualTo(1);
        Assertions.assertThat(user.getVersion()).isEqualTo(1);

        user = sqlSessionManager.<IUser>selectOne(StatementNameHelper.buildFindEntityByIdKey(IUser.class), IdFactory.IdString.from("2"));
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
}
