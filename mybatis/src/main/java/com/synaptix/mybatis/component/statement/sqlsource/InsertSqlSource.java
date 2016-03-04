package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
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

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(cd);

        String versionPropertyName = EntityHelper.findVersionPropertyName(cd.getComponentClass());
        if (versionPropertyName != null) {
            ComponentDescriptor.PropertyDescriptor versionPropertyDescriptor = cd.getPropertyDescriptor(versionPropertyName);
            ComponentMyBatisHelper.getVersionAnnotation(cd, versionPropertyDescriptor);

            component.straightSetProperty(versionPropertyName, 0);
        }

        SQL sqlBuilder = new SQL();
        sqlBuilder.INSERT_INTO(entity.name());
        for (ComponentDescriptor.PropertyDescriptor propertyDescriptor : cd.getPropertyDescriptors()) {
            if (component.straightGetProperty(propertyDescriptor.getPropertyName()) != null) {
                Column column = ComponentMyBatisHelper.getColumnAnnotation(cd, propertyDescriptor);
                sqlBuilder.VALUES(column.name(), ComponentMyBatisHelper.buildColumn(cd, propertyDescriptor, column));
            }
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }

}
