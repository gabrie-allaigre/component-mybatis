package com.talanlabs.mybatis.component.statement.sqlsource;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UpdateSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(UpdateSqlSource.class);

    private final Class<E> componentClass;

    private final SqlSourceBuilder sqlSourceParser;

    private final Set<String> nlsProperties;

    public UpdateSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass, String[] nlsProperties) {
        super();

        this.componentClass = componentClass;
        this.nlsProperties = new HashSet<>(Arrays.asList(nlsProperties));
        this.sqlSourceParser = new SqlSourceBuilder(componentConfiguration);
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
        ComponentDescriptor.PropertyDescriptor idPropertyDescriptor = cd.getPropertyDescriptor(idPropertyName);
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
                Column column = ComponentMyBatisHelper.getColumnAnnotation(cd, propertyDescriptor);
                if (column != null) {
                    sqlBuilder.SET(ComponentMyBatisHelper.buildSetColumn(cd, propertyDescriptor));
                } else {
                    NlsColumn nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(cd, propertyDescriptor);
                    if (nlsColumn != null && nlsProperties.contains(propertyDescriptor.getPropertyName())) {
                        sqlBuilder.SET(ComponentMyBatisHelper.buildSetNlsColumn(cd, propertyDescriptor));
                    }
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
