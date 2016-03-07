package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class ComponentCache<E extends IComponent> implements Cache {

    private final Configuration configuration;

    private final ComponentCacheManager componentCacheManager;

    private final Class<E> componentClass;

    private final String id;

    private final Map<Object, Object> cache = new HashMap<Object, Object>();

    public ComponentCache(Configuration configuration, ComponentCacheManager componentCacheManager, Class<E> componentClass, String id) {
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
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();

        componentCacheManager.get(componentClass).forEach(linkComponentClass -> {
            configuration.getCache(CacheNameHelper.buildCacheKey(linkComponentClass)).clear();
        });
    }

    @Override
    public int getSize() {
        return cache.size();
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
