package com.synaptix.mybatis.component.resultmap;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.resultmap.factory.*;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.AbstractResultMapFactory;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComponentResultMapFactory extends AbstractResultMapFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentResultMapFactory.class);

    private List<IResultMappingFactory<?>> resultMappingFactories = new ArrayList<>();

    public ComponentResultMapFactory() {
        super();

        addResultMappingFactory(new ColumnResultMappingFactory());
        addResultMappingFactory(new AssociationResultMappingFactory());
        addResultMappingFactory(new CollectionResultMappingFactory());
        addResultMappingFactory(new NlsColumnResultMappingFactory());
    }

    public void addResultMappingFactory(IResultMappingFactory<?> resultMappingFactory) {
        resultMappingFactories.add(resultMappingFactory);
    }

    @Override
    public ResultMap createResultMap(ComponentConfiguration componentConfiguration, String key) {
        if (ResultMapNameHelper.isResultMapKey(key)) {
            Class<? extends IComponent> componentClass = ResultMapNameHelper.extractComponentClassInResultMapKey(key);
            if (componentClass != null) {
                return createComponentResultMap(componentConfiguration, componentClass, key);
            }
        }
        return null;
    }

    private ResultMap createComponentResultMap(ComponentConfiguration componentConfiguration, Class<? extends IComponent> componentClass, String key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ResultMap for " + componentClass);
        }

        List<ResultMapping> resultMappings = createResultMappings(componentConfiguration, componentClass);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(componentConfiguration, key, componentClass, resultMappings, null);
        return inlineResultMapBuilder.build();
    }

    private List<ResultMapping> createResultMappings(ComponentConfiguration componentConfiguration, Class<? extends IComponent> componentClass) {
        ComponentDescriptor<?> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

        List<ResultMapping> resultMappings = new ArrayList<>();

        Set<String> propertyNames = componentDescriptor.getPropertyNames();
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);

            ResultMapping resultMapping = buildResultMapping(componentConfiguration, componentDescriptor, propertyDescriptor);
            if (resultMapping != null) {
                resultMappings.add(resultMapping);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Not Yet implemented propertyName=" + propertyDescriptor.getPropertyName());
                }
            }
        }

        return resultMappings;
    }

    private ResultMapping buildResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Method method = propertyDescriptor.getMethod();
        for (IResultMappingFactory<?> resultMappingFactory : resultMappingFactories) {
            if (method.isAnnotationPresent(resultMappingFactory.getAnnotationClass())) {
                return resultMappingFactory.buildColumnResultMapping(componentConfiguration, componentDescriptor, propertyDescriptor);
            }
        }
        return null;
    }
}
