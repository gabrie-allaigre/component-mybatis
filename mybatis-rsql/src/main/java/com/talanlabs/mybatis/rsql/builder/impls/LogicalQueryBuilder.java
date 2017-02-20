package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;

public class LogicalQueryBuilder extends AbstractLogical<FieldOpenBuilder> {

    LogicalQueryBuilder(RsqlBuilder rsqlBuilder, StringBuilder context) {
        super(rsqlBuilder, context);
    }

    @Override
    protected FieldOpenBuilder nextBuilder() {
        return new FieldOpenBuilder(rsqlBuilder, context);
    }

    /**
     * @return rsql query
     */
    public String query() {
        return context.toString();
    }
}