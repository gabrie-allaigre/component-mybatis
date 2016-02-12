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
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
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

        String sql = buildFindComponentsByPropertyName(componentClass, propertyNames);
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

    private String buildFindComponentsByPropertyName(Class<E> componentClass, String... propertyNames) {
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
        int param = 1;
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + propertyName);
            }
            if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);
            if (StringUtils.isBlank(column.name())) {
                throw new IllegalArgumentException("Not name in Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }

            Class<?> javaType = column.javaType() != null && column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();
            JdbcType jdbcType = column.jdbcType() != null && !JdbcType.UNDEFINED.equals(column.jdbcType()) ? column.jdbcType() : null;
            Class<? extends TypeHandler<?>> typeHandlerClass = column.typeHandler() != null && !UnknownTypeHandler.class.equals(column.typeHandler()) ? column.typeHandler() : null;

            sqlBuilder.WHERE("t." + column.name() + " = #{" + StatementNameHelper.buildParam(param) + ",javaType=" + javaType.getCanonicalName() + (jdbcType != null ?
                    ",jdbcType=" + jdbcType.name() :
                    "") + (typeHandlerClass != null ? ",typeHandler=" + typeHandlerClass.getCanonicalName() : "") + "}");
            param++;
        }
        if (ignoreCancel) {
            sqlBuilder.WHERE("t.check_cancel = #{checkCancel,javaType=java.lang.Boolean}");
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }
}