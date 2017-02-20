package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

public class StringProperty<Builder> extends AbstractListableProperty<Builder, String> {

    public StringProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    public Builder startWith(String value) {
        return eq((value != null ? value : "") + "*");
    }

    public Builder endWith(String value) {
        return eq("*" + (value != null ? value : ""));
    }

    public Builder containts(String value) {
        return eq("*" + (value != null ? value : "") + "*");
    }

    @Override
    protected String toString(String value) {
        return "'" + (value != null ? value.replaceAll("'", "\\\\'") : "") + "'";
    }
}