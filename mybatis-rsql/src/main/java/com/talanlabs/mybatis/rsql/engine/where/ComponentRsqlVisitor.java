package com.talanlabs.mybatis.rsql.engine.where;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IllegalPropertyException;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.where.registry.IComparisonOperatorManagerRegistry;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

public class ComponentRsqlVisitor<E extends IComponent> implements RSQLVisitor<SqlResult, EngineContext> {

    private final ComponentDescriptor<E> componentDescriptor;
    private final IComparisonOperatorManagerRegistry comparisonOperatorManagerRegistry;

    public ComponentRsqlVisitor(Class<E> componentClass, IComparisonOperatorManagerRegistry comparisonOperatorManagerRegistry) {
        super();

        this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        this.comparisonOperatorManagerRegistry = comparisonOperatorManagerRegistry;
    }

    @Override
    public SqlResult visit(AndNode node, EngineContext context) {
        return node.getChildren().stream().map(n -> n.accept(this, context)).collect(SqlResult.SqlResultJoiner.joining(" AND ", "(", ")"));
    }

    @Override
    public SqlResult visit(OrNode node, EngineContext context) {
        return node.getChildren().stream().map(n -> n.accept(this, context)).collect(SqlResult.SqlResultJoiner.joining(" OR ", "(", ")"));
    }

    @Override
    public SqlResult visit(ComparisonNode node, EngineContext context) {
        IComparisonOperatorManager comparisonOperatorManager = comparisonOperatorManagerRegistry.getComparisonOperatorManager(node.getOperator());
        if (comparisonOperatorManager != null) {
            return comparisonOperatorManager.visit(componentDescriptor, node, context);
        }
        throw new IllegalPropertyException(String.format("Property %s with operator %s not accepted", node.getSelector(), node.getOperator()));
    }
}