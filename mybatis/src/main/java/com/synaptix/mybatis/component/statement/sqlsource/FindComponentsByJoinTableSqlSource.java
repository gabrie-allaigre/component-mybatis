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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindComponentsByJoinTableSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByJoinTableSqlSource.class);

    private final SqlSource sqlSource;

    private final boolean ignoreCancel;

    public FindComponentsByJoinTableSqlSource(ComponentConfiguration componentConfiguration, Class<? extends IComponent> componentClass, Class<? extends IComponent> sourceComponentClass,
            String[] sourceProperties, String[] targetProperties, List<Pair<String, Pair<String[], String[]>>> joins, boolean ignoreCancel, List<Pair<String, String>> orderBies) {
        super();

        this.ignoreCancel = ignoreCancel && ICancelable.class.isAssignableFrom(componentClass);

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(componentConfiguration);

        String sql = buildFindComponentsByJoinTable(componentClass, sourceComponentClass, sourceProperties, targetProperties, joins, orderBies);
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

    private String buildFindComponentsByJoinTable(Class<? extends IComponent> componentClass, Class<? extends IComponent> sourceComponentClass, String[] sourceProperties, String[] targetProperties,
            List<Pair<String, Pair<String[], String[]>>> joins, List<Pair<String, String>> orderBies) {
        ComponentDescriptor<?> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        ComponentDescriptor<?> sourceComponentDescriptor = ComponentFactory.getInstance().getDescriptor(sourceComponentClass);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);

        SQL sqlBuilder = new SQL();
        sqlBuilder.SELECT("t.*");
        sqlBuilder.FROM(entity.name() + " t");
        int i = 0;
        for (int j = joins.size() - 1; j >= 0; j--) {
            Pair<String, Pair<String[], String[]>> join = joins.get(j);
            String[] rights = join.getRight().getRight();
            String label = "u" + (i + 1);

            List<String> ands = new ArrayList<>();
            if (j == joins.size() - 1) {
                for (int k = 0; k < targetProperties.length; k++) {
                    ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(targetProperties[k]);
                    if (propertyDescriptor == null) {
                        throw new IllegalArgumentException("Not exists property for Component=" + componentDescriptor.getComponentClass() + " with property=" + targetProperties[j]);
                    }

                    Column column = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, propertyDescriptor);
                    if (column == null) {
                        throw new IllegalArgumentException(
                                "Not present annotation Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                    }
                    ands.add(label + "." + rights[k] + " = t." + column.name());
                }
            } else {
                String[] lefts = join.getRight().getRight();
                for (int k = 0; k < lefts.length; k++) {
                    ands.add(label + "." + rights[k] + " = u" + i + "." + lefts[k]);
                }
            }

            sqlBuilder.INNER_JOIN(join.getLeft() + " " + label + " on " + String.join(" and ", ands));
            i++;
        }

        Pair<String, Pair<String[], String[]>> join = joins.get(0);
        String[] lefts = join.getRight().getLeft();

        int param = 1;
        for (int j = 0; j < sourceProperties.length; j++) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = sourceComponentDescriptor.getPropertyDescriptor(sourceProperties[j]);

            if (propertyDescriptor == null) {
                throw new IllegalArgumentException("Not exists property for Component=" + sourceComponentDescriptor.getComponentClass() + " with property=" + sourceProperties[j]);
            }

            Column column = ComponentMyBatisHelper.getColumnAnnotation(sourceComponentDescriptor, propertyDescriptor);
            if (column == null) {
                throw new IllegalArgumentException(
                        "Not present annotation Column for Component=" + sourceComponentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            sqlBuilder.WHERE("u" + i + "." + lefts[j] + " = " + ComponentMyBatisHelper.buildColumn(sourceComponentDescriptor, propertyDescriptor, column, StatementNameHelper.buildParam(param)));

            param++;
            j++;
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