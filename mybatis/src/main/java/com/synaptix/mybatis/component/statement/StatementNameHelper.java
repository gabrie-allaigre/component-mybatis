package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementNameHelper {

    private static final String FIND_ENTITY_BY_ID_NAME = "findEntityById";

    private static final String FIND_COMPONENTS_BY_NAME = "findComponentsBy";

    private static final Pattern FIND_ENTITY_BY_ID_PATTERN = Pattern.compile("(([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)/" + FIND_ENTITY_BY_ID_NAME);

    private static final Pattern FIND_COMPONENTS_BY_PATTERN = Pattern
            .compile("(([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*)/" + FIND_COMPONENTS_BY_NAME + "\\?(([a-zA-Z_$][a-zA-Z\\d_$]*&)*[a-zA-Z_$][a-zA-Z\\d_$]*)");

    public static <E extends IComponent> String buildFindEntityByIdKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_ENTITY_BY_ID_NAME;
    }

    public static boolean isFindEntityByIdKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_ENTITY_BY_ID_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInFindEntityByIdKey(String key) {
        if (!isFindEntityByIdKey(key)) {
            return null;
        }
        Matcher m = FIND_ENTITY_BY_ID_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }

    public static <E extends IComponent> String buildFindComponentsByKey(Class<E> componentClass, String... propertyNames) {
        if (componentClass == null || propertyNames == null || propertyNames.length == 0) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_COMPONENTS_BY_NAME + "?" + String.join("&", propertyNames);
    }

    public static boolean isFindComponentsByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }

    public static String[] extractPropertyNamesInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        m.find();
        return m.group(3).split("&");
    }
}
