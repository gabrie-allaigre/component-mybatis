package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;

import java.util.*;

public class ComponentCacheManager {

    private Map<Class<? extends IComponent>, Set<Class<? extends IComponent>>> linkMap;

    public ComponentCacheManager() {
        super();

        this.linkMap = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized void putAdd(Class<? extends IComponent> classComponent, Class<? extends IComponent> linkClassComponent) {
        Set<Class<? extends IComponent>> links = linkMap.get(classComponent);
        if (links == null) {
            links = new HashSet<>();
            linkMap.put(classComponent, links);
        }
        links.add(linkClassComponent);
    }

    public synchronized Set<Class<? extends IComponent>> get(Class<? extends IComponent> classComponent) {
        Set<Class<? extends IComponent>> links = linkMap.get(classComponent);
        if (links == null) {
            links = new HashSet<>();
            linkMap.put(classComponent, links);
        }
        return Collections.unmodifiableSet(links);
    }
}
