package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.Request;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import com.talanlabs.mybatis.rsql.test.data.PersonFields;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StatementCustomSortIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();
        beforeClass();
    }

    @Test
    public void testSortCode() {
        List<IPerson> persons = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class),
                Request.newBuilder().customSortLeft(this::buildCustomSortSqlResultLeft1).sort("lastName").customSortRight(this::buildCustomSortSqlResultRight1).build());
        Assertions.assertThat(persons).isNotNull().hasSize(5).extracting(PersonFields.firstName).containsSequence("David", "Gabriel", "Laureline", "Raphael", "Sandra");
    }

    private SqlResult buildCustomSortSqlResultLeft1(EngineContext context) {
        return SqlResult.of(Collections.emptyList(), "FIRST_NAME", new HashMap<>());
    }

    private SqlResult buildCustomSortSqlResultRight1(EngineContext context) {
        return SqlResult.of(Collections.emptyList(), "AGE", new HashMap<>());
    }
}
