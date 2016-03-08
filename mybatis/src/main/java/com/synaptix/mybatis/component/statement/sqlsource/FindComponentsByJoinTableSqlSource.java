package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
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

    public FindComponentsByJoinTableSqlSource(Configuration configuration, Class<? extends IComponent> componentClass, Class<? extends IComponent> sourceComponentClass, String[] sourceProperties,
            String[] targetProperties, List<Pair<String, Pair<String[], String[]>>> joins, boolean ignoreCancel) {
        super();

        this.ignoreCancel = ignoreCancel && ICancellable.class.isAssignableFrom(componentClass);

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);

        String sql = buildFindComponentsByJoinTable(componentClass, sourceComponentClass, sourceProperties, targetProperties, joins);
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

    private String buildFindComponentsByJoinTable(Class<? extends IComponent> componentClass, Class<? extends IComponent> sourceComponentClass, String[] sourceProperties, String[] targetProperties,
            List<Pair<String, Pair<String[], String[]>>> joins) {
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
                    ComponentDescriptor.PropertyDescriptor propertyDescriptor = sourceComponentDescriptor.getPropertyDescriptor(targetProperties[k]);
                    if (propertyDescriptor == null) {
                        throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + targetProperties[j]);
                    }

                    Column column = ComponentMyBatisHelper.getColumnAnnotation(sourceComponentDescriptor, propertyDescriptor);
                    if (column == null) {
                        throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
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
                throw new IllegalArgumentException("Not exists property for Component=" + componentClass + " with property=" + sourceProperties[j]);
            }

            Column column = ComponentMyBatisHelper.getColumnAnnotation(sourceComponentDescriptor, propertyDescriptor);
            if (column == null) {
                throw new IllegalArgumentException("Not present annotation Column for Component=" + componentClass + " with property=" + propertyDescriptor.getPropertyName());
            }
            sqlBuilder.WHERE("u" + i + "." + lefts[j] + " = " + ComponentMyBatisHelper.buildColumn(sourceComponentDescriptor, propertyDescriptor, column, StatementNameHelper.buildParam(param)));

            param++;
            j++;
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