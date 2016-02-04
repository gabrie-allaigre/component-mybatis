package com.talanlabs.mybatis.rsql.test.it.statement;

import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.policy.AlwaysUpperStringPolicy;
import com.talanlabs.mybatis.rsql.statement.Request;
import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.CountryFields;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementCustomIT extends AbstractHSQLIntegration {

    @BeforeClass
    public static void init() {
        stringComparePolicy = new AlwaysUpperStringPolicy();

        beforeClass();
    }

    @Test
    public void testCustomFindCode1() {
        List<ICountry> countries = sqlSessionManager.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().customRequest(this::buildCustomSqlResult1).build());
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("FRA");
    }

    private SqlResult buildCustomSqlResult1(EngineContext context) {
        return SqlResult.of(Collections.emptyList(), "CODE = 'FRA'", new HashMap<>());
    }

    @Test
    public void testCustomFindCode2() {
        List<ICountry> countries = sqlSessionManager
                .selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("name==Fromage").customRequest(this::buildCustomSqlResult2).build());
        Assertions.assertThat(countries).isNotNull().hasSize(1).extracting(CountryFields.code).containsExactly("FRA");
    }

    private SqlResult buildCustomSqlResult2(EngineContext context) {
        Map<String, Object> parameterMap = new HashMap<>();
        String param1 = context.getNewParamName();
        parameterMap.put(param1, "FRA");
        return SqlResult.of(Collections.emptyList(), "CODE = #{" + param1 + "}", parameterMap);
    }

    @Test
    public void testCustom3() {
        List<IPerson> persons = sqlSessionManager
                .selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), Request.newBuilder().rsql("lastName==All*").customRequest(this::buildCustomSqlResult3).build());
        Assertions.assertThat(persons).isNotNull().hasSize(3);
    }

    private SqlResult buildCustomSqlResult3(EngineContext context) {
        List<SqlResult.Join> joins = new ArrayList<>();
        joins.add(SqlResult.Join.of(SqlResult.Join.Type.Inner, "T_ADDRESS a on a.id = t.address_id"));

        Map<String, Object> parameterMap = new HashMap<>();
        String param1 = context.getNewParamName();
        parameterMap.put(param1, "Versailles");
        String param2 = context.getNewParamName();
        parameterMap.put(param2, "Valence");
        return SqlResult.of(joins, "a.CITY = #{" + param1 + "}" + " OR exists (select * from t_address b where b.id = t.address_id and b.city = #{" + param2 + "})", parameterMap);
    }
}
