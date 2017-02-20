package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public abstract class AbstractNumberProperty<Builder, Type extends Number> extends AbstractListableProperty<Builder, Type> {

    AbstractNumberProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    /**
     * Less to
     *
     * @param value number
     */
    public final Builder lt(Type value) {
        return fieldBuilder.lt(field, toString(value));
    }

    /**
     * Less or equals
     *
     * @param value number
     */
    public final Builder lte(Type value) {
        return fieldBuilder.lte(field, toString(value));
    }

    /**
     * Greater to
     *
     * @param value number
     */
    public final Builder gt(Type value) {
        return fieldBuilder.gt(field, toString(value));
    }

    /**
     * Greater or equals
     *
     * @param value number
     */
    public final Builder gte(Type value) {
        return fieldBuilder.gte(field, toString(value));
    }
}
