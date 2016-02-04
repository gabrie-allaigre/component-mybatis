package com.talanlabs.mybatis.component.statement.sqlsource;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DeleteSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(DeleteSqlSource.class);

    private final SqlSource sqlSource;

    public DeleteSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass) {
        super();

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(componentConfiguration);
        String sql = buildDelete(componentClass);
        sqlSource = sqlSourceParser.parse(sql, Map.class, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private String buildDelete(Class<E> componentClass) {
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
        sqlBuilder.DELETE_FROM(entity.name());
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
