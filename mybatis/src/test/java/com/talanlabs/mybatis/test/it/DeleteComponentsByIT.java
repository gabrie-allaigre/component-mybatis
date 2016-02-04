package com.talanlabs.mybatis.test.it;

import com.talanlabs.entity.factory.IdFactory;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.test.data.IUser;
import com.talanlabs.mybatis.test.data.UserFields;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeleteComponentsByIT extends AbstractHSQLIntegration {

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
