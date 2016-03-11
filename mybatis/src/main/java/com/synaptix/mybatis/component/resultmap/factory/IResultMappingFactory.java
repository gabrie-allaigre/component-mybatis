package com.synaptix.mybatis.component.resultmap.factory;

import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import org.apache.ibatis.mapping.ResultMapping;

import java.lang.annotation.Annotation;

public interface IResultMappingFactory<E extends Annotation> {

    Class<E> getAnnotationClass();

    ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

}
