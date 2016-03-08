package com.synaptix.mybatis.component;

import com.google.common.reflect.TypeToken;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * Find children for component class, all level
     *
     * @param componentClass component class
     * @return all children
     */
    public static <E extends IComponent> Set<Class<? extends IComponent>> findAllLinks(Class<E> componentClass) {
        Set<Class<? extends IComponent>> res = new HashSet<Class<? extends IComponent>>();
        findAllLinks(res, componentClass);
        return res;
    }

    private static <E extends IComponent> void findAllLinks(Set<Class<? extends IComponent>> res, Class<E> componentClass) {
        Set<Class<? extends IComponent>> childs = findLinks(componentClass);
        if (childs != null && !childs.isEmpty()) {
            childs.stream().filter(child -> !res.contains(child)).forEach(child -> {
                res.add(child);
                findAllLinks(res, child);
            });
        }
    }

    /**
     * Find children for component class, 1 level
     *
     * @param componentClass component class
     * @return children
     */
    @SuppressWarnings("unchecked")
    private static <E extends IComponent> Set<Class<? extends IComponent>> findLinks(Class<E> componentClass) {
        Set<Class<? extends IComponent>> res = new HashSet<Class<? extends IComponent>>();

        Cache cache = componentClass.getAnnotation(Cache.class);
        if (cache != null && cache.links() != null && cache.links().length > 0) {
            for (int i = 0; i < cache.links().length; i++) {
                res.add(cache.links()[i]);
            }
        }

        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        for (ComponentDescriptor.PropertyDescriptor propertyDescriptor : componentDescriptor.getPropertyDescriptors()) {
            if (propertyDescriptor.getMethod().isAnnotationPresent(Association.class)) {
                Association association = propertyDescriptor.getMethod().getAnnotation(Association.class);

                Class<?> javaType = association.javaType() != null && association.javaType() != void.class ? association.javaType() : propertyDescriptor.getPropertyClass();
                if (ComponentFactory.getInstance().isComponentType(javaType)) {
                    res.add((Class<? extends IComponent>) javaType);
                }
            } else if (propertyDescriptor.getMethod().isAnnotationPresent(Collection.class)) {
                Collection collection = propertyDescriptor.getMethod().getAnnotation(Collection.class);

                Class<?> javaType = collection.javaType() != null && collection.javaType() != void.class ? collection.javaType() : propertyDescriptor.getPropertyClass();
                if (!java.util.Collection.class.isAssignableFrom(javaType)) {
                    throw new IllegalArgumentException(
                            "Not accept javaType for Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + " javaType="
                                    + javaType);
                }

                Class<?> clazz = getCollectionElementClass(componentDescriptor, propertyDescriptor, collection);
                if (ComponentFactory.getInstance().isComponentType(clazz)) {
                    res.add((Class<? extends IComponent>) clazz);
                }
            }
        }
        return res;
    }

    /**
     * Get element type of collection
     *
     * @param componentDescriptor component descriptor
     * @param propertyDescriptor  property descriptor
     * @param collection          collection
     * @return element type
     */
    public static <E extends IComponent> Class<?> getCollectionElementClass(ComponentDescriptor<E> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            Collection collection) {
        Class<?> ofType = collection.ofType();
        if (ofType == null || ofType == void.class) {
            Type type = getCollectionElementType(TypeToken.of(propertyDescriptor.getPropertyType()));
            if (type == null) {
                throw new IllegalArgumentException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }
            ofType = TypeToken.of(type).getRawType();
        }
        return ofType;
    }

    @SuppressWarnings("unchecked")
    private static <T> Type getCollectionElementType(TypeToken<T> typeToken) {
        Type collectionType = typeToken.getSupertype((Class<? super T>) java.util.Collection.class).getType();

        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType) collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }
}
