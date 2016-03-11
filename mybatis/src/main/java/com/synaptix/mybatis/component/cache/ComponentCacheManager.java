package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;

import java.util.*;

public class ComponentCacheManager {

    private ThreadLocal<Boolean> undispatchThreadLocal;
    private Map<Class<? extends IComponent>, Set<String>> linkMap;
    private Set<ICacheListener> cacheListeners;

    public ComponentCacheManager() {
        super();

        this.linkMap = Collections.synchronizedMap(new HashMap<>());
        this.undispatchThreadLocal = new ThreadLocal<>();
        this.cacheListeners = new HashSet<>();
    }

    /**
     * Add link
     *
     * @param classComponent     origin
     * @param linkClassComponent link
     */
    public synchronized void putCacheLink(Class<? extends IComponent> classComponent, Class<? extends IComponent> linkClassComponent) {
        Set<String> links = linkMap.get(classComponent);
        if (links == null) {
            links = new HashSet<>();
            linkMap.put(classComponent, links);
        }
        links.add(CacheNameHelper.buildCacheKey(linkClassComponent));
    }

    /**
     * All link for component
     *
     * @param classComponent component
     * @return links
     */
    public synchronized Set<String> getCacheLinks(Class<? extends IComponent> classComponent) {
        Set<String> links = linkMap.get(classComponent);
        if (links == null) {
            links = new HashSet<>();
            linkMap.put(classComponent, links);
        }
        return Collections.unmodifiableSet(links);
    }

    /**
     * Dispatch clear in current thread
     *
     * @return dispatch
     */
    public synchronized boolean isDispatch() {
        Boolean res = undispatchThreadLocal.get();
        return res == null || !res;
    }

    /**
     * Active dispatch for current thread
     */
    public synchronized void dispatch() {
        undispatchThreadLocal.remove();
    }

    /**
     * Desactive dispatch for current thread
     */
    public synchronized void undispatch() {
        undispatchThreadLocal.set(true);
    }

    /**
     * Add cache observer
     *
     * @param cacheListener cache observer
     */
    public synchronized void addCacheListener(ICacheListener cacheListener) {
        cacheListeners.add(cacheListener);
    }

    /**
     * Remove cache observer
     *
     * @param cacheListener cache observer
     */
    public synchronized void removeCacheListener(ICacheListener cacheListener) {
        cacheListeners.remove(cacheListener);
    }

    /**
     * Fire clear for id cache
     *
     * @param id cache
     */
    public synchronized void fireCleared(String id) {
        for (ICacheListener l : cacheListeners) {
            l.cleared(id);
        }
    }
}
