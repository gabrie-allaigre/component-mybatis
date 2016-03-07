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

    private ComponentCacheManager componentCacheManager;

    public ComponentCacheFactory() {
        super();

        this.componentCacheManager = new ComponentCacheManager();
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

    private <E extends IComponent> Cache createComponentCache(Configuration configuration, Class<E> componentClass, String key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create Cache for " + componentClass);
        }

        com.synaptix.entity.annotation.Cache cache = componentClass.getAnnotation(com.synaptix.entity.annotation.Cache.class);
        if (cache == null) {
            return new ComponentNoCache(key);
        }

        ComponentMyBatisHelper.findAllChildren(componentClass).forEach(subComponentClass -> {
            componentCacheManager.putAdd(subComponentClass, componentClass);
        });

        if (cache.links() != null && cache.links().length > 0) {
            for (Class<? extends IComponent> link : cache.links()) {
                componentCacheManager.putAdd(link, componentClass);
            }
        }

        return new CacheBuilder(key).build();//new ComponentCache<>(configuration, componentCacheManager, componentClass, key);
    }
}
