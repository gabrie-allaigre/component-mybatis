package com.talanlabs.mybatis.rsql.sort;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SortParser {

    private final Set<SortDirection> sortDirections;
    private SortDirection defaultSortDirection = SortDirections.ASC;
    private String separator = ",";

    public SortParser() {
        this(SortDirections.defaultSortDirections());
    }

    public SortParser(Set<SortDirection> sortDirections) {
        super();

        this.sortDirections = new TreeSet<>(this::compareSortDirection);
        this.sortDirections.addAll(sortDirections);
    }

    public SortDirection getDefaultSortDirection() {
        return defaultSortDirection;
    }

    public void setDefaultSortDirection(SortDirection defaultSortDirection) {
        this.defaultSortDirection = defaultSortDirection;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Set<SortDirection> getSortDirections() {
        return sortDirections;
    }

    public List<SortNode> parse(String query) {
        String text = query.replaceAll(" ", "");
        String[] ss = text.split(separator);
        return Arrays.stream(ss).map(this::buildSortNode).collect(Collectors.toList());
    }

    private SortNode buildSortNode(String s) {
        for (SortDirection sortDirection : sortDirections) {
            if (s.startsWith(sortDirection.getSymbol())) {
                return new SortNode(sortDirection, s.substring(sortDirection.getSymbol().length()));
            }
        }
        return new SortNode(defaultSortDirection, s);
    }

    private int compareSortDirection(SortDirection o1, SortDirection o2) {
        if (o1.getSymbol().length() == o2.getSymbol().length()) {
            int i = o1.getSymbol().compareTo(o2.getSymbol());
            return i < 0 ? 1 : (i > 0 ? -1 : 0);
        } else {
            return -Integer.compare(o1.getSymbol().length(), o2.getSymbol().length());
        }
    }
}
