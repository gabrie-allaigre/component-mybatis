package com.synaptix.entity.annotation;

import com.synaptix.entity.EntityFields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Association {

    /**
     * Property source
     *
     * @return
     */
    String propertySource();

    /**
     * Property target
     *
     * @return
     */
    String propertyTarget() default EntityFields.id;

    String queryId() default "";
}
