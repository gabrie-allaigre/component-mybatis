package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public abstract class AbstractProperty<Builder> {

    protected final String field;
    protected final AbstractField<Builder> fieldBuilder;

    AbstractProperty(String field, AbstractField<Builder> fieldBuilder) {
        super();

        this.field = field;
        this.fieldBuilder = fieldBuilder;
    }
}
