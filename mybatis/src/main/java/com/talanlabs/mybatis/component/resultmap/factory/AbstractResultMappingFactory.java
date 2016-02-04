package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.factory.ComponentDescriptor;

import java.lang.annotation.Annotation;

public abstract class AbstractResultMappingFactory<E extends Annotation> implements IResultMappingFactory {

    private final Class<E> annotationClass;

    public AbstractResultMappingFactory(Class<E> annotationClass) {
        super();

        this.annotationClass = annotationClass;
    }

    @Override
    public boolean acceptProperty(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.getMethod().isAnnotationPresent(annotationClass);
    }

    public final Class<E> getAnnotationClass() {
        return annotationClass;
    }

}
