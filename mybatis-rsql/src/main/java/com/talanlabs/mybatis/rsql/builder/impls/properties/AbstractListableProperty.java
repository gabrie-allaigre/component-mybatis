package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractListableProperty<Builder, Type> extends AbstractEquitableProperty<Builder, Type> {

    AbstractListableProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    public final Builder in(Type... values) {
        return in(Arrays.asList(values));
    }

    public final Builder in(Collection<Type> values) {
        return fieldBuilder.in(field, toString(values));
    }

    public final Builder out(Type... values) {
        return out(Arrays.asList(values));
    }

    public final Builder out(Collection<Type> values) {
        return fieldBuilder.out(field, toString(values));
    }

    private String toString(Collection<Type> values) {
        return values.stream().map(this::toString).collect(Collectors.joining(",", "(", ")"));
    }
}
