package com.synaptix.entity.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {

    /**
     * @return Table name
     */
    String name();

    /**
     * @return Left join with previous
     */
    String[] left();

    /**
     * @return Right join with next
     */
    String[] right();

}
