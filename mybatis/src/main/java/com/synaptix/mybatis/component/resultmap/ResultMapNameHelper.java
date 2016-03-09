package com.synaptix.mybatis.component.resultmap;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultMapNameHelper {

    private static final String RESULT_MAP_NAME = "resultMap";

    private static final String COMPONENT_CLASS_PAT = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final Pattern RESULT_MAP_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + RESULT_MAP_NAME);

    private ResultMapNameHelper() {
        super();
    }

    // ResultMap

    /**
     * Build key for result map
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildResultMapKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + RESULT_MAP_NAME;
    }

    /**
     * Is a result map key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isResultMapKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = RESULT_MAP_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInResultMapKey(String key) {
        if (!isResultMapKey(key)) {
            return null;
        }
        Matcher m = RESULT_MAP_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }
}
