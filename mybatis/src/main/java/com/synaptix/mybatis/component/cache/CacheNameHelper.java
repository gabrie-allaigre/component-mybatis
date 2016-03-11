package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheNameHelper {

    private static final String CACHE_NAME = "cache";

    private static final String COMPONENT_CLASS_PAT = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final Pattern CACHE_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + CACHE_NAME);

    private CacheNameHelper() {
        super();
    }

    // Cache

    /**
     * Build cache key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildCacheKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + CACHE_NAME;
    }

    /**
     * Is a cache key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isCacheKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = CACHE_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInCacheKey(String key) {
        if (!isCacheKey(key)) {
            return null;
        }
        Matcher m = CACHE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }
}
