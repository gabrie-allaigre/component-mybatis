package com.synaptix.entity.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {

    String name();

    String[] left();

    String[] right();

}
