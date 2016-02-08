package com.synaptix.entity.helper;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;

import javax.persistence.Id;

public class EntityHelper {

    public static final <E extends IComponent> String findIdPropertyName(Class<E> componentClass) {
        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);
        for (ComponentDescriptor.PropertyDescriptor pd : cd.getPropertyDescriptors()) {
            if (pd.getMethod().isAnnotationPresent(Id.class)) {
                return pd.getPropertyName();
            }
        }
        return null;
    }

}
