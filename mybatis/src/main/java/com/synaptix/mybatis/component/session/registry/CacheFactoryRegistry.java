package com.synaptix.mybatis.component.session.registry;

import com.synaptix.mybatis.component.session.factory.ICacheFactory;

import java.util.ArrayList;
import java.util.List;

public class CacheFactoryRegistry {

    private List<ICacheFactory> cacheFactories = new ArrayList<>();

    /**
     * Registry cache factory
     *
     * @param cacheFactory cache factory to add
     */
    public void registry(ICacheFactory cacheFactory) {
        this.cacheFactories.add(cacheFactory);
    }

    /**
     * Unregistry cache factory
     *
     * @param cacheFactory cache factory to remove
     */
    public void unregistry(ICacheFactory cacheFactory) {
        this.cacheFactories.remove(cacheFactory);
    }

    /**
     * Get cache factory
     *
     * @param key name of mapped statement
     * @return cache or null if not found factory
     */
    public ICacheFactory getCacheFactory(String key) {
        for (ICacheFactory cacheFactory : cacheFactories) {
            if (cacheFactory.acceptKey(key)) {
                return cacheFactory;
            }
        }
        return null;
    }
}
