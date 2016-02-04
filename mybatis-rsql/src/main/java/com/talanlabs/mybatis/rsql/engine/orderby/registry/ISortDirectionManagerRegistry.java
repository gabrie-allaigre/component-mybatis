package com.talanlabs.mybatis.rsql.engine.orderby.registry;

import com.talanlabs.mybatis.rsql.engine.orderby.ISortDirectionManager;
import com.talanlabs.mybatis.rsql.sort.SortDirection;

public interface ISortDirectionManagerRegistry {

    /**
     * Get a comparison operator manager for comparison operator
     *
     * @param sortDirection sort direction
     * @return a sort direction manager
     */
    ISortDirectionManager getSortDirectionManager(SortDirection sortDirection);

}
