package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import com.synaptix.mybatis.test.data.UserFields;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeleteComponentsByIT extends AbstractIntegration {

    @Test
    public void testDelete() {
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNotNull();

        int i = sqlSessionManager.delete(StatementNameHelper.buildDeleteComponentsByKey(IUser.class, UserFields.login), user.getLogin());
        Assertions.assertThat(i).isEqualTo(1);

        user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNull();
    }
}
