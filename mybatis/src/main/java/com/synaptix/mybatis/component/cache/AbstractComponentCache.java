package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.session.Configuration;

import java.util.concurrent.locks.ReadWriteLock;

public abstract class AbstractComponentCache<E extends IComponent> implements Cache {

    private final Configuration configuration;
    private final ComponentCacheManager componentCacheManager;
    private final Class<E> componentClass;
    private final String id;

    public AbstractComponentCache(Configuration configuration, ComponentCacheManager componentCacheManager, Class<E> componentClass, String id) {
        super();

        this.configuration = configuration;
        this.componentCacheManager = componentCacheManager;
        this.componentClass = componentClass;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void clear() {
        if (componentCacheManager.isDispatch()) {
            componentCacheManager.undispatch();

            componentCacheManager.getCacheLinks(componentClass).forEach(cacheName -> configuration.getCache(cacheName).clear());

            componentCacheManager.dispatch();

            componentCacheManager.fireCleared(getId());
        }
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID.");
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cache)) {
            return false;
        }

        Cache otherCache = (Cache) o;
        return getId().equals(otherCache.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID.");
        }
        return getId().hashCode();
    }
}
