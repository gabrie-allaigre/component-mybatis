package com.synaptix.entity.helper;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Id;
import com.synaptix.entity.annotation.Version;

public class EntityHelper {

    /**
     * Find propertyName with annotation Id
     *
     * @param componentClass component class
     * @return propertyName
     */
    public static final <E extends IComponent> String findIdPropertyName(Class<E> componentClass) {
        ComponentDescriptor.PropertyDescriptor pd = findIdPropertyDescriptor(componentClass);
        if (pd != null) {
            return pd.getPropertyName();
        }
        return null;
    }

    /**
     * Find property desciptor with annotation Id
     *
     * @param componentClass component class
     * @return property desciptor or null
     */
    public static <E extends IComponent> ComponentDescriptor.PropertyDescriptor findIdPropertyDescriptor(Class<E> componentClass) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);
        for (ComponentDescriptor.PropertyDescriptor pd : cd.getPropertyDescriptors()) {
            if (pd.getMethod().isAnnotationPresent(Id.class)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Find propertyName with annotation Version
     *
     * @param componentClass component class
     * @return propertyName
     */
    public static <E extends IComponent> String findVersionPropertyName(Class<E> componentClass) {
        ComponentDescriptor.PropertyDescriptor pd = findVersionPropertyDescriptor(componentClass);
        if (pd != null) {
            return pd.getPropertyName();
        }
        return null;
    }

    /**
     * Find propertyName with annotation Version
     *
     * @param componentClass component class
     * @return property desciptor or null
     */
    public static <E extends IComponent> ComponentDescriptor.PropertyDescriptor findVersionPropertyDescriptor(Class<E> componentClass) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);
        for (ComponentDescriptor.PropertyDescriptor pd : cd.getPropertyDescriptors()) {
            if (pd.getMethod().isAnnotationPresent(Version.class) && (Integer.class.equals(pd.getPropertyClass()) || Long.class.equals(pd.getPropertyClass()))) {
                return pd;
            }
        }
        return null;
    }
}
