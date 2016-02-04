package com.talanlabs.mybatis.component.session.factory;

import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import org.apache.ibatis.cache.Cache;

public interface ICacheFactory {

    /**
     * Accept key
     *
     * @param key key at verify
     * @return true or false
     */
    boolean acceptKey(String key);

    /**
     * Create cache if key is valid
     *
     * @param componentConfiguration configuration
     * @param key                    key
     * @return cache or null
     */
    Cache createCache(ComponentConfiguration componentConfiguration, String key);

}
