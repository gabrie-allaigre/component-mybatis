package com.talanlabs.entity.annotation;

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
     * @return one or multiple
     */
    String[] propertySource();

    /**
     * Property target, use for auto select, default use Id
     *
     * @return one or multiple
     */
    String[] propertyTarget() default {};

    /**
     * Java type for association
     *
     * @return javaType or void.class for auto
     */
    Class<?> javaType() default void.class;

    /**
     * Select mapped statement
     *
     * @return key for select or empty for auto select
     */
    String select() default "";

    /**
     * Lazy or eager loading
     *
     * @return type of loading
     */
    FetchType fetchType() default FetchType.DEFAULT;

    /**
     * Join table
     *
     * @return join table
     */
    JoinTable[] joinTable() default {};

}
