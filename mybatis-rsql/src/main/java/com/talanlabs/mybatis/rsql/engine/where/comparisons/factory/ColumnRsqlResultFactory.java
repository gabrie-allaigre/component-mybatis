package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

public class ColumnRsqlResultFactory extends AbstractColumnRsqlResultFactory<Column> {

    public ColumnRsqlResultFactory(IRsqlConfiguration configuration) {
        super(Column.class, configuration);
    }

    @Override
    public SqlResult buildComponentRsqlResult(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            ComparisonNode node, String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context) {
        Column column = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, propertyDescriptor);
        Class<?> javaType = column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();
        JdbcType jdbcType = !JdbcType.UNDEFINED.equals(column.jdbcType()) ? column.jdbcType() : null;
        Class<? extends TypeHandler<?>> typeHandlerClass = !UnknownTypeHandler.class.equals(column.typeHandler()) ? column.typeHandler() : null;
        return buildComponentRsqlResult(rsqlResultContext, componentDescriptor, propertyDescriptor, node, previousPropertyName, nextPropertyName, tableJoinName, column.name(), javaType, jdbcType,
                typeHandlerClass, context);
    }
}
