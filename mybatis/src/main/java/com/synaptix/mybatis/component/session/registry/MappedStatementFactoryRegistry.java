package com.synaptix.mybatis.component.session.registry;

import com.synaptix.mybatis.component.session.factory.IMappedStatementFactory;

import java.util.ArrayList;
import java.util.List;

public class MappedStatementFactoryRegistry {

    private List<IMappedStatementFactory> cacheFactories = new ArrayList<>();

    /**
     * Registry cache factory
     *
     * @param mappedStatementFactory cache factory to add
     */
    public void registry(IMappedStatementFactory mappedStatementFactory) {
        this.cacheFactories.add(mappedStatementFactory);
    }

    /**
     * Unregistry cache factory
     *
     * @param mappedStatementFactory cache factory to remove
     */
    public void unregistry(IMappedStatementFactory mappedStatementFactory) {
        this.cacheFactories.remove(mappedStatementFactory);
    }

    /**
     * Get cache factory
     *
     * @param key name of mapped statement
     * @return cache or null if not found factory
     */
    public IMappedStatementFactory getMappedStatementFactory(String key) {
        for (IMappedStatementFactory mappedStatementFactory : cacheFactories) {
            if (mappedStatementFactory.acceptKey(key)) {
                return mappedStatementFactory;
            }
        }
        return null;
    }
}
