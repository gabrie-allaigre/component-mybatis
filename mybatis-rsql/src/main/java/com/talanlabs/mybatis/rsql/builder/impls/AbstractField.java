package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public abstract class AbstractField<Builder> {

    protected final RsqlBuilder rsqlBuilder;
    protected final StringBuilder context;

    AbstractField(RsqlBuilder rsqlBuilder, StringBuilder context) {
        super();

        this.rsqlBuilder = rsqlBuilder;
        this.context = context;
    }

    /**
     * @return next builder
     */
    protected abstract Builder nextBuilder();

    /**
     * Custom operation
     *
     * @param field     a name
     * @param operation operation
     * @param value     value of operation
     */
    public Builder is(String field, String operation, String value) {
        context.append(field).append(operation).append(value);
        return nextBuilder();
    }

    /**
     * Equal to
     *
     * @param field name
     * @param value value of operation
     */
    public Builder eq(String field, String value) {
        context.append(field).append(Operator.Equal.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Equal to
     *
     * @param field name
     * @param value value of operation
     */
    public Builder neq(String field, String value) {
        context.append(field).append(Operator.NotEqual.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Less
     *
     * @param field name
     * @param value value of operation
     */
    public Builder lt(String field, String value) {
        context.append(field).append(Operator.Less.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Less or equals
     *
     * @param field name
     * @param value value of operation
     */
    public Builder lte(String field, String value) {
        context.append(field).append(Operator.LessEqual.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Greater
     *
     * @param field name
     * @param value value of operation
     */
    public Builder gt(String field, String value) {
        context.append(field).append(Operator.Greater.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Greater or equal
     *
     * @param field name
     * @param value value of operation
     */
    public Builder gte(String field, String value) {
        context.append(field).append(Operator.GreaterEqual.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * In
     *
     * @param field name
     * @param value value of operation
     */
    public Builder in(String field, String value) {
        context.append(field).append(Operator.In.getSymbol()).append(value);
        return nextBuilder();
    }

    /**
     * Not in
     *
     * @param field name
     * @param value value of operation
     */
    public Builder nin(String field, String value) {
        context.append(field).append(Operator.NotIn.getSymbol()).append(value);
        return nextBuilder();
    }

}