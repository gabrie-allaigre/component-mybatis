package com.synaptix.mybatis.component.resultmap;

import com.synaptix.component.IComponent;
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

    public static <E extends IComponent> String buildResultMapKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + RESULT_MAP_NAME;
    }

    public static boolean isResultMapKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = RESULT_MAP_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInResultMapKey(String key) {
        if (!isResultMapKey(key)) {
            return null;
        }
        Matcher m = RESULT_MAP_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }
}
