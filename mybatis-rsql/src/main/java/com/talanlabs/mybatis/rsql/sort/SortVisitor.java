package com.talanlabs.mybatis.rsql.sort;

import java.util.List;

public interface SortVisitor<R, A> {

    R visit(List<SortNode> nodes, A param);

    R visit(SortNode node, A param);
}
