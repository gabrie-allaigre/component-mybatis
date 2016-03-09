package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.mapping.ResultMap;

public interface IResultMapFactoryRegistry {

    /**
     * Create a result map
     *
     * @param componentConfiguration configuration
     * @param key           name of result map
     * @return result map or null if not found factory
     */
    ResultMap createResultMap(ComponentConfiguration componentConfiguration, String key);

}
