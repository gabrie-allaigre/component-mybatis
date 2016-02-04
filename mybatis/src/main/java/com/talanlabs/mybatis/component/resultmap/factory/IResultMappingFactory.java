package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import org.apache.ibatis.mapping.ResultMapping;

public interface IResultMappingFactory {

    /**
     * @param componentDescriptor component descriptor
     * @param propertyDescriptor  current property
     * @return true, if factory build with property
     */
    boolean acceptProperty(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

    /**
     * @param componentConfiguration configuration
     * @param componentDescriptor    component descriptor
     * @param propertyDescriptor     current property
     * @return A result mapping
     */
    ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

}
