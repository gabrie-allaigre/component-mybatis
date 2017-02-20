package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class EnumProperty<Builder, E extends Enum<E>> extends AbstractListableProperty<Builder, E> {

    public EnumProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    @Override
    protected String toString(E value) {
        return "'" + (value != null ? value.name() : "") + "'";
    }
}