package com.talanlabs.mybatis.component.cache;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractCacheFactory;
import com.talanlabs.mybatis.component.session.handler.INlsColumnHandler;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
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

    /**
     * Component cache manager
     *
     * @return
     */
    public ComponentCacheManager getComponentCacheManager() {
        return componentCacheManager;
    }

    @Override
    public boolean acceptKey(String key) {
        return CacheNameHelper.isCacheKey(key);
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

        com.talanlabs.entity.annotation.Cache cache = componentClass.getAnnotation(com.talanlabs.entity.annotation.Cache.class);

        Cache res;
        if (cache == null) {
            res = new ComponentNoCache<>(componentConfiguration, componentCacheManager, componentClass, key);
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
        if (nlsColumnHandler != null && ComponentMyBatisHelper.isAllUseNlsColumn(componentClass)) {
            res = new NlsColumnCache(nlsColumnHandler, res);
        }

        return res;
    }
}
