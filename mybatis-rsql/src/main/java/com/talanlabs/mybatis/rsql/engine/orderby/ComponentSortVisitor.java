package com.talanlabs.mybatis.rsql.engine.orderby;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IllegalPropertyException;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.orderby.registry.ISortDirectionManagerRegistry;
import com.talanlabs.mybatis.rsql.sort.SortNode;
import com.talanlabs.mybatis.rsql.sort.SortVisitor;

import java.util.List;

public class ComponentSortVisitor<E extends IComponent> implements SortVisitor<SqlResult, EngineContext> {

    private final ComponentDescriptor<E> componentDescriptor;
    private final ISortDirectionManagerRegistry sortDirectionManagerRegistry;

    public ComponentSortVisitor(Class<E> componentClass, ISortDirectionManagerRegistry sortDirectionManagerRegistry) {
        super();

        this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        this.sortDirectionManagerRegistry = sortDirectionManagerRegistry;
    }

    @Override
    public SqlResult visit(List<SortNode> nodes, EngineContext context) {
        return nodes.stream().map(n -> visit(n, context)).collect(SqlResult.SqlResultJoiner.joining(", ", "", ""));
    }

    @Override
    public SqlResult visit(SortNode node, EngineContext context) {
        ISortDirectionManager sortDirectionManager = sortDirectionManagerRegistry.getSortDirectionManager(node.getDirection());
        if (sortDirectionManager != null) {
            return sortDirectionManager.visit(componentDescriptor, node, context);
        }
        throw new IllegalPropertyException(String.format("Property %s with operator %s not accepted", node.getSelector(), node.getDirection()));
    }
}