package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public class OpennedFieldOpenBuilder<E> extends AbstractPropertyField<CloseableLogicalCloseBuilder<E>> {

    private final E previousBuilder;
    private final StringBuilder context;

    OpennedFieldOpenBuilder(RsqlBuilder rsqlBuilder, E previousBuilder, StringBuilder context) {
        super(rsqlBuilder, context);

        this.previousBuilder = previousBuilder;
        this.context = context;
    }

    @Override
    protected CloseableLogicalCloseBuilder<E> nextBuilder() {
        return new CloseableLogicalCloseBuilder<E>(rsqlBuilder, previousBuilder, context);
    }

    /**
     * Open a new group "("
     */
    public OpennedFieldOpenBuilder<CloseableLogicalCloseBuilder<E>> openGroup() {
        context.append(rsqlBuilder.getOpenGroupSymbol());
        return new OpennedFieldOpenBuilder<>(rsqlBuilder, new CloseableLogicalCloseBuilder<>(rsqlBuilder, previousBuilder, context), context);
    }
}