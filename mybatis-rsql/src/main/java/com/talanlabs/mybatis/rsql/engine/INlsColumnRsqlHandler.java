package com.talanlabs.mybatis.rsql.engine;

import com.talanlabs.component.IComponent;

public interface INlsColumnRsqlHandler {

    /**
     * @param componentClass   component class
     * @param propertyName     property name
     * @param fullPropertyName complete property name
     * @param tableJoinName    jointure name
     * @param context          a rsql context
     * @return Build a sql name result for where
     */
    SqlResult buildNameResultForWhere(Class<? extends IComponent> componentClass, String propertyName, String fullPropertyName, String tableJoinName, EngineContext context);

    /**
     * @param componentClass   component class
     * @param propertyName     property name
     * @param fullPropertyName complete property name
     * @param tableJoinName    jointure name
     * @param context          a rsql context
     * @return Build a sql name result for order by
     */
    SqlResult buildNameResultForOrderBy(Class<? extends IComponent> componentClass, String propertyName, String fullPropertyName, String tableJoinName, EngineContext context);

}
