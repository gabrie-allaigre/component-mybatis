package com.synaptix.mybatis.component.resultmap.factory;

import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.JoinTable;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
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

    public static List<Pair<String, Pair<String[], String[]>>> joinTables(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, JoinTable[] joinTables,
            String[] propertySource, String[] propertyTarget) {
        List<Pair<String, Pair<String[], String[]>>> joins = new ArrayList<>();
        int i = 0;
        for (JoinTable joinTable : joinTables) {
            if (StringUtils.isBlank(joinTable.name())) {
                throw new IllegalArgumentException("JoinTable is empty for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (joinTable.left() == null || joinTable.left().length == 0) {
                throw new IllegalArgumentException(
                        "JoinTable join=" + joinTable.name() + " left is empty for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            if (joinTable.right() == null || joinTable.right().length == 0) {
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

    public static void fillComposite(Configuration configuration, List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns, ResultMapping.Builder resultMappingBuilder) {
        if (sourceColumns.size() > 1) {
            List<ResultMapping> composites = new ArrayList<>();
            int param = 1;
            for (Pair<ComponentDescriptor.PropertyDescriptor, String> sourceColumn : sourceColumns) {
                ComponentDescriptor.PropertyDescriptor pd = sourceColumn.getLeft();
                composites.add(new ResultMapping.Builder(configuration, StatementNameHelper.buildParam(param), sourceColumn.getRight(), pd.getPropertyClass()).build());
                param++;
            }
            resultMappingBuilder.composites(composites);
        }
    }

    public static void checkTraget(ComponentDescriptor<?> cd, String[] propertyTarget) {
        if (propertyTarget == null || propertyTarget.length == 0) {
            throw new IllegalArgumentException("propertyTarget is null or empty");
        }

        for (String propertyName : propertyTarget) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            checkColumn(cd, propertyDescriptor);
        }
    }

    public static List<Pair<ComponentDescriptor.PropertyDescriptor, String>> prepareSourceColumns(ComponentDescriptor<?> cd, String[] propertySource) throws IllegalArgumentException {
        if (propertySource == null || propertySource.length == 0) {
            throw new IllegalArgumentException("propertySource is null or empty");
        }

        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> columnNames = new ArrayList<>();
        for (String propertyName : propertySource) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            checkColumn(cd, propertyDescriptor);

            columnNames.add(Pair.of(propertyDescriptor, propertyDescriptor.getMethod().getAnnotation(Column.class).name()));
        }
        return columnNames;
    }

    public static void checkColumn(ComponentDescriptor<?> cd, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Not exists property for Component=" + cd.getComponentClass());
        }
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Not present annotation Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (StringUtils.isBlank(propertyDescriptor.getMethod().getAnnotation(Column.class).name())) {
            throw new IllegalArgumentException("Not name in Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
    }
}
