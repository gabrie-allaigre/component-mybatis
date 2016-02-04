package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Id;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.Collections;

public class ColumnResultMappingFactory extends AbstractResultMappingFactory<Column> {

    public ColumnResultMappingFactory() {
        super(Column.class);
    }

    @Override
    public ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);

        String columnName = column.name();
        if (StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("Not name in column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        Class<?> javaType = column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), columnName, javaType);
        if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            resultMappingBuilder.flags(Collections.singletonList(ResultFlag.ID));
        }
        if (!JdbcType.UNDEFINED.equals(column.jdbcType())) {
            resultMappingBuilder.jdbcType(column.jdbcType());
        }
        if (!UnknownTypeHandler.class.equals(column.typeHandler())) {
            resultMappingBuilder.typeHandler(componentConfiguration.getTypeHandlerRegistry().getTypeHandler(column.typeHandler()));
        }
        return resultMappingBuilder.build();
    }
}
