package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class IntegerProperty<Builder> extends AbstractNumberProperty<Builder, Integer> {

    public IntegerProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(Integer value) {
        return "'" + (value != null ? value.toString() : "") + "'";
    }
}