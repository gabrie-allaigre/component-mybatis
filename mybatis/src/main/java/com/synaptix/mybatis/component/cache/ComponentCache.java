package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;
import java.util.Map;

public class ComponentCache<E extends IComponent> extends AbstractComponentCache<E> {

    private final Map<Object, Object> cache = new HashMap<>();

    public ComponentCache(Configuration configuration, ComponentCacheManager componentCacheManager, Class<E> componentClass, String id) {
        super(configuration, componentCacheManager, componentClass, id);
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

        super.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }

}
