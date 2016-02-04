package com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory;

import com.talanlabs.component.factory.ComponentDescriptor;

import java.lang.annotation.Annotation;

public abstract class AbstractSortResultFactory<E extends Annotation> implements ISortResultFactory {

    private final Class<E> annotationClass;

    public AbstractSortResultFactory(Class<E> annotationClass) {
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
