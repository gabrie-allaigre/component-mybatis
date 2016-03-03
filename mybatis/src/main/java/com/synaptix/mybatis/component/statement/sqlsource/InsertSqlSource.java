package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
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

public class InsertSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(InsertSqlSource.class);

    private final Class<E> componentClass;

    private final SqlSourceBuilder sqlSourceParser;

    public InsertSqlSource(Configuration configuration, Class<E> componentClass) {
        super();

        this.componentClass = componentClass;
        this.sqlSourceParser = new SqlSourceBuilder(configuration);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        SqlSource sqlSource = createSqlSource(parameterObject);
        return sqlSource.getBoundSql(parameterObject);
    }

    @SuppressWarnings("unchecked")
    private SqlSource createSqlSource(Object parameterObject) {
        try {
            String sql = buildInsert((E) parameterObject);
            Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
            return sqlSourceParser.parse(sql, parameterType, null);
        } catch (Exception e) {
            throw new BuilderException("Error invoking Count method for Insert Cause: " + e, e);
        }
    }

    private String buildInsert(E component) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        if (!componentClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Not found annotation Entity for Component=" + componentClass);
        }
        Entity entity = componentClass.getAnnotation(Entity.class);
        if (StringUtils.isBlank(entity.name())) {
            throw new IllegalArgumentException("Not name in Entity for Component=" + componentClass);
        }

        SQL sqlBuilder = new SQL();
        sqlBuilder.INSERT_INTO(entity.name());
        for (ComponentDescriptor.PropertyDescriptor propertyDescriptor : cd.getPropertyDescriptors()) {
            if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);
            if (StringUtils.isBlank(column.name())) {
                throw new IllegalArgumentException("Not name in Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }

            if (component.straightGetProperty(propertyDescriptor.getPropertyName()) != null) {
                // we don't save null values (so that we are subject to get
                // the default value)

                Class<?> javaType = column.javaType() != null && column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();
                JdbcType jdbcType = column.jdbcType() != null && !JdbcType.UNDEFINED.equals(column.jdbcType()) ? column.jdbcType() : null;
                Class<? extends TypeHandler<?>> typeHandlerClass = column.typeHandler() != null && !UnknownTypeHandler.class.equals(column.typeHandler()) ? column.typeHandler() : null;

                sqlBuilder.VALUES(column.name(), "#{" + propertyDescriptor.getPropertyName() + ",javaType=" + javaType.getCanonicalName() + (jdbcType != null ? ",jdbcType=" + jdbcType.name() : "") + (
                        typeHandlerClass != null ?
                                ",typeHandler=" + typeHandlerClass.getCanonicalName() :
                                "") + "}");
            }
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }

}
