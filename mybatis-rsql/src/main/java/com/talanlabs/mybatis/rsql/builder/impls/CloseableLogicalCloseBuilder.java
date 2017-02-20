package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public class CloseableLogicalCloseBuilder<PreviousBuilder> extends AbstractLogical<OpennedFieldOpenBuilder<PreviousBuilder>> {

    private final PreviousBuilder previousBuilder;

    CloseableLogicalCloseBuilder(RsqlBuilder rsqlBuilder, PreviousBuilder previousBuilder, StringBuilder context) {
        super(rsqlBuilder, context);

        this.previousBuilder = previousBuilder;
    }

    @Override
    protected OpennedFieldOpenBuilder<PreviousBuilder> nextBuilder() {
        return new OpennedFieldOpenBuilder<>(rsqlBuilder, previousBuilder, context);
    }

    /**
     * Close a group ")"
     */
    public PreviousBuilder closeGroup() {
        context.append(rsqlBuilder.getCloseGroupSymbol());
        return previousBuilder;
    }
}