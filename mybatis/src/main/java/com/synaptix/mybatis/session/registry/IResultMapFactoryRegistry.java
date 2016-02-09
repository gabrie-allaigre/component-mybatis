package com.synaptix.mybatis.session.registry;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

public interface IResultMapFactoryRegistry {

    /**
     * Create a result map
     *
     * @param configuration configuration
     * @param key           name of result map
     * @return result map or null if not found factory
     */
    ResultMap createResultMap(Configuration configuration, String key);

}
