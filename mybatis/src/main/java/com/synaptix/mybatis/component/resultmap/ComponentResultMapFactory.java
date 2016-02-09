package com.synaptix.mybatis.component.resultmap;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.annotation.Association;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Id;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.session.factory.AbstractResultMapFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ComponentResultMapFactory extends AbstractResultMapFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentResultMapFactory.class);

    @Override
    public ResultMap createResultMap(Configuration configuration, String key) {
        Class<? extends IComponent> componentClass = ComponentHelper.getComponentClass(key);
        if (componentClass != null) {
            return createComponentResultMap(configuration, componentClass);
        }
        return null;
    }

    private ResultMap createComponentResultMap(Configuration configuration, Class<? extends IComponent> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ResultMap for " + componentClass);
        }

        String key = componentClass.getName();
        List<ResultMapping> resultMappings = createResultMappings(configuration, componentClass);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, key, componentClass, resultMappings, null);
        return inlineResultMapBuilder.build();
    }

    private List<ResultMapping> createResultMappings(Configuration configuration, Class<? extends IComponent> componentClass) {
        ComponentDescriptor<?> cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

        Set<String> propertyNames = cd.getPropertyNames();
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);

            Method method = propertyDescriptor.getMethod();

            if (method.isAnnotationPresent(Column.class)) {
                String columnName = propertyDescriptor.getMethod().getAnnotation(Column.class).name();
                if (StringUtils.isBlank(columnName)) {
                    throw new IllegalArgumentException("Not name in column for Component=" + componentClass + " with property=" + propertyName);
                }

                ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyName, columnName, propertyDescriptor.getPropertyClass());
                if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
                    resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
                }
                resultMappings.add(resultMappingBuilder.build());
            } else if (method.isAnnotationPresent(Association.class)) {
                Association assossiation = method.getAnnotation(Association.class);

                ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyName, prepareColumns(cd, assossiation.propertySource()),
                        propertyDescriptor.getPropertyClass());
                if (StringUtils.isNotBlank(assossiation.queryId())) {
                    resultMappingBuilder.nestedQueryId(assossiation.queryId());
                    resultMappings.add(resultMappingBuilder.build());
                } else if (ComponentFactory.getInstance().isComponentType(propertyDescriptor.getPropertyType())) {
                    resultMappingBuilder.nestedQueryId(
                            StatementNameHelper.buildFindComponentsByKey((Class<? extends IComponent>) propertyDescriptor.getPropertyClass(), prepareTarget(assossiation.propertyTarget())));
                    resultMappings.add(resultMappingBuilder.build());
                } else {
                    throw new RuntimeException("Not accept Association for Component=" + componentClass + " with property=" + propertyName);
                }
            } else {
                System.out.println("Not Yet implemented propertyName=" + propertyName);
            }
        }

        return resultMappings;
    }

    private String[] prepareTarget(String target) {
        return target.split(",");
    }

    private String prepareColumns(ComponentDescriptor<?> cd, String propertySource) throws IllegalArgumentException {
        if (StringUtils.isBlank(propertySource)) {
            throw new IllegalArgumentException("propertySource is null");
        }
        if (!propertySource.contains(",")) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertySource);
            checkColumn(cd, propertyDescriptor);

            return propertyDescriptor.getMethod().getAnnotation(Column.class).name();
        }

        String[] propertyNames = propertySource.split(",");
        List<String> columnNames = new ArrayList<>();
        int i = 1;
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            checkColumn(cd, propertyDescriptor);

            String columnName = propertyDescriptor.getMethod().getAnnotation(Column.class).name();
            columnNames.add("value" + i + "=" + columnName);

            i++;
        }
        return "{" + String.join(",", columnNames) + "}";
    }

    private void checkColumn(ComponentDescriptor<?> cd, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Not exists property for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Not present annotation Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (StringUtils.isBlank(propertyDescriptor.getMethod().getAnnotation(Column.class).name())) {
            throw new IllegalArgumentException("Not name in Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
    }
}
