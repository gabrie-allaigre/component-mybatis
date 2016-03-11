package com.synaptix.mybatis.component.session.observer;

import com.synaptix.component.IComponent;

public interface ITriggerObserver {

    /**
     * Trigger call before insert, update or delete component
     *
     * @param type      insert, update or delete
     * @param component component
     */
    <E extends IComponent> void triggerBefore(Type type, E component);

    /**
     * Trigger call after insert, update or delete component
     *
     * @param type      insert, update or delete
     * @param component component
     */
    <E extends IComponent> void triggerAfter(Type type, E component);

    enum Type {
        Insert,
        Update,
        Delete
    }
}
