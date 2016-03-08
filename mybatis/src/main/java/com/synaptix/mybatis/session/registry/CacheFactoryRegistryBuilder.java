package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.factory.ICacheFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

public class CacheFactoryRegistryBuilder {

    private final MyCacheFactoryRegistry cacheFactoryRegistry;

    private CacheFactoryRegistryBuilder() {
        super();

        this.cacheFactoryRegistry = new MyCacheFactoryRegistry();
    }

    public static CacheFactoryRegistryBuilder newBuilder() {
        return new CacheFactoryRegistryBuilder();
    }

    public CacheFactoryRegistryBuilder addCacheFactory(ICacheFactory cacheFactory) {
        cacheFactoryRegistry.cacheFactories.add(cacheFactory);
        return this;
    }

    public ICacheFactoryRegistry build() {
        return cacheFactoryRegistry;
    }

    private static class MyCacheFactoryRegistry extends AbstractCacheFactoryRegistry {

        private List<ICacheFactory> cacheFactories;

        public MyCacheFactoryRegistry() {
            super();

            this.cacheFactories = new ArrayList<>();
        }

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
}