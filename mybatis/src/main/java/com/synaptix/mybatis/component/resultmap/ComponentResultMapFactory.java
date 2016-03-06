package com.synaptix.mybatis.component.resultmap;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.resultmap.factory.AssociationResultMappingFactory;
import com.synaptix.mybatis.component.resultmap.factory.CollectionResultMappingFactory;
import com.synaptix.mybatis.component.resultmap.factory.ColumnResultMappingFactory;
import com.synaptix.mybatis.component.resultmap.factory.IResultMappingFactory;
import com.synaptix.mybatis.session.factory.AbstractResultMapFactory;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
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
    }

    public void addResultMappingFactory(IResultMappingFactory<?> resultMappingFactory) {
        resultMappingFactories.add(resultMappingFactory);
    }

    @Override
    public ResultMap createResultMap(Configuration configuration, String key) {
        if (ResultMapNameHelper.isResultMapKey(key)) {
            String componentName = ResultMapNameHelper.extractComponentNameInResultMapKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null) {
                return createComponentResultMap(configuration, componentClass, key);
            }
        }
        return null;
    }

    private ResultMap createComponentResultMap(Configuration configuration, Class<? extends IComponent> componentClass, String key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ResultMap for " + componentClass);
        }

        List<ResultMapping> resultMappings = createResultMappings(configuration, componentClass);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, key, componentClass, resultMappings, null);
        return inlineResultMapBuilder.build();
    }

    private List<ResultMapping> createResultMappings(Configuration configuration, Class<? extends IComponent> componentClass) {
        ComponentDescriptor<?> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

        List<ResultMapping> resultMappings = new ArrayList<>();

        Set<String> propertyNames = componentDescriptor.getPropertyNames();
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);

            ResultMapping resultMapping = buildResultMapping(configuration, componentDescriptor, propertyDescriptor);
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

    private ResultMapping buildResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Method method = propertyDescriptor.getMethod();
        for (IResultMappingFactory<?> resultMappingFactory : resultMappingFactories) {
            if (method.isAnnotationPresent(resultMappingFactory.getAnnotationClass())) {
                return resultMappingFactory.buildColumnResultMapping(configuration, componentDescriptor, propertyDescriptor);
            }
        }
        return null;
    }
}
