package com.synaptix.mybatis.component.resultmap.factory;

import java.lang.annotation.Annotation;

public abstract class AbstractResultMappingFactory<E extends Annotation> implements IResultMappingFactory<E> {

    private final Class<E> annotationClass;

    public AbstractResultMappingFactory(Class<E> annotationClass) {
        super();

        this.annotationClass = annotationClass;
    }

    public final Class<E> getAnnotationClass() {
        return annotationClass;
    }

}
