package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import org.apache.ibatis.session.Configuration;

public class ComponentNoCache<E extends IComponent> extends AbstractComponentCache<E> {

    public ComponentNoCache(Configuration configuration, ComponentCacheManager componentCacheManager, Class<E> componentClass, String id) {
        super(configuration, componentCacheManager, componentClass, id);
    }

    @Override
    public void putObject(Object key, Object value) {
        // Nothing
    }

    @Override
    public Object getObject(Object key) {
        return null;// Nothing
    }

    @Override
    public Object removeObject(Object key) {
        return null; // Nothing
    }

    @Override
    public int getSize() {
        return 0; // Nothing
    }
}
