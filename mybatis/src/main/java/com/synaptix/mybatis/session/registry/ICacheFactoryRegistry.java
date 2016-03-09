package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.cache.Cache;

public interface ICacheFactoryRegistry {

    /**
     * Create a cache statement
     *
     * @param componentConfiguration configuration
     * @param key           name of mapped statement
     * @return cache or null if not found factory
     */
    Cache createCache(ComponentConfiguration componentConfiguration, String key);

}
