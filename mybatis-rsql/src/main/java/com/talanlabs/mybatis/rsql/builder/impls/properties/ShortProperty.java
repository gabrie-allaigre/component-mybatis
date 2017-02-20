package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class ShortProperty<Builder> extends AbstractNumberProperty<Builder, Short> {

    public ShortProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(Short value) {
        return value != null ? value.toString() : "";
    }
}