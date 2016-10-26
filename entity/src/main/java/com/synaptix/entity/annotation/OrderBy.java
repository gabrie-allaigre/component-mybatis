package com.synaptix.entity.annotation;

public @interface OrderBy {

    String property();

    Sort sort() default Sort.Asc;

    enum Sort {
        Asc, Desc, AscNullsFisrt, AscNullsLast, DescNullsFisrt, DescNullsLast
    }

}
