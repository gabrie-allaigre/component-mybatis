package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class FloatProperty<Builder> extends AbstractNumberProperty<Builder, Float> {

    public FloatProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(Float value) {
        return value != null ? value.toString() : "";
    }
}