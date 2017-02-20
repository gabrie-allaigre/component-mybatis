package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;
import com.talanlabs.mybatis.rsql.builder.impls.properties.BooleanProperty;
import com.talanlabs.mybatis.rsql.builder.impls.properties.IntegerProperty;
import com.talanlabs.mybatis.rsql.builder.impls.properties.StringProperty;

public abstract class AbstractPropertyField<Builder> extends AbstractField<Builder> {

    AbstractPropertyField(RsqlBuilder rsqlBuilder, StringBuilder context) {
        super(rsqlBuilder, context);
    }

    public StringProperty<Builder> string(String field) {
        return new StringProperty<>(field, this);
    }

    public BooleanProperty<Builder> bool(String field) {
        return new BooleanProperty<Builder>(field, this);
    }

    public IntegerProperty<Builder> integer(String field) {
        return new IntegerProperty<Builder>(field, this);
    }
}
