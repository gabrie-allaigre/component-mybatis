package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;

import java.lang.annotation.Annotation;

public abstract class AbstractRsqlResultFactory<E extends Annotation> implements IRsqlResultFactory {

    private final Class<E> annotationClass;

    public AbstractRsqlResultFactory(Class<E> annotationClass) {
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
