package com.talanlabs.mybatis.rsql.engine.orderby;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.sort.SortNode;

public interface ISortDirectionManager {

    /**
     * Create result for direction
     *
     * @param componentDescriptor component descriptor
     * @param node                current node
     * @param context             current context
     * @return a result
     */
    <E extends IComponent> SqlResult visit(ComponentDescriptor<E> componentDescriptor, SortNode node, EngineContext context);

}
