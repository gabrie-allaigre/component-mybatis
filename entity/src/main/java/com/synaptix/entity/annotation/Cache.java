package com.synaptix.entity.annotation;

import com.synaptix.component.IComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cache {

    /**
     * If true, object is same instance for read else is new instance
     *
     * @return default is false
     */
    boolean readOnly() default false;

    /**
     * Cache size
     *
     * @return default 512
     */
    int size() default 512;

    /**
     * Clear interval
     *
     * @return default 1 hour
     */
    long clearInterval() default 60 * 60 * 1000; // 1 hour

    /**
     * Others component is links, if links flush cache then flush current cache
     *
     * @return default empty array
     */
    Class<? extends IComponent>[] links() default {};

}
