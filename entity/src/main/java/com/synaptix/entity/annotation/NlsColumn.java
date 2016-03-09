package com.synaptix.entity.annotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NlsColumn {

    /**
     * Column name in database
     *
     * @return name
     */
    String name();

    /**
     * Java type for collection
     *
     * @return javaType or void.class for auto
     */
    Class<?> javaType() default void.class;

    /**
     * Jdbc Type
     *
     * @return type
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * Type handler
     *
     * @return handler
     */
    Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;

    /**
     * Property source default use Id
     *
     * @return one or multiple
     */
    String[] propertySource() default {};

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
}
