package com.talanlabs.mybatis.test.it;

import com.talanlabs.entity.factory.IdFactory;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeleteByIdIT extends AbstractHSQLIntegration {

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
