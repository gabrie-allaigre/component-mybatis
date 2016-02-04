package com.talanlabs.mybatis.rsql.engine.where.registry;

import com.talanlabs.mybatis.rsql.engine.where.IComparisonOperatorManager;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;

public interface IComparisonOperatorManagerRegistry {

    /**
     * Get a comparison operator manager for comparison operator
     *
     * @param comparisonOperator comparison operation
     * @return a comparison operator manager
     */
    IComparisonOperatorManager getComparisonOperatorManager(ComparisonOperator comparisonOperator);

}
