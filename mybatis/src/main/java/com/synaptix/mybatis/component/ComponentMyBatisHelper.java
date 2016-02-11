package com.synaptix.mybatis.component;

import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;

public class ComponentMyBatisHelper {

    private ComponentMyBatisHelper() {
        super();
    }

    public static final <E extends IComponent> Class<E> loadComponentClass(String componentClass) {
        try {
            return ComponentHelper.loadComponentClass(componentClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
