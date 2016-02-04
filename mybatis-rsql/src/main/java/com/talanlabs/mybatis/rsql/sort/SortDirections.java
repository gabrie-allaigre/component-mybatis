package com.talanlabs.mybatis.rsql.sort;

import com.google.common.collect.Sets;

import java.util.Set;

public abstract class SortDirections {

    public static final SortDirection ASC = new SortDirection("+");
    public static final SortDirection ASC_NULLS_FIRST = new SortDirection("++");
    public static final SortDirection ASC_NULLS_LAST = new SortDirection("+-");
    public static final SortDirection DESC = new SortDirection("-");
    public static final SortDirection DESC_NULLS_FIRST = new SortDirection("-+");
    public static final SortDirection DESC_NULLS_LAST = new SortDirection("--");

    public static final Set<SortDirection> defaultSortDirections() {
        return Sets.newHashSet(ASC, ASC_NULLS_FIRST, ASC_NULLS_LAST, DESC, DESC_NULLS_FIRST, DESC_NULLS_LAST);
    }
}
