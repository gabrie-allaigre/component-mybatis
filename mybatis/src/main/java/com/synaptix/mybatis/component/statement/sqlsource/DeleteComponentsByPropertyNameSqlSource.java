package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DeleteComponentsByPropertyNameSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(DeleteComponentsByPropertyNameSqlSource.class);

    private final SqlSource sqlSource;

    public DeleteComponentsByPropertyNameSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass, String... propertyNames) {
        super();

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(componentConfiguration);

        String sql = buildFindComponentsByPropertyName(componentClass, propertyNames);
        sqlSource = sqlSourceParser.parse(sql, Map.class, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    private String buildFindComponentsByPropertyName(Class<E> componentClass, String... propertyNames) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(cd);

        SQL sqlBuilder = new SQL();
        sqlBuilder.DELETE_FROM(entity.name() + " t");
        int param = 1;
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + propertyName);
            }

            Column column = ComponentMyBatisHelper.getColumnAnnotation(cd, propertyDescriptor);
            if (column == null) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            sqlBuilder.WHERE("t." + column.name() + " = " + ComponentMyBatisHelper.buildColumn(cd, propertyDescriptor, column, StatementNameHelper.buildParam(param)));

            param++;
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }
}