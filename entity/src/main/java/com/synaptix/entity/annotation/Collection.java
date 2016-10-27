package com.synaptix.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Collection {

    /**
     * Property source, default use Id
     *
     * @return one or multiple
     */
    String[] propertySource() default {};

    /**
     * Property target, use for auto select
     *
     * @return one or multiple
     */
    String[] propertyTarget();

    /**
     * Java type for collection
     *
     * @return javaType or void.class for auto
     */
    Class<? extends java.util.Collection> javaType() default java.util.Collection.class;

    /**
     * Java type for element in collection
     *
     * @return type of element or void.class for auto
     */
    Class<?> ofType() default void.class;

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

    /**
     * Order by
     *
     * @return order by
     */
    OrderBy[] orderBy() default {};

}
