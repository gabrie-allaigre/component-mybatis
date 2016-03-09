package com.synaptix.mybatis.session.factory;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.cache.Cache;

public interface ICacheFactory {

    /**
     * Create cache if key is valid
     *
     * @param componentConfiguration configuration
     * @param key                    key
     * @return cache or null
     */
    Cache createCache(ComponentConfiguration componentConfiguration, String key);

}
