package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class BooleanProperty<Builder> extends AbstractProperty<Builder> {

    public BooleanProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    public final Builder isTrue() {
        return fieldBuilder.eq(field, "true");
    }

    public final Builder isFalse() {
        return fieldBuilder.eq(field, "false");
    }
}
