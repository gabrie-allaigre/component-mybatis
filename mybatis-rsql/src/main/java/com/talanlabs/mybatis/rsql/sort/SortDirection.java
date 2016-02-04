package com.talanlabs.mybatis.rsql.sort;

public final class SortDirection {

    private final String symbol;

    public SortDirection(String symbol) {
        super();

        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SortDirection))
            return false;

        SortDirection that = (SortDirection) o;
        return getSymbol().equals(that.getSymbol());
    }

    @Override
    public int hashCode() {
        return getSymbol().hashCode();
    }
}
