package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class LongProperty<Builder> extends AbstractNumberProperty<Builder, Long> {

    public LongProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(Long value) {
        return value != null ? value.toString() : "";
    }
}