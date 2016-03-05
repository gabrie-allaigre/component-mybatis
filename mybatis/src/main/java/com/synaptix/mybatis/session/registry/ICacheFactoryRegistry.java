package com.synaptix.mybatis.session.registry;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

public interface ICacheFactoryRegistry {

    /**
     * Create a cache statement
     *
     * @param configuration configuration
     * @param key           name of mapped statement
     * @return cache or null if not found factory
     */
    Cache createCache(Configuration configuration, String key);

}
