package com.talanlabs.mybatis.rsql.engine.policy;

import com.talanlabs.mybatis.rsql.engine.ILikePolicy;

public class DefaultLikePolicy implements ILikePolicy {

    public static final String DEFAULT_LIKE_SYMBOL = "%";
    public static final String DEFAULT_ESCAPE_SYMBOL = "\\";

    private final String likeSymbol;
    private final String escpaeSymbol;

    public DefaultLikePolicy() {
        this(DEFAULT_LIKE_SYMBOL, DEFAULT_ESCAPE_SYMBOL);
    }

    public DefaultLikePolicy(String likeSymbol, String escpaeSymbol) {
        super();

        this.likeSymbol = likeSymbol;
        this.escpaeSymbol = escpaeSymbol;
    }

    @Override
    public String getLikeSymbol() {
        return likeSymbol;
    }

    @Override
    public String getEscapeSymbol() {
        return escpaeSymbol;
    }
}
