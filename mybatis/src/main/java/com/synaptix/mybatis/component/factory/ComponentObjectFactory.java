package com.synaptix.mybatis.component.factory;

import com.synaptix.component.factory.ComponentFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

public class ComponentObjectFactory extends DefaultObjectFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> type) {
        if (ComponentFactory.getInstance().isComponentType(type)) {
            return ComponentFactory.getInstance().createInstance(type);
        }
        return super.create(type);
    }
}

