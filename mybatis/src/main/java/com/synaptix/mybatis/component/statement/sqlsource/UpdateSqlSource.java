package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(UpdateSqlSource.class);

    private final Class<E> componentClass;

    private final SqlSourceBuilder sqlSourceParser;

    public UpdateSqlSource(Configuration configuration, Class<E> componentClass) {
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
            String sql = buildUpdate((E) parameterObject);
            Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
            return sqlSourceParser.parse(sql, parameterType, null);
        } catch (Exception e) {
            throw new BuilderException("Error invoking Count method for Update Cause: " + e, e);
        }
    }

    private String buildUpdate(E component) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(cd);

        String idPropertyName = EntityHelper.findIdPropertyName(cd.getComponentClass());
        if (StringUtils.isBlank(idPropertyName)) {
            throw new IllegalArgumentException("Not found annotation Id for Component=" + componentClass);
        }
        ComponentDescriptor.PropertyDescriptor idPropertyDescriptor = null;
        idPropertyDescriptor = cd.getPropertyDescriptor(idPropertyName);
        if (idPropertyDescriptor == null) {
            throw new IllegalArgumentException("Not found annotation Id for Component=" + componentClass);
        }

        String versionPropertyName = EntityHelper.findVersionPropertyName(cd.getComponentClass());
        ComponentDescriptor.PropertyDescriptor versionPropertyDescriptor = null;
        String versionSetColumn = null;
        if (versionPropertyName != null) {
            versionPropertyDescriptor = cd.getPropertyDescriptor(versionPropertyName);

            versionSetColumn = ComponentMyBatisHelper.buildSetVersionColumn(cd, versionPropertyDescriptor);
        }

        SQL sqlBuilder = new SQL();
        sqlBuilder.UPDATE(entity.name());
        for (ComponentDescriptor.PropertyDescriptor propertyDescriptor : cd.getPropertyDescriptors()) {
            if (propertyDescriptor != idPropertyDescriptor && propertyDescriptor != versionPropertyDescriptor) {
                String c = ComponentMyBatisHelper.buildSetColumn(cd, propertyDescriptor);
                if (c != null) {
                    sqlBuilder.SET(c);
                }
            }
        }
        if (versionPropertyDescriptor != null && versionSetColumn != null) {
            sqlBuilder.SET(versionSetColumn + " + 1");
        }
        String i = ComponentMyBatisHelper.buildSetIdColumn(cd, idPropertyDescriptor);
        if (i == null) {
            throw new IllegalArgumentException("Not found annotation column for Component=" + componentClass + " property=" + idPropertyDescriptor.getPropertyName());
        }
        sqlBuilder.WHERE(i);
        if (versionPropertyDescriptor != null && versionSetColumn != null) {
            sqlBuilder.WHERE(versionSetColumn);
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }

}
