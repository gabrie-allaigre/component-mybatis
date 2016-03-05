package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.session.factory.AbstractCacheFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.CacheBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComponentCacheFactory extends AbstractCacheFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentCacheFactory.class);

    public ComponentCacheFactory() {
        super();
    }

    @Override
    public Cache createCache(Configuration configuration, String key) {
        if (CacheNameHelper.isCacheKey(key)) {
            String componentName = CacheNameHelper.extractComponentNameInCacheKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null) {
                return createComponentCache(configuration, componentClass, key);
            }
        }
        return null;
    }

    private Cache createComponentCache(Configuration configuration, Class<? extends IComponent> componentClass, String key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create Cache for " + componentClass);
        }

        CacheBuilder cacheBuilder = new CacheBuilder(key);
        return cacheBuilder.build();
    }
}
