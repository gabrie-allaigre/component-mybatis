package com.synaptix.mybatis.component.session.observer;

import com.synaptix.component.IComponent;

public abstract class AbstractTriggerObserver implements ITriggerObserver {

    @Override
    public <E extends IComponent> void triggerBefore(Type type, E component) {
        // Nothing
    }

    @Override
    public <E extends IComponent> void triggerAfter(Type type, E component) {
        // Nothing
    }
}
