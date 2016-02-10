package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FindComponentsByPropertyNameSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByPropertyNameSqlSource.class);

    private final SqlSource sqlSource;

    private final boolean ignoreCancel;

    public FindComponentsByPropertyNameSqlSource(Configuration configuration, Class<E> componentClass, boolean ignoreCancel, String... propertyNames) {
        super();

        this.ignoreCancel = ignoreCancel && ICancellable.class.isAssignableFrom(componentClass);

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);

        String sql = buildFindComponentsByPropertyName(componentClass, ignoreCancel, propertyNames);
        sqlSource = sqlSourceParser.parse(sql, Map.class, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        if (ignoreCancel) {
            boundSql.setAdditionalParameter("checkCancel", false);
        }
        return boundSql;
    }

    private String buildFindComponentsByPropertyName(Class<E> componentClass, boolean useCheckCancel, String... propertyNames) {
        ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        if (!componentClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Not found annotation Entity for Component=" + componentClass);
        }
        Entity entity = componentClass.getAnnotation(Entity.class);
        if (StringUtils.isBlank(entity.name())) {
            throw new IllegalArgumentException("Not name in Entity for Component=" + componentClass);
        }

        SQL sqlBuilder = new SQL();
        sqlBuilder.SELECT("t.*");
        sqlBuilder.FROM(entity.name() + " t");
        int i = 0;
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (StringUtils.isBlank(propertyDescriptor.getMethod().getAnnotation(Column.class).name())) {
                throw new IllegalArgumentException("Not name in Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }

            String column = propertyDescriptor.getMethod().getAnnotation(Column.class).name();
            sqlBuilder.WHERE("t." + column + " = #{" + StatementNameHelper.buildParam(i) + ",javaType=" + propertyDescriptor.getPropertyClass().getCanonicalName() + "}");
            i++;
        }
        if (useCheckCancel) {
            sqlBuilder.WHERE("t.check_cancel = #{checkCancel,javaType=java.lang.Boolean}");
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }
}