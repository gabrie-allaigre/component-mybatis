package com.talanlabs.mybatis.rsql.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RsqlStatementNameHelper {

    public static final String RSQL_NAME = "rsql";
    public static final String COUNT_RSQL_NAME = "countRsql";
    public static final Pattern RSQL_PATTERN = Pattern.compile("(" + StatementNameHelper.COMPONENT_CLASS_PAT + ")/" + RSQL_NAME);
    public static final Pattern COUNT_RSQL_PATTERN = Pattern.compile("(" + StatementNameHelper.COMPONENT_CLASS_PAT + ")/" + COUNT_RSQL_NAME);

    private RsqlStatementNameHelper() {
        super();
    }

    // Rsql

    /**
     * Build rsql key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildRsqlKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return ComponentMyBatisHelper.componentClassToString(componentClass) + "/" + RSQL_NAME;
    }

    /**
     * Verify is rsql key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isRsqlKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = RSQL_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInRsqlKey(String key) {
        if (!isRsqlKey(key)) {
            return null;
        }
        Matcher m = RSQL_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }

    // CountRsql

    /**
     * Build rsql key
     *
     * @param componentClass component class
     * @return key
     */
    public static <E extends IComponent> String buildCountRsqlKey(Class<E> componentClass) {
        if (componentClass == null) {
            return null;
        }
        return ComponentMyBatisHelper.componentClassToString(componentClass) + "/" + COUNT_RSQL_NAME;
    }

    /**
     * Verify is rsql key
     *
     * @param key key
     * @return true or false
     */
    public static boolean isCountRsqlKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        Matcher m = COUNT_RSQL_PATTERN.matcher(key);
        return m.matches();
    }

    /**
     * Extract component in the key
     *
     * @param key key
     * @return component class
     */
    public static <E extends IComponent> Class<E> extractComponentClassInCountRsqlKey(String key) {
        if (!isCountRsqlKey(key)) {
            return null;
        }
        Matcher m = COUNT_RSQL_PATTERN.matcher(key);
        if (!m.find()) {
            return null;
        }
        return ComponentMyBatisHelper.loadComponentClass(m.group(1));
    }
}
