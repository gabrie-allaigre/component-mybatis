package com.talanlabs.mybatis.rsql.engine.where.comparisons;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IllegalPropertyException;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.where.IComparisonOperatorManager;
import com.talanlabs.mybatis.rsql.engine.where.comparisons.factory.AssociationRsqlResultFactory;
import com.talanlabs.mybatis.rsql.engine.where.comparisons.factory.ColumnRsqlResultFactory;
import com.talanlabs.mybatis.rsql.engine.where.comparisons.factory.IRsqlResultFactory;
import com.talanlabs.mybatis.rsql.engine.where.comparisons.factory.NlsColumnRsqlResultFactory;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StandardComparisonOperatorManager implements IComparisonOperatorManager, IRsqlResultFactory.IRsqlResultContext {

    private List<IRsqlResultFactory> rsqlResultFactories = new ArrayList<>();

    public StandardComparisonOperatorManager(IRsqlConfiguration configuration) {
        super();

        addRsqlResultFactory(new ColumnRsqlResultFactory(configuration));
        addRsqlResultFactory(new NlsColumnRsqlResultFactory(configuration));
        addRsqlResultFactory(new AssociationRsqlResultFactory());
    }

    public void addRsqlResultFactory(IRsqlResultFactory rsqlResultFactory) {
        rsqlResultFactories.add(0, rsqlResultFactory);
    }

    @Override
    public <E extends IComponent> SqlResult visit(ComponentDescriptor<E> componentDescriptor, ComparisonNode node, EngineContext context) {
        return visit(componentDescriptor, node, null, node.getSelector(), context.getDefaultTableName(), context);
    }

    @Override
    public SqlResult visit(ComponentDescriptor<?> componentDescriptor, ComparisonNode node, String previousPropertyName, String selector, String tableJoinName, EngineContext context) {
        int indexPoint = selector.indexOf(".");
        String propertyName;
        String nextPropertyName;
        if (indexPoint == -1) {
            propertyName = selector;
            nextPropertyName = null;
        } else {
            propertyName = selector.substring(0, indexPoint);
            nextPropertyName = selector.substring(indexPoint + 1);
        }

        String current = (StringUtils.isNotBlank(previousPropertyName) ? previousPropertyName + "." : "") + propertyName;

        ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);
        if (propertyDescriptor == null) {
            throw new IllegalPropertyException(String.format("Property %s not exists", current));
        }

        for (IRsqlResultFactory rsqlResultFactory : rsqlResultFactories) {
            if (rsqlResultFactory.acceptProperty(componentDescriptor, propertyDescriptor)) {
                return rsqlResultFactory.buildComponentRsqlResult(this, componentDescriptor, propertyDescriptor, node, previousPropertyName, nextPropertyName, tableJoinName, context);
            }
        }

        throw new IllegalPropertyException(String.format("Property %s not accepted", current));
    }
}
