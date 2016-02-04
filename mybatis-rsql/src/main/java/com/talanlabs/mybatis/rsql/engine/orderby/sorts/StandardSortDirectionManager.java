package com.talanlabs.mybatis.rsql.engine.orderby.sorts;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IllegalPropertyException;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.orderby.ISortDirectionManager;
import com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory.AssociationSortResultFactory;
import com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory.ColumnSortResultFactory;
import com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory.ISortResultFactory;
import com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory.NlsColumnSortResultFactory;
import com.talanlabs.mybatis.rsql.sort.SortDirection;
import com.talanlabs.mybatis.rsql.sort.SortNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StandardSortDirectionManager implements ISortDirectionManager, ISortResultFactory.ISortResultContext {

    private List<ISortResultFactory> sortResultFactories = new ArrayList<>();

    public StandardSortDirectionManager(IRsqlConfiguration configuration) {
        super();

        addSortResultFactory(new ColumnSortResultFactory(configuration));
        addSortResultFactory(new NlsColumnSortResultFactory(configuration));
        addSortResultFactory(new AssociationSortResultFactory());
    }

    public void addSortResultFactory(ISortResultFactory sortResultFactory) {
        sortResultFactories.add(0, sortResultFactory);
    }

    @Override
    public <E extends IComponent> SqlResult visit(ComponentDescriptor<E> componentDescriptor, SortNode node, EngineContext context) {
        return visit(componentDescriptor, node.getDirection(), null, node.getSelector(), context.getDefaultTableName(), context);
    }

    @Override
    public SqlResult visit(ComponentDescriptor<?> componentDescriptor, SortDirection sortDirection, String previousPropertyName, String selector, String tableJoinName, EngineContext context) {
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

        for (ISortResultFactory sortResultFactory : sortResultFactories) {
            if (sortResultFactory.acceptProperty(componentDescriptor, propertyDescriptor)) {
                return sortResultFactory.buildComponentSortResult(this, componentDescriptor, propertyDescriptor, sortDirection, previousPropertyName, nextPropertyName, tableJoinName, context);
            }
        }

        throw new IllegalPropertyException(String.format("Property %s not accepted", current));
    }
}
