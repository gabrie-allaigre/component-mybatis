package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

public class StatementCountIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        beforeClass();
    }

    @Test
    public void testSimpleFindCode() {
        Integer count = sqlSessionManager.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), "code==FRA");
        Assertions.assertThat(count).isEqualTo(1);
    }

    @Test
    public void testSimpleNotFindCode() {
        Integer count = sqlSessionManager.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), "code==TOTO");
        Assertions.assertThat(count).isEqualTo(0);
    }

    @Test
    public void testSimple() {
        Integer count = sqlSessionManager.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class));
        Assertions.assertThat(count).isEqualTo(7);
    }
}
