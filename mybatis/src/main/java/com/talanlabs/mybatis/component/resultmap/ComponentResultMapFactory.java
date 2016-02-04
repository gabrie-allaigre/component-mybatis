package com.talanlabs.mybatis.component.resultmap;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.mybatis.component.resultmap.factory.AssociationResultMappingFactory;
import com.talanlabs.mybatis.component.resultmap.factory.CollectionResultMappingFactory;
import com.talanlabs.mybatis.component.resultmap.factory.ColumnResultMappingFactory;
import com.talanlabs.mybatis.component.resultmap.factory.IResultMappingFactory;
import com.talanlabs.mybatis.component.resultmap.factory.NlsColumnResultMappingFactory;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractResultMapFactory;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComponentResultMapFactory extends AbstractResultMapFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentResultMapFactory.class);

    private List<IResultMappingFactory> resultMappingFactories = new ArrayList<>();

    public ComponentResultMapFactory() {
        super();

        addResultMappingFactory(new ColumnResultMappingFactory());
        addResultMappingFactory(new AssociationResultMappingFactory());
        addResultMappingFactory(new CollectionResultMappingFactory());
        addResultMappingFactory(new NlsColumnResultMappingFactory());
    }

    /**
     * Add result mapping factory, first
     *
     * @param resultMappingFactory a factory for result
     */
    public void addResultMappingFactory(IResultMappingFactory resultMappingFactory) {
        resultMappingFactories.add(0, resultMappingFactory);
    }

    @Override
    public boolean acceptKey(String key) {
        return ResultMapNameHelper.isResultMapKey(key);
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
                    LOG.debug("Not yet implemented propertyName=" + propertyDescriptor.getPropertyName());
                }
            }
        }

        return resultMappings;
    }

    private ResultMapping buildResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        for (IResultMappingFactory resultMappingFactory : resultMappingFactories) {
            if (resultMappingFactory.acceptProperty(componentDescriptor, propertyDescriptor)) {
                return resultMappingFactory.buildColumnResultMapping(componentConfiguration, componentDescriptor, propertyDescriptor);
            }
        }
        return null;
    }
}
