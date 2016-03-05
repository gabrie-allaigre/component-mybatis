package com.synaptix.mybatis.guice.registry;

import com.google.inject.Inject;
import com.synaptix.mybatis.session.factory.ICacheFactory;
import com.synaptix.mybatis.session.registry.AbstractCacheFactoryRegistry;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;

import java.util.Set;

public class GuiceCacheFactoryRegistry extends AbstractCacheFactoryRegistry {

    @Inject(optional = true)
    private Set<ICacheFactory> cacheFactories;

    @Override
    public Cache createCache(Configuration configuration, String key) {
        for (ICacheFactory cacheFactory : cacheFactories) {
            Cache cache = cacheFactory.createCache(configuration, key);
            if (cache != null) {
                return cache;
            }
        }
        return null;
    }
}
