package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public abstract class AbstractLogical<Builder> {

    protected final RsqlBuilder rsqlBuilder;
    protected final StringBuilder context;

    AbstractLogical(RsqlBuilder rsqlBuilder, StringBuilder context) {
        super();

        this.rsqlBuilder = rsqlBuilder;
        this.context = context;
    }

    /**
     * @return next builder
     */
    protected abstract Builder nextBuilder();

    /**
     * AND operation
     */
    public Builder and() {
        context.append(rsqlBuilder.getAndSymbol());
        return nextBuilder();
    }

    /**
     * OR operation
     */
    public Builder or() {
        context.append(rsqlBuilder.getOrSymbol());
        return nextBuilder();
    }
}