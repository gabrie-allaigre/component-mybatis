package com.synaptix.mybatis.component.session.registry;

import com.synaptix.mybatis.component.session.factory.IResultMapFactory;

import java.util.ArrayList;
import java.util.List;

public class ResultMapFactoryRegistry {

    private List<IResultMapFactory> cacheFactories = new ArrayList<>();

    /**
     * Registry cache factory
     *
     * @param resultMapFactory cache factory to add
     */
    public void registry(IResultMapFactory resultMapFactory) {
        this.cacheFactories.add(resultMapFactory);
    }

    /**
     * Unregistry cache factory
     *
     * @param resultMapFactory cache factory to remove
     */
    public void unregistry(IResultMapFactory resultMapFactory) {
        this.cacheFactories.remove(resultMapFactory);
    }

    /**
     * Get cache factory
     *
     * @param key name of mapped statement
     * @return cache or null if not found factory
     */
    public IResultMapFactory getResultMapFactory(String key) {
        for (IResultMapFactory resultMapFactory : cacheFactories) {
            if (resultMapFactory.acceptKey(key)) {
                return resultMapFactory;
            }
        }
        return null;
    }

}
