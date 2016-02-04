package com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.sort.SortDirection;

public interface ISortResultFactory {

    /**
     * @param componentDescriptor component descriptor
     * @param propertyDescriptor  current property
     * @return true, if factory build with property
     */
    boolean acceptProperty(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

    /**
     * @param sortResultContext    rsql result context
     * @param componentDescriptor  component descriptor
     * @param propertyDescriptor   current property
     * @param sortDirection        current sortDirection
     * @param previousPropertyName last property name
     * @param nextPropertyName     next property name
     * @param tableJoinName        current table prefix
     * @param context              context
     * @return a component rsql result
     */
    SqlResult buildComponentSortResult(ISortResultContext sortResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            SortDirection sortDirection, String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context);

    interface ISortResultContext {

        SqlResult visit(ComponentDescriptor<?> componentDescriptor, SortDirection sortDirection, String previousPropertyName, String selector, String tablePrefix, EngineContext context);

    }
}
