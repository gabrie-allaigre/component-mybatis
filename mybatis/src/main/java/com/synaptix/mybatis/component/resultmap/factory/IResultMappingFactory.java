package com.synaptix.mybatis.component.resultmap.factory;

import com.synaptix.component.factory.ComponentDescriptor;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.lang.annotation.Annotation;

public interface IResultMappingFactory<E extends Annotation> {

    Class<E> getAnnotationClass();

    ResultMapping buildColumnResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor);

}
