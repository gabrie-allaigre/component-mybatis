package com.talanlabs.mybatis.rsql.sort;

public class SortNode {

    private final SortDirection direction;
    private final String selector;

    public SortNode(SortDirection direction, String selector) {
        super();

        this.direction = direction;
        this.selector = selector;
    }

    public SortDirection getDirection() {
        return direction;
    }

    public String getSelector() {
        return selector;
    }
}
