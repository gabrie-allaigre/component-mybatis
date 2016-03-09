package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.session.factory.AbstractCacheFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.*;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComponentCacheFactory extends AbstractCacheFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentCacheFactory.class);

    private ComponentCacheManager componentCacheManager;

    public ComponentCacheFactory() {
        this(new ComponentCacheManager());
    }

    public ComponentCacheFactory(ComponentCacheManager componentCacheManager) {
        super();

        this.componentCacheManager = componentCacheManager;
    }

    @Override
    public Cache createCache(Configuration configuration, String key) {
        if (CacheNameHelper.isCacheKey(key)) {
            Class<? extends IComponent> componentClass =  CacheNameHelper.extractComponentClassInCacheKey(key);
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

        ComponentMyBatisHelper.findAllLinks(componentClass).forEach(subComponentClass -> {
            if (componentClass != subComponentClass) {
                componentCacheManager.putCacheLink(subComponentClass, componentClass);
            }
        });

        com.synaptix.entity.annotation.Cache cache = componentClass.getAnnotation(com.synaptix.entity.annotation.Cache.class);

        Cache res;
        if (cache == null) {
            res = new ComponentNoCache<E>(configuration, componentCacheManager, componentClass, key);
        } else {
            res = new ComponentCache<>(configuration, componentCacheManager, componentClass, key);
            res = new LruCache(res);
            ((LruCache) res).setSize(cache.size());
            res = new ScheduledCache(res);
            ((ScheduledCache) res).setClearInterval(cache.clearInterval());
            if (cache.readWrite()) {
                res = new SerializedCache(res);
            }
            res = new LoggingCache(res);
            res = new SynchronizedCache(res);
        }
        return res;
    }
}
