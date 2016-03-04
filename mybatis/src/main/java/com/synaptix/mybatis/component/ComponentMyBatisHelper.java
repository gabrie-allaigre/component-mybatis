package com.synaptix.mybatis.component;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.Id;
import com.synaptix.entity.annotation.Version;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

public class ComponentMyBatisHelper {

    private ComponentMyBatisHelper() {
        super();
    }

    /**
     * Load a component class
     *
     * @param componentClassString component class string
     * @return Component class
     */
    public static <E extends IComponent> Class<E> loadComponentClass(String componentClassString) {
        try {
            return ComponentHelper.loadComponentClass(componentClassString);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Get Entity annotation, verify else throw IllegalArgumentException
     *
     * @param componentDescriptor Component descriptor
     * @return Entity
     */
    public static Entity getEntityAnnotation(ComponentDescriptor<?> componentDescriptor) {
        Class<?> componentClass = componentDescriptor.getComponentClass();
        if (!componentClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Not found annotation Entity for Component=" + componentClass);
        }
        Entity entity = componentClass.getAnnotation(Entity.class);
        if (StringUtils.isBlank(entity.name())) {
            throw new IllegalArgumentException("Not name in Entity for Component=" + componentClass);
        }
        return entity;
    }

    /**
     * Get Column annotation, verify else throw IllegalArgumentException
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @return Column
     */
    public static Column getColumnAnnotation(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
            return null;
        }
        Column column = propertyDescriptor.getMethod().getAnnotation(Column.class);
        if (StringUtils.isBlank(column.name())) {
            throw new IllegalArgumentException("Not name in Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return column;
    }

    public static Id getIdAnnotation(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            return null;
        }
        return propertyDescriptor.getMethod().getAnnotation(Id.class);
    }

    /**
     * Get Version annotation, verify else throw IllegalArgumentException
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @return Version or null if not exists
     */
    public static Version getVersionAnnotation(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Version.class)) {
            return null;
        }
        if (Integer.class != propertyDescriptor.getPropertyClass() && int.class != propertyDescriptor.getPropertyClass() && Long.class != propertyDescriptor.getPropertyClass()
                && long.class != propertyDescriptor.getPropertyClass()) {
            throw new IllegalArgumentException(
                    "Not int or long return type of Version for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return propertyDescriptor.getMethod().getAnnotation(Version.class);
    }

    /**
     * Build Set column, used for update
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @return column.name = #{...}
     */
    public static String buildSetColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Column column = getColumnAnnotation(componentDescriptor, propertyDescriptor);
        if (column == null) {
            return null;
        }
        return column.name() + " = " + buildColumn(componentDescriptor, propertyDescriptor, column);
    }

    /**
     * Build Set id column, used for update
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @return column.name = #{...}
     */
    public static String buildSetIdColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            throw new IllegalArgumentException("Not present annotation Id for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return buildSetColumn(componentDescriptor, propertyDescriptor);
    }

    /**
     * Build Set version column, used for update
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @return column.name = #{...}
     */
    public static String buildSetVersionColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Version version = getVersionAnnotation(componentDescriptor, propertyDescriptor);
        if (version == null) {
            throw new IllegalArgumentException("Not present annotation Version for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return buildSetColumn(componentDescriptor, propertyDescriptor);
    }

    /**
     * Build column
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @param column              column
     * @return #{...}
     */
    public static String buildColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, Column column) {
        return buildColumn(componentDescriptor, propertyDescriptor, column, propertyDescriptor.getPropertyName());
    }

    /**
     * Build column
     *
     * @param componentDescriptor Component descriptor
     * @param propertyDescriptor  Property descriptor
     * @param column              column
     * @param param               name of param
     * @return #{...}
     */
    public static String buildColumn(ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor, Column column, String param) {
        Class<?> javaType = column.javaType() != null && column.javaType() != void.class ? column.javaType() : propertyDescriptor.getPropertyClass();
        JdbcType jdbcType = column.jdbcType() != null && !JdbcType.UNDEFINED.equals(column.jdbcType()) ? column.jdbcType() : null;
        Class<? extends TypeHandler<?>> typeHandlerClass = column.typeHandler() != null && !UnknownTypeHandler.class.equals(column.typeHandler()) ? column.typeHandler() : null;

        return "#{" + param + ",javaType=" + javaType.getCanonicalName() + (jdbcType != null ? ",jdbcType=" + jdbcType.name() : "") + (typeHandlerClass != null ?
                ",typeHandler=" + typeHandlerClass.getCanonicalName() :
                "") + "}";
    }
}
