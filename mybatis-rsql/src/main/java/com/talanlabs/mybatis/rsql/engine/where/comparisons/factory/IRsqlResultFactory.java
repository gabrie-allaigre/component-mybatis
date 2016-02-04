package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import cz.jirutka.rsql.parser.ast.ComparisonNode;

public interface IRsqlResultFactory {

    /**
     * @param componentDescriptor component descriptor
     * @param propertyDescriptor  current property
     * @return true, if factory build with property
     */
    boolean acceptProperty(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

    /**
     * @param rsqlResultContext    rsql result context
     * @param componentDescriptor  component descriptor
     * @param propertyDescriptor   current property
     * @param node                 current node
     * @param previousPropertyName last property name
     * @param nextPropertyName     next property name
     * @param tableJoinName        current table prefix
     * @param context              context
     * @return a component rsql result
     */
    SqlResult buildComponentRsqlResult(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, ComparisonNode node,
            String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context);

    interface IRsqlResultContext {

        SqlResult visit(ComponentDescriptor<?> componentDescriptor, ComparisonNode node, String previousPropertyName, String selector, String tablePrefix, EngineContext context);

    }
}
