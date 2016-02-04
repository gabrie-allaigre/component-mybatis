package com.talanlabs.mybatis.rsql.database;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.statement.IPageStatementFactory;
import com.talanlabs.mybatis.rsql.statement.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class StandardHandler implements IPageStatementFactory {

    @Override
    public <E extends IComponent> String buildPageSql(Class<E> componentClass, String sqlFromWhereOrderBy, Request.Rows rows, Map<String, Object> additionalParameters, EngineContext context) {
        String sql = "SELECT " + (StringUtils.isNotBlank(context.getDefaultTableName()) ? context.getDefaultTableName() + "." : "") + "* " + sqlFromWhereOrderBy;
        String limitParam = context.getNewParamName();
        String offsetParam = context.getNewParamName();
        additionalParameters.put(limitParam, rows.limit);
        additionalParameters.put(offsetParam, rows.offset);
        sql += " LIMIT #{" + limitParam + ",javaType=long} OFFSET #{" + offsetParam + ",javaType=long}";
        return sql;
    }
}
