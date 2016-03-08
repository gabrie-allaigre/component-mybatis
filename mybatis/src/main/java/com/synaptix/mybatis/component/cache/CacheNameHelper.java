package com.synaptix.mybatis.component.cache;

import com.synaptix.component.IComponent;
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

    public static <E extends IComponent> String buildCacheKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + CACHE_NAME;
    }

    public static boolean isCacheKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = CACHE_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInCacheKey(String key) {
        if (!isCacheKey(key)) {
            return null;
        }
        Matcher m = CACHE_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }
}
