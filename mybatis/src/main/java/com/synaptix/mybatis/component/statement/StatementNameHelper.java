package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StatementNameHelper {

    private static final String FIND_ENTITY_BY_ID_NAME = "findEntityById";

    private static final String FIND_COMPONENTS_BY_NAME = "findComponentsBy";

    private static final String FIND_COMPONENTS_BY_JOIN_TABLE_NAME = "findComponentsByJoinTable";

    private static final String INSERT_NAME = "insert";

    private static final String UPDATE_NAME = "update";

    private static final String DELETE_NAME = "delete";

    private static final String NLS_NAME = "nls";

    private static final String PROPERTIES = "properties";

    private static final String SOURCE_PROPERTIES = "sourceProperties";

    private static final String TARGET_PROPERTIES = "targetProperties";

    private static final String SOURCE_COMPONENT = "sourceComponent";

    private static final String JOIN = "join";

    private static final String IGNORE_CANCEL = "ignoreCancel";

    private static final String PARAM = "";

    private static final String PROPERTIES_SEPARATOR = ",";

    private static final String COMPONENT_CLASS_PAT = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final String PROPERTY_PAT = "[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final String PROPERTIES_PAT = "(" + PROPERTY_PAT + PROPERTIES_SEPARATOR + ")*" + PROPERTY_PAT;

    private static final String JOIN_PAT = PROPERTY_PAT + ";" + PROPERTIES_PAT + ";" + PROPERTIES_PAT;

    private static final String JOINS_PAT = "(" + JOIN_PAT + "#)*" + JOIN_PAT;

    private static final Pattern FIND_ENTITY_BY_ID_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + FIND_ENTITY_BY_ID_NAME);

    private static final Pattern FIND_COMPONENTS_BY_PATTERN = Pattern
            .compile("(" + COMPONENT_CLASS_PAT + ")/" + FIND_COMPONENTS_BY_NAME + "\\?" + PROPERTIES + "=(" + PROPERTIES_PAT + ")(&(" + IGNORE_CANCEL + "))?");

    private static final Pattern FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN = Pattern.compile(
            "(" + COMPONENT_CLASS_PAT + ")/" + FIND_COMPONENTS_BY_JOIN_TABLE_NAME + "\\?" + SOURCE_COMPONENT + "=(" + COMPONENT_CLASS_PAT + ")&" + SOURCE_PROPERTIES + "=(" + PROPERTIES_PAT + ")&"
                    + TARGET_PROPERTIES + "=(" + PROPERTIES_PAT + ")&" + JOIN + "=(" + JOINS_PAT + ")(&(" + IGNORE_CANCEL + "))?");

    private static final Pattern INSERT_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + INSERT_NAME);

    private static final Pattern UPDATE_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + UPDATE_NAME);

    private static final Pattern DELETE_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + DELETE_NAME);

    private static final Pattern NLS_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + NLS_NAME + "\\?");

    private StatementNameHelper() {
        super();
    }

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

    public static String extractComponentNameInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }

    public static String extractSourceComponentNameInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        return m.group(3);
    }

    public static String[] extractSourcePropertiesInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        return m.group(5).split(PROPERTIES_SEPARATOR);
    }

    public static String[] extractTargetPropertiesInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        return m.group(7).split(PROPERTIES_SEPARATOR);
    }

    public static List<Pair<String, Pair<String[], String[]>>> extractJoinInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        String[] joins = m.group(9).split("#");
        List<Pair<String, Pair<String[], String[]>>> res = new ArrayList<>();
        for (String join : joins) {
            String[] ss = join.split(";");
            res.add(Pair.of(ss[0], Pair.of(ss[1].split(PROPERTIES_SEPARATOR), ss[2].split(PROPERTIES_SEPARATOR))));
        }
        return res;
    }

    public static boolean isIgnoreCancelInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        m.find();
        return IGNORE_CANCEL.equals(m.group(16));
    }

    // Insert

    public static <E extends IComponent> String buildInsertKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + INSERT_NAME;
    }

    public static boolean isInsertKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = INSERT_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInInsertKey(String key) {
        if (!isInsertKey(key)) {
            return null;
        }
        Matcher m = INSERT_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }

    // Update

    public static <E extends IComponent> String buildUpdateKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + UPDATE_NAME;
    }

    public static boolean isUpdateKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = UPDATE_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInUpdateKey(String key) {
        if (!isUpdateKey(key)) {
            return null;
        }
        Matcher m = UPDATE_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }

    // Delete

    public static <E extends IComponent> String buildDeleteKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + DELETE_NAME;
    }

    public static boolean isDeleteKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = DELETE_PATTERN.matcher(key);
        return m.matches();
    }

    public static String extractComponentNameInDeleteKey(String key) {
        if (!isDeleteKey(key)) {
            return null;
        }
        Matcher m = DELETE_PATTERN.matcher(key);
        m.find();
        return m.group(1);
    }
}
