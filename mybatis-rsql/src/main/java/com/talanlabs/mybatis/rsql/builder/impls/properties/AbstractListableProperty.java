package com.talanlabs.mybatis.rsql.builder.impls.properties;

import com.talanlabs.mybatis.rsql.builder.impls.AbstractField;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AbstractListableProperty<Builder, Type> extends AbstractEquitableProperty<Builder, Type> {

    AbstractListableProperty(String field, AbstractField<Builder> fieldBuilder) {
        super(field, fieldBuilder);
    }

    /**
     * In
     *
     * @param values arrays of type
     */
    public final Builder in(Type... values) {
        return in(Arrays.asList(values));
    }

    /**
     * In
     *
     * @param values collection of type
     */
    public final Builder in(Collection<Type> values) {
        return fieldBuilder.in(field, toString(values));
    }

    /**
     * Not in
     *
     * @param values arrays of type
     */
    public final Builder nin(Type... values) {
        return nin(Arrays.asList(values));
    }

    /**
     * Not in
     *
     * @param values collection of type
     */
    public final Builder nin(Collection<Type> values) {
        return fieldBuilder.nin(field, toString(values));
    }

    private String toString(Collection<Type> values) {
        return values.stream().map(this::toString).collect(Collectors.joining(",", "(", ")"));
    }
}
