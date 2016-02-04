package com.talanlabs.mybatis.rsql.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.rsql.engine.EngineContext;

import java.util.Map;

public interface IPageStatementFactory {

    /**
     * Build page statement
     *
     * @param componentClass       current component
     * @param sqlFromWhereOrderBy  current statement without select
     * @param rows                 current rows
     * @param additionalParameters add parameters here
     * @param context              engine context
     * @return a sql statement
     */
    <E extends IComponent> String buildPageSql(Class<E> componentClass, String sqlFromWhereOrderBy, Request.Rows rows, Map<String, Object> additionalParameters, EngineContext context);

}
