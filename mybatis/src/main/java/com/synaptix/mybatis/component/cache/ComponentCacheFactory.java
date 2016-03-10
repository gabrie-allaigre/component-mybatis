package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.AbstractCacheFactory;
import com.synaptix.mybatis.session.handler.INlsColumnHandler;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.*;
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
    public Cache createCache(ComponentConfiguration componentConfiguration, String key) {
        if (CacheNameHelper.isCacheKey(key)) {
            Class<? extends IComponent> componentClass = CacheNameHelper.extractComponentClassInCacheKey(key);
            if (componentClass != null) {
                return createComponentCache(componentConfiguration, componentClass, key);
            }
        }
        return null;
    }

    private <E extends IComponent> Cache createComponentCache(ComponentConfiguration componentConfiguration, Class<E> componentClass, String key) {
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
            res = new ComponentNoCache<E>(componentConfiguration, componentCacheManager, componentClass, key);
        } else {
            res = new ComponentCache<>(componentConfiguration, componentCacheManager, componentClass, key);
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

        INlsColumnHandler nlsColumnHandler = componentConfiguration.getNlsColumnHandler();
        if (nlsColumnHandler != null && ComponentMyBatisHelper.isUseNlsColumn(componentClass)) {
            res = new NlsColumnCache(nlsColumnHandler, res);
        }

        return res;
    }
}
