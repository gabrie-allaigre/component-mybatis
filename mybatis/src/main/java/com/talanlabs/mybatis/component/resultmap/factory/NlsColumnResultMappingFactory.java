package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.FetchType;
import com.talanlabs.entity.annotation.Id;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NlsColumnResultMappingFactory extends AbstractResultMappingFactory<NlsColumn> {

    public NlsColumnResultMappingFactory() {
        super(NlsColumn.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        NlsColumn nlsColumn = propertyDescriptor.getMethod().getAnnotation(NlsColumn.class);

        String columnName = nlsColumn.name();
        if (StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("Not name in nlsColumn for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        Class<?> javaType = nlsColumn.javaType() != void.class ? nlsColumn.javaType() : propertyDescriptor.getPropertyClass();

        if (componentConfiguration.getNlsColumnHandler() == null) {
            return buildDefaultColumnResultMapping(componentConfiguration, componentDescriptor, propertyDescriptor, nlsColumn, javaType, columnName);
        } else {
            return buildNlsColumnResultMapping(componentConfiguration, componentDescriptor, propertyDescriptor, nlsColumn, javaType, columnName);
        }
    }

    private ResultMapping buildNlsColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor, NlsColumn nlsColumn, Class<?> javaType, String columnName) {
        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), null, javaType);
        List<ResultMapping> composites = new ArrayList<>();
        composites.add(new ResultMapping.Builder(componentConfiguration, "defaultValue", columnName, javaType).build());

        String[] propertySources = nlsColumn.propertySource();
        if (propertySources.length == 0) {
            propertySources = new String[] { EntityHelper.findIdPropertyName(componentDescriptor.getComponentClass()) };
        }
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = ComponentResultMapHelper.prepareSourceColumns(componentDescriptor, propertySources);
        for (Pair<ComponentDescriptor.PropertyDescriptor, String> sourceColumn : sourceColumns) {
            ComponentDescriptor.PropertyDescriptor pd = sourceColumn.getLeft();
            composites.add(new ResultMapping.Builder(componentConfiguration, sourceColumn.getLeft().getPropertyName(), sourceColumn.getRight(), pd.getPropertyClass()).build());
        }
        resultMappingBuilder.composites(composites);

        if (FetchType.LAZY.equals(nlsColumn.fetchType())) {
            resultMappingBuilder.lazy(true);
        } else if (FetchType.EAGER.equals(nlsColumn.fetchType())) {
            resultMappingBuilder.lazy(false);
        }
        if (StringUtils.isNotBlank(nlsColumn.select())) {
            resultMappingBuilder.nestedQueryId(nlsColumn.select());
        } else {
            resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindNlsColumnKey(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName()));
        }
        return resultMappingBuilder.build();
    }

    private ResultMapping buildDefaultColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor, NlsColumn nlsColumn, Class<?> javaType, String columnName) {
        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), columnName, javaType);
        if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            resultMappingBuilder.flags(Collections.singletonList(ResultFlag.ID));
        }
        if (!JdbcType.UNDEFINED.equals(nlsColumn.jdbcType())) {
            resultMappingBuilder.jdbcType(nlsColumn.jdbcType());
        }
        if (!UnknownTypeHandler.class.equals(nlsColumn.typeHandler())) {
            resultMappingBuilder.typeHandler(componentConfiguration.getTypeHandler(nlsColumn.typeHandler()));
        }
        return resultMappingBuilder.build();
    }
}
