package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public abstract class AbstractEquitableProperty<Builder, Type> extends AbstractProperty<Builder> {

    AbstractEquitableProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    /**
     * Equal compare
     *
     * @param value value of type
     */
    public final Builder eq(Type value) {
        return fieldBuilder.eq(field, toString(value));
    }

    /**
     * Not equal
     *
     * @param value value of type
     */
    public final Builder neq(Type value) {
        return fieldBuilder.neq(field, toString(value));
    }

    protected abstract String toString(Type value);

}
