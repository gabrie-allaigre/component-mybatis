package com.synaptix.mybatis.test.it;

import com.synaptix.entity.factory.IdFactory;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DeleteIT extends AbstractIntegration {

    @Test
    public void testDelete() {
        IUser user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        int i = componentSqlSessionManager.delete(user);

        Assertions.assertThat(i).isEqualTo(1);

        user = componentSqlSessionManager.findById(IUser.class, IdFactory.IdString.from("1"));
        Assertions.assertThat(user).isNull();
    }
}
