package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.component.statement.StatementNameHelper;
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
}
