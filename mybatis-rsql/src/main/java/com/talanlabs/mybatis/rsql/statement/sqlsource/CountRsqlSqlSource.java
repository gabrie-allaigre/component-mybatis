package com.talanlabs.mybatis.rsql.statement.sqlsource;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.statement.Request;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

import java.util.HashMap;
import java.util.Map;

public class CountRsqlSqlSource<E extends IComponent> extends AbstractRsqlSqlSource<E> {

    public CountRsqlSqlSource(ComponentConfiguration componentConfiguration, IRsqlConfiguration rsqlConfiguration, Class<E> componentClass) {
        super(componentConfiguration, rsqlConfiguration, componentClass);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        String rsql;
        Request.ICustomRequest customRequest = null;
        String sort = null;
        Request.ICustomSort customSortLeft = null;
        Request.ICustomSort customSortRight = null;
        if (parameterObject instanceof Request) {
            Request request = (Request) parameterObject;
            rsql = request.getRsql();
            customRequest = request.getCustomRequest();
            sort = request.getSort();
            customSortLeft = request.getCustomSortLeft();
            customSortRight = request.getCustomSortRight();
        } else {
            rsql = (String) parameterObject;
        }

        Map<String, Object> additionalParameters = new HashMap<>();
        EngineContext context = rsqlConfiguration.newEngineContext();

        String sqlFromWhereOrderBy = buildSqlFromWhereOrderBy(rsql, customRequest, sort, customSortLeft, customSortRight, additionalParameters, context);

        SqlSource sqlSource = sqlSourceParser.parse("SELECT count(0) " + sqlFromWhereOrderBy, String.class, additionalParameters);
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        additionalParameters.entrySet().forEach(e -> boundSql.setAdditionalParameter(e.getKey(), e.getValue()));
        return boundSql;
    }
}
