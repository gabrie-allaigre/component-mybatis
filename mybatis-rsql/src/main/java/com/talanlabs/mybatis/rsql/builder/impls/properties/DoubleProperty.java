package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class DoubleProperty<Builder> extends AbstractNumberProperty<Builder, Double> {

    public DoubleProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(Double value) {
        return value != null ? value.toString() : "";
    }
}