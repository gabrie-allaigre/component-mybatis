package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.JoinTable;
import com.talanlabs.entity.annotation.OrderBy;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ComponentResultMapHelper {

    private ComponentResultMapHelper() {
        super();
    }

    /**
     * Join tables
     *
     * @param componentDescriptor
     * @param propertyDescriptor
     * @param joinTables
     * @param propertySource
     * @param propertyTarget
     * @return
     */
    public static List<Pair<String, Pair<String[], String[]>>> joinTables(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, JoinTable[] joinTables,
            String[] propertySource, String[] propertyTarget) {
        List<Pair<String, Pair<String[], String[]>>> joins = new ArrayList<>();
        int i = 0;
        for (JoinTable joinTable : joinTables) {
            if (StringUtils.isBlank(joinTable.name())) {
                throw new IllegalArgumentException("JoinTable is empty for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (joinTable.left().length == 0) {
                throw new IllegalArgumentException(
                        "JoinTable join=" + joinTable.name() + " left is empty for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (joinTable.right().length == 0) {
                throw new IllegalArgumentException(
                        "JoinTable join=" + joinTable.name() + " right is empty for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            if (i == 0) {
                if (propertySource.length != joinTable.left().length) {
                    throw new IllegalArgumentException(
                            "JoinTable join=" + joinTable.name() + " left different size propertySource for Component=" + componentDescriptor.getComponentClass() + " with property="
                                    + propertyDescriptor.getPropertyName());
                }
            }
            if (i == joinTables.length - 1) {
                if (propertyTarget.length != joinTable.right().length) {
                    throw new IllegalArgumentException(
                            "JoinTable join=" + joinTable.name() + " right different size propertyTarget for Component=" + componentDescriptor.getComponentClass() + " with property="
                                    + propertyDescriptor.getPropertyName());
                }
            }
            if (i > 0 && i < joinTables.length) {
                if (joinTables[i - 1].right().length != joinTable.left().length) {
                    throw new IllegalArgumentException(
                            "JoinTable join=" + joinTable.name() + " left different size with previous join for Component=" + componentDescriptor.getComponentClass() + " with property="
                                    + propertyDescriptor.getPropertyName());
                }
            }

            joins.add(Pair.of(joinTable.name(), Pair.of(joinTable.left(), joinTable.right())));

            i++;
        }
        return joins;
    }

    /**
     * Build composites result
     *
     * @param configuration
     * @param sourceColumns
     * @return
     */
    public static List<ResultMapping> buildComposites(Configuration configuration, List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns) {
        List<ResultMapping> composites = new ArrayList<>();
        if (sourceColumns.size() > 1) {
            int param = 1;
            for (Pair<ComponentDescriptor.PropertyDescriptor, String> sourceColumn : sourceColumns) {
                ComponentDescriptor.PropertyDescriptor pd = sourceColumn.getLeft();
                composites.add(new ResultMapping.Builder(configuration, StatementNameHelper.buildParam(param), sourceColumn.getRight(), pd.getPropertyClass()).build());
                param++;
            }
        }
        return composites;
    }

    /**
     * Check target properties
     *
     * @param componentDescriptor
     * @param propertyTarget
     */
    public static void checkTarget(ComponentDescriptor<?> componentDescriptor, String[] propertyTarget) {
        if (propertyTarget == null || propertyTarget.length == 0) {
            throw new IllegalArgumentException("propertyTarget is null or empty");
        }

        for (String propertyName : propertyTarget) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);
            checkColumn(componentDescriptor, propertyDescriptor);
        }
    }

    /**
     * Prepare sources properties
     *
     * @param componentDescriptor
     * @param propertySource
     * @return
     */
    public static List<Pair<ComponentDescriptor.PropertyDescriptor, String>> prepareSourceColumns(ComponentDescriptor<?> componentDescriptor, String[] propertySource) throws IllegalArgumentException {
        if (propertySource == null || propertySource.length == 0) {
            throw new IllegalArgumentException("propertySource is null or empty");
        }

        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> columnNames = new ArrayList<>();
        for (String propertyName : propertySource) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);
            checkColumn(componentDescriptor, propertyDescriptor);

            columnNames.add(Pair.of(propertyDescriptor, propertyDescriptor.getMethod().getAnnotation(Column.class).name()));
        }
        return columnNames;
    }

    /**
     * Check column
     *
     * @param componentDescriptor component descriptor
     * @param propertyDescriptor  property descriptor
     */
    public static void checkColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Not exists property for Component=" + componentDescriptor.getComponentClass());
        }
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Not present annotation Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (StringUtils.isBlank(propertyDescriptor.getMethod().getAnnotation(Column.class).name())) {
            throw new IllegalArgumentException("Not name in Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
    }

    /**
     * Get columnNames for properties
     *
     * @param componentDescriptor component
     * @param properties          properties names
     * @return get property names and columns names
     */
    public static Pair<String[], String[]> prepareColumns(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, String[] properties,
            boolean addIdIfEmpty) throws IllegalArgumentException {
        if (properties == null || properties.length == 0) {
            if (addIdIfEmpty) {
                String idPropertyName = EntityHelper.findIdPropertyName(componentDescriptor.getComponentClass());
                if (idPropertyName == null) {
                    throw new IllegalArgumentException(
                            "Not find Id property for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + ", fill propertyTarget");
                }
                properties = new String[] { idPropertyName };
            } else {
                throw new IllegalArgumentException("propertySource is null or empty");
            }
        }

        List<String> columnNames = new ArrayList<>();
        for (String propertyName : properties) {
            ComponentDescriptor.PropertyDescriptor pd = componentDescriptor.getPropertyDescriptor(propertyName);
            checkColumn(componentDescriptor, pd);

            columnNames.add(pd.getMethod().getAnnotation(Column.class).name());
        }
        return Pair.of(properties, columnNames.toArray(new String[columnNames.size()]));
    }

    /**
     * Check column
     *
     * @param componentDescriptor component descriptor
     * @param orderBies           property descriptor
     */
    public static List<Pair<String, String>> orderBies(ComponentDescriptor<?> componentDescriptor, OrderBy[] orderBies) {
        List<Pair<String, String>> res = new ArrayList<>();
        if (orderBies != null && orderBies.length > 0) {
            for (OrderBy orderBy : orderBies) {
                ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(orderBy.value());
                checkColumn(componentDescriptor, propertyDescriptor);
                res.add(Pair.of(orderBy.value(), orderBy.sort().name()));
            }
        }
        return res;
    }
}
