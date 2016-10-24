package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
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

    private static final String DELETE_ENTITY_BY_ID_NAME = "deleteEntityById";

    private static final String DELETE_COMPONENTS_BY_NAME = "deleteComponentsBy";

    private static final String FIND_NLS_COLUMN_NAME = "findNlsColumn";

    private static final String PROPERTY = "property";

    private static final String PROPERTIES = "properties";

    private static final String SOURCE_PROPERTIES = "sourceProperties";

    private static final String TARGET_PROPERTIES = "targetProperties";

    private static final String SOURCE_COMPONENT = "sourceComponent";

    private static final String NLS_PROPERTIES = "nlsProperties";

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

    private static final Pattern UPDATE_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + UPDATE_NAME + "(\\?" + NLS_PROPERTIES + "=(" + PROPERTIES_PAT + ")?)?");

    private static final Pattern DELETE_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + DELETE_NAME);

    private static final Pattern DELETE_ENTITY_BY_ID_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + DELETE_ENTITY_BY_ID_NAME);

    private static final Pattern DELETE_COMPONENTS_BY_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + DELETE_COMPONENTS_BY_NAME + "\\?" + PROPERTIES + "=(" + PROPERTIES_PAT + ")");

    private static final Pattern FIND_NLS_COLUMN_PATTERN = Pattern.compile("(" + COMPONENT_CLASS_PAT + ")/" + FIND_NLS_COLUMN_NAME + "\\?" + PROPERTY + "=(" + PROPERTY_PAT + ")");

    private StatementNameHelper() {
        super();
    }

    /**
     * Get a param
     *
     * @param i number
     * @return param + number
     */
    public static String buildParam(int i) {
        return PARAM + i;
    }

    // FindEntityById

    /**
     * Build find entity by id key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildFindEntityByIdKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_ENTITY_BY_ID_NAME;
    }

    /**
     * Verify is find entity by id key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isFindEntityByIdKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_ENTITY_BY_ID_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInFindEntityByIdKey(String key) {
        if (!isFindEntityByIdKey(key)) {
            return null;
        }
        Matcher m = FIND_ENTITY_BY_ID_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    // FindComponentsBy

    /**
     * Build find components by id key
     *
     * @param componentClass component class
     * @param useCheckCancel use check cancel
     * @param propertyNames  array of property
     * @return key
     */
    public static <E extends IComponent> String buildFindComponentsByKey(Class<E> componentClass, boolean useCheckCancel, String... propertyNames) {
        if (componentClass == null || propertyNames == null || propertyNames.length == 0) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_COMPONENTS_BY_NAME + "?" + PROPERTIES + "=" + String.join(PROPERTIES_SEPARATOR, propertyNames) + (useCheckCancel ?
                "&" + IGNORE_CANCEL :
                "");
    }

    /**
     * Verify is find components by
     *
     * @param key key
     * @return true or false
     */
    public static boolean isFindComponentsByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    /**
     * Extract properties
     *
     * @param key key
     * @return properties
     */
    public static String[] extractPropertyNamesInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return m.group(3).split(PROPERTIES_SEPARATOR);
    }

    /**
     * Extract ignore cancel
     *
     * @param key key
     * @return true or false
     */
    public static boolean isIgnoreCancelInFindComponentsByKey(String key) {
        if (!isFindComponentsByKey(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_PATTERN.matcher(key);
        return m.find() && IGNORE_CANCEL.equals(m.group(6));
    }

    // FindComponentsByJoinTable

    /**
     * Create a key for Find a components by join table
     *
     * @param sourceComponentClass source component class
     * @param targetComponentClass target component class
     * @param useCheckCancel       use check cancel
     * @param joins                list of join
     * @param sourceProperties     properties source
     * @param targetProperties     properties target
     * @return key
     */
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

    /**
     * Verify is find components by join table key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isFindComponentsByJoinTableKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    /**
     * Extract source component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractSourceComponentClassInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(3));
    }

    /**
     * Extract source properties
     *
     * @param key key
     * @return properties
     */
    public static String[] extractSourcePropertiesInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return m.group(5).split(PROPERTIES_SEPARATOR);
    }

    /**
     * Extract target properties
     *
     * @param key key
     * @return properties
     */
    public static String[] extractTargetPropertiesInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return m.group(7).split(PROPERTIES_SEPARATOR);
    }

    /**
     * Extract join table
     *
     * @param key key
     * @return list of join
     */
    public static List<Pair<String, Pair<String[], String[]>>> extractJoinInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return null;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        String[] joins = m.group(9).split("#");
        List<Pair<String, Pair<String[], String[]>>> res = new ArrayList<>();
        for (String join : joins) {
            String[] ss = join.split(";");
            res.add(Pair.of(ss[0], Pair.of(ss[1].split(PROPERTIES_SEPARATOR), ss[2].split(PROPERTIES_SEPARATOR))));
        }
        return res;
    }

    /**
     * Extract ignore cancel
     *
     * @param key key
     * @return true or false
     */
    public static boolean isIgnoreCancelInFindComponentsByJoinTableKey(String key) {
        if (!isFindComponentsByJoinTableKey(key)) {
            return false;
        }
        Matcher m = FIND_COMPONENTS_BY_JOIN_TABLE_PATTERN.matcher(key);
        return m.find() && IGNORE_CANCEL.equals(m.group(16));
    }

    // Insert

    /**
     * Build a insert key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildInsertKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + INSERT_NAME;
    }

    /**
     * Verify is insert key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isInsertKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = INSERT_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInInsertKey(String key) {
        if (!isInsertKey(key)) {
            return null;
        }
        Matcher m = INSERT_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    // Update

    /**
     * Build update key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildUpdateKey(Class<E> componentClass, String... nlsPropertyNames) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + UPDATE_NAME + "?" + NLS_PROPERTIES + "=" + String.join(PROPERTIES_SEPARATOR, nlsPropertyNames);
    }

    /**
     * Verify is update key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isUpdateKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = UPDATE_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInUpdateKey(String key) {
        if (!isUpdateKey(key)) {
            return null;
        }
        Matcher m = UPDATE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    /**
     * Extract source properties
     *
     * @param key key
     * @return properties
     */
    public static String[] extractNlsPropertiesInUpdateKey(String key) {
        if (!isUpdateKey(key)) {
            return null;
        }
        Matcher m = UPDATE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        String properties = m.group(4);
        return properties != null ? properties.split(PROPERTIES_SEPARATOR) : new String[0];
    }

    // Delete

    /**
     * Build delete key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildDeleteKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + DELETE_NAME;
    }

    /**
     * Verify is delete key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isDeleteKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = DELETE_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInDeleteKey(String key) {
        if (!isDeleteKey(key)) {
            return null;
        }
        Matcher m = DELETE_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    // NlsColumn

    /**
     * Build nls key
     *
     * @param componentClass component class
     * @param property       property
     * @return key
     */
    public static <E extends IComponent> String buildFindNlsColumnKey(Class<E> componentClass, String property) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + FIND_NLS_COLUMN_NAME + "?" + PROPERTY + "=" + property;
    }

    /**
     * Verify is nls key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isFindNlsColumnKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = FIND_NLS_COLUMN_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInFindNlsColumnKey(String key) {
        if (!isFindNlsColumnKey(key)) {
            return null;
        }
        Matcher m = FIND_NLS_COLUMN_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    /**
     * Extract property
     *
     * @param key key
     * @return properties
     */
    public static String extractPropertyNameInFindNlsColumnByKey(String key) {
        if (!isFindNlsColumnKey(key)) {
            return null;
        }
        Matcher m = FIND_NLS_COLUMN_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return m.group(3);
    }

    // DeleteEntityById

    /**
     * Build delete entity by id key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildDeleteEntityByIdKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + DELETE_ENTITY_BY_ID_NAME;
    }

    /**
     * Verify is delete entity by id key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isDeleteEntityByIdKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = DELETE_ENTITY_BY_ID_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInDeleteEntityByIdKey(String key) {
        if (!isDeleteEntityByIdKey(key)) {
            return null;
        }
        Matcher m = DELETE_ENTITY_BY_ID_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    // DeleteComponentsBy

    /**
     * Build delete components by
     *
     * @param componentClass component class
     * @param propertyNames  array of property
     * @return key
     */
    public static <E extends IComponent> String buildDeleteComponentsByKey(Class<E> componentClass, String... propertyNames) {
        if (componentClass == null || propertyNames == null || propertyNames.length == 0) {
            return null;
        }
        return componentClass.getCanonicalName() + "/" + DELETE_COMPONENTS_BY_NAME + "?" + PROPERTIES + "=" + String.join(PROPERTIES_SEPARATOR, propertyNames);
    }

    /**
     * Verify is delete components by
     *
     * @param key key
     * @return true or false
     */
    public static boolean isDeleteComponentsByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = DELETE_COMPONENTS_BY_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInDeleteComponentsByKey(String key) {
        if (!isDeleteComponentsByKey(key)) {
            return null;
        }
        Matcher m = DELETE_COMPONENTS_BY_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    /**
     * Extract properties
     *
     * @param key key
     * @return properties
     */
    public static String[] extractPropertyNamesInDeleteComponentsByKey(String key) {
        if (!isDeleteComponentsByKey(key)) {
            return null;
        }
        Matcher m = DELETE_COMPONENTS_BY_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return m.group(3).split(PROPERTIES_SEPARATOR);
    }

}
