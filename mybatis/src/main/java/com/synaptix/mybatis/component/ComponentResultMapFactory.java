package com.synaptix.mybatis.component;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ComponentResultMapFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentResultMapFactory.class);

    private final Configuration configuration;

    public ComponentResultMapFactory(Configuration configuration) {
        super();

        this.configuration = configuration;
    }

    public ResultMap createComponentResultMap(Class<? extends IComponent> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ResultMap for " + componentClass);
        }

        String key = componentClass.getName();
        List<ResultMapping> resultMappings = createResultMappings(componentClass);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, key, componentClass, resultMappings, null);
        return inlineResultMapBuilder.build();
    }

    private List<ResultMapping> createResultMappings(Class<? extends IComponent> componentClass) {
        ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

        Set<String> propertyNames = cd.getPropertyNames();
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);

            boolean otherComponent = ComponentFactory.getInstance().isComponentType(propertyDescriptor.getPropertyType());

            Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);
            if (column != null) {
                ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyName, column.name(), propertyDescriptor.getPropertyClass());
                if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
                    resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
                }
                resultMappings.add(resultMappingBuilder.build());
            } else {
                System.out.println("Not yet implemented");
            }
        }

        return resultMappings;
    }
}
