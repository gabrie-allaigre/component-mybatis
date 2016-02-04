package com.talanlabs.mybatis.rsql.database;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.statement.IPageStatementFactory;
import com.talanlabs.mybatis.rsql.statement.Request;

import java.util.Map;

public class OracleHandler implements IPageStatementFactory {

    @Override
    public <E extends IComponent> String buildPageSql(Class<E> componentClass, String sqlFromWhereOrderBy, Request.Rows rows, Map<String, Object> additionalParameters, EngineContext context) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);
        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(cd);

        long first = rows.offset + 1;
        long last = rows.offset + 1 + rows.limit;

        String firstParam = context.getNewParamName();
        String lastParam = context.getNewParamName();
        additionalParameters.put(firstParam, first);
        additionalParameters.put(lastParam, last);

        return "SELECT i.rn, t.* " + "FROM (SELECT i.* " + "FROM (SELECT i.*, ROWNUM AS rn " + "FROM (SELECT ROWID AS a_rowid " + sqlFromWhereOrderBy + ") i " + "WHERE ROWNUM <= #{" + lastParam
                + ",javaType=long} " + ") i " + "WHERE rn >= #{" + firstParam + ",javaType=long} " + ") i " + ", " + entity.name() + " t " + "WHERE i.a_rowid = t.ROWID " + "ORDER BY rn";
    }
}
