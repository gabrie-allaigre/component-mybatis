package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StatementNameHelper {

    private static final String FIND_ENTITY_BY_ID_NAME = "findEntityById";

    private static final String FIND_COMPONENTS_BY_NAME = "findComponentsBy";

    private static final String FIND_COMPONENTS_BY_JOIN_TABLE_NAME = "findComponentsByJoinTable";

    private static final String PROPERTIES = "properties";

    private static final String SOURCE_PROPERTIES = "sourceProperties";

    private static final String TARGET_PROPERTIES = "targetProperties";

    private static final String SOURCE_COMPONENT = "sourceComponent";

    private static final String JOIN = "join";

    private static final String IGNORE_CANCEL = "ignoreCancel";

    private static final String PARAM = "";

    private static final String PROPERTIES_SEPARATOR = ",";

    private static final String COMPONENT_CLASS_PAT = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final String PROPERTIE_PAT = "[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final String PROPERTIES_PAT = "(" + PROPERTIE_PAT + PROPERTIES_SEPARATOR + ")*" + PROPERTIE_PAT;

    private static final String JOIN_PAT = PROPERTIE_PAT + ";" + PROPERTIES_PAT + ";" + PROPERTIES_PAT;

    private static final String JOINS_PAT = "(" + JOIN_PAT + "#)*" + JOIN_PAT;

    private static final Pattern FIND_ENTITY_BY_ID_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + FIND_ENTITY_BY_ID_NAME);

    private static final Pattern FIND_COMPONENTS_BY_PATTERN = Pattern
            .compile("(" + COMPONENT_CLASS_PAT + ")/" + FIND_COMPONENTS_BY_NAME + "\\?" + PROPERTIES + "=(" + PROPERTIES_PAT + ")(&(" + IGNORE_CANCEL + "))?");

    private static final Pattern FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN = Pattern.compile(
            "(" + COMPONENT_CLASS_PAT + ")/" + FIND_COMPONENTS_BY_JOIN_TABLE_NAME + "\\?" + SOURCE_COMPONENT + "=(" + COMPONENT_CLASS_PAT + ")&" + SOURCE_PROPERTIES + "=(" + PROPERTIES_PAT + ")&"
                    + TARGET_PROPERTIES + "=(" + PROPERTIES_PAT + ")&" + JOIN + "=(" + JOINS_PAT + ")(&(" + IGNORE_CANCEL + "))?");

    public static String buildParam(int i) {
        return PARAM + i;
    }

    // FindEntityById

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

    // FindComponentsBy

    public static <E extends IComponent> String buildFindComponentsByKey(Class<E> componentClass, boolean useCheckCancel, String... propertyNames) {
        if (componentClass == null || propertyNames == null || propertyNames.length == 0) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_COMPONENTS_BY_NAME + "?" + PROPERTIES + "=" + String.join(PROPERTIES_SEPARATOR, propertyNames) + (useCheckCancel ?
                "&" + IGNORE_CANCEL :
                "");
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
        return m.group(3).split(PROPERTIES_SEPARATOR);
    }

    public static boolean isIgnoreCancelInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        m.find();
        return IGNORE_CANCEL.equals(m.group(6));
    }

    // FindComponentsByJoinTable

    public static <E extends IComponent, F extends IComponent> String buildFindComponentsByJoinTableKey(Class<E> sourceComponentClass, Class<F> targetComponentClass, boolean useCheckCancel,
            List<Pair<String, Pair<String[], String[]>>> joins, String[] sourceProperties, String[] targetProperties) {
        if (sourceComponentClass == null || targetComponentClass == null || sourceProperties == null || sourceProperties.length == 0 || joins == null || joins.size() == 0 || targetProperties == null
                || targetProperties.length == 0) {
            return null;
        }
        List<String> js = joins.stream()
                .map(join -> join.getLeft() + ";" + String.join(PROPERTIES_SEPARATOR, join.getRight().getLeft()) + ";" + String.join(PROPERTIES_SEPARATOR, join.getRight().getRight()))
                .collect(Collectors.toList());
        return targetComponentClass.getCanonicalName() + "/" + FIND_COMPONENTS_BY_JOIN_TABLE_NAME + "?" + SOURCE_COMPONENT + "=" + sourceComponentClass.getCanonicalName() + "&" + SOURCE_PROPERTIES
                + "=" + String.join(PROPERTIES_SEPARATOR, sourceProperties) + "&" + TARGET_PROPERTIES + "=" + String.join(PROPERTIES_SEPARATOR, targetProperties) + "&" + JOIN + "=" + String
                .join("#", js) + (useCheckCancel ? "&" + IGNORE_CANCEL : "");
    }

    public static boolean isFindComponentsByJoinTableKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        return m.matches();
    }
}
