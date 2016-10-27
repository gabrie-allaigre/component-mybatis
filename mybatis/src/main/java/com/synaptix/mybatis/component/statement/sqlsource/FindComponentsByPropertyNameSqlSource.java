package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancelable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindComponentsByPropertyNameSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByPropertyNameSqlSource.class);

    private final SqlSource sqlSource;

    private final boolean ignoreCancel;

    public FindComponentsByPropertyNameSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass, boolean ignoreCancel, String[] propertyNames,
            List<Pair<String, String>> orderBies) {
        super();

        this.ignoreCancel = ignoreCancel && ICancelable.class.isAssignableFrom(componentClass);

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(componentConfiguration);

        String sql = buildFindComponentsByPropertyName(componentClass, propertyNames, orderBies);
        sqlSource = sqlSourceParser.parse(sql, Map.class, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        if (ignoreCancel) {
            boundSql.setAdditionalParameter("canceled", false);
        }
        return boundSql;
    }

    private String buildFindComponentsByPropertyName(Class<E> componentClass, String[] propertyNames, List<Pair<String, String>> orderBies) {
        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);

        SQL sqlBuilder = new SQL();
        sqlBuilder.SELECT("t.*");
        sqlBuilder.FROM(entity.name() + " t");
        int param = 1;
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);
            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + propertyName);
            }

            Column column = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, propertyDescriptor);
            if (column == null) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            sqlBuilder.WHERE("t." + column.name() + " = " + ComponentMyBatisHelper.buildColumn(componentDescriptor, propertyDescriptor, column, StatementNameHelper.buildParam(param)));

            param++;
        }
        if (ignoreCancel) {
            sqlBuilder.WHERE("t.canceled = #{canceled,javaType=java.lang.Boolean}");
        }
        if (orderBies != null && !orderBies.isEmpty()) {
            for (Pair<String, String> orderBy : orderBies) {
                ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(orderBy.getLeft());

                if (propertyDescriptor == null) {
                    throw new IllegalArgumentException("Not exists property for Component=" + componentDescriptor.getComponentClass() + " with property=" + orderBy.getLeft());
                }

                Column column = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, propertyDescriptor);
                if (column == null) {
                    throw new IllegalArgumentException(
                            "Not present annotation Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }
                String sort;
                switch (orderBy.getRight()) {
                case "Desc":
                    sort = "DESC";
                    break;
                default:
                    sort = "ASC";
                    break;
                }
                sqlBuilder.ORDER_BY("t." + column.name() + " " + sort);
            }
        }
        String sql = sqlBuilder.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(sql);
        }
        return sql;
    }
}