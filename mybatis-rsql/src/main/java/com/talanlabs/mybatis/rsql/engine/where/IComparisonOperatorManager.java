package com.talanlabs.mybatis.rsql.engine.where;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import cz.jirutka.rsql.parser.ast.ComparisonNode;

public interface IComparisonOperatorManager {

    /**
     * Create result for node
     *
     * @param componentDescriptor component descriptor
     * @param node                current node
     * @param context             current context
     * @return a result
     */
    <E extends IComponent> SqlResult visit(ComponentDescriptor<E> componentDescriptor, ComparisonNode node, EngineContext context);

}
