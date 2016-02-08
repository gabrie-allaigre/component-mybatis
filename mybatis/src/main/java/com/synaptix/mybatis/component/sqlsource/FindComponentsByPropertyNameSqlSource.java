package com.synaptix.mybatis.component.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IId;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;

public class FindComponentsByPropertyNameSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByPropertyNameSqlSource.class);

    private final SqlSource sqlSource;

    public FindComponentsByPropertyNameSqlSource(Configuration configuration, Class<E> componentClass, String propertyName, boolean useCheckCancel) {
        this(configuration, componentClass, null, propertyName, useCheckCancel);
    }

    public FindComponentsByPropertyNameSqlSource(Configuration configuration, Class<E> componentClass, String idSource, String propertyName, boolean useCheckCancel) {
        this(configuration, componentClass, null, idSource, null, propertyName, useCheckCancel);
    }

    public FindComponentsByPropertyNameSqlSource(Configuration configuration, Class<E> componentClass, String idSource, String idTarget, String propertyName, boolean useCheckCancel) {
        this(configuration, componentClass, null, idSource, idTarget, propertyName, useCheckCancel);
    }

    public FindComponentsByPropertyNameSqlSource(Configuration configuration, Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, String propertyName,
            boolean useCheckCancel) {
        super();

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);

        String sql;
        if (idTarget == null) {
            sql = buildFindComponentsByPropertyName(componentClass, propertyName, useCheckCancel);
        } else if (assoSqlTableName == null) {
            sql = buildFindComponentsByPropertyName2(componentClass, idTarget, useCheckCancel);
        } else {
            sql = buildFindComponentsByPropertyName(componentClass, assoSqlTableName, idSource, idTarget, useCheckCancel);
        }
        sqlSource = sqlSourceParser.parse(sql, IId.class, null);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private <E extends IComponent> String buildFindComponentsByPropertyName(Class<E> componentClass, String propertyName, boolean useCheckCancel) {
        ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
        if (!ed.getPropertyNames().contains(propertyName)) {
            throw new RuntimeException("buildFindComponentByIdPropertyName" + " in " + ed.getComponentClass().getSimpleName() + " is not a property " + propertyName, null);
        }

        ComponentDescriptor.PropertyDescriptor pd = ed.getPropertyDescriptor(propertyName);
        Column column = pd.getMethod().getAnnotation(Column.class);
        if (column == null) {
            throw new RuntimeException("buildFindComponentByIdPropertyName" + " in " + ed.getComponentClass().getSimpleName() + " is not a column for property " + propertyName, null);
        }

        return buildFindComponentsByPropertyName2(componentClass, column.name(), useCheckCancel);
    }

    private <E extends IComponent> String buildFindComponentsByPropertyName2(Class<E> componentClass, String column, boolean useCheckCancel) {
        Entity entity = componentClass.getAnnotation(Entity.class);

        SQL sqlBuilder = new SQL();
        sqlBuilder.SELECT("t.*");
        sqlBuilder.FROM(entity.name() + " t");
        sqlBuilder.WHERE("t." + column + " = #{value}");
        if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
            sqlBuilder.AND();
            sqlBuilder.WHERE("t.check_cancel = #{checkCancel}");
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }

    private <E extends IComponent> String buildFindComponentsByPropertyName(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, boolean useCheckCancel) {
        Entity entity = componentClass.getAnnotation(Entity.class);

        SQL sqlBuilder = new SQL();
        sqlBuilder.SELECT("t.*");
        sqlBuilder.FROM(entity.name() + " t");
        sqlBuilder.INNER_JOIN(assoSqlTableName + " a on a." + idTarget + " = t.ID");
        sqlBuilder.WHERE("a." + idSource + " = #{value}");
        if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
            sqlBuilder.AND();
            sqlBuilder.WHERE("t.check_cancel = #{checkCancel}");
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }
}