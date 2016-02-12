package com.synaptix.mybatis.component.resultmap.factory;

import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Id;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.Arrays;

public class ColumnResultMappingFactory extends AbstractResultMappingFactory<Column> {

    public ColumnResultMappingFactory() {
        super(Column.class);
    }

    @Override
    public ResultMapping buildColumnResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);

        String columnName = column.name();
        if (StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("Not name in column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        Class<?> javaType = column.javaType() == void.class ? propertyDescriptor.getPropertyClass() : column.javaType();

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyDescriptor.getPropertyName(), columnName, javaType);
        if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
        }
        if (column.jdbcType() != null && !JdbcType.UNDEFINED.equals(column.jdbcType())) {
            resultMappingBuilder.jdbcType(column.jdbcType());
        }
        if (column.typeHandler() != null && !UnknownTypeHandler.class.equals(column.typeHandler())) {
            resultMappingBuilder.typeHandler(configuration.getTypeHandlerRegistry().getTypeHandler(column.typeHandler()));
        }
        return resultMappingBuilder.build();
    }
}
