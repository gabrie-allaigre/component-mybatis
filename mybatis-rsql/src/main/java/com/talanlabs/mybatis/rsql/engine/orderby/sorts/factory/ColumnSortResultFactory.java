package com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.sort.SortDirection;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

public class ColumnSortResultFactory extends AbstractColumnSortResultFactory<Column> {

    public ColumnSortResultFactory(IRsqlConfiguration configuration) {
        super(Column.class, configuration);
    }

    @Override
    public SqlResult buildComponentSortResult(ISortResultContext sortResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            SortDirection sortDirection, String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context) {
        Column column = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, propertyDescriptor);
        Class<?> javaType = column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();
        JdbcType jdbcType = !JdbcType.UNDEFINED.equals(column.jdbcType()) ? column.jdbcType() : null;
        Class<? extends TypeHandler<?>> typeHandlerClass = !UnknownTypeHandler.class.equals(column.typeHandler()) ? column.typeHandler() : null;
        return buildComponentSortResult(sortResultContext, componentDescriptor, propertyDescriptor, sortDirection, previousPropertyName, nextPropertyName, tableJoinName, column.name(), javaType,
                jdbcType, typeHandlerClass, context);
    }
}
