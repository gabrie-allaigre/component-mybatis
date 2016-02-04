package com.talanlabs.entity.helper;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Id;
import com.talanlabs.entity.annotation.Version;

public class EntityHelper {

    private EntityHelper() {
        super();
    }

    /**
     * Find propertyName with annotation Id
     *
     * @param componentClass component class
     * @return propertyName
     */
    public static <E extends IComponent> String findIdPropertyName(Class<E> componentClass) {
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
