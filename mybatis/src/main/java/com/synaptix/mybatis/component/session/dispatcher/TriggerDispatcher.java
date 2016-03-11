package com.synaptix.mybatis.component.session.dispatcher;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.session.observer.ITriggerObserver;

import java.util.ArrayList;
import java.util.List;

public class TriggerDispatcher {

    private List<ITriggerObserver> triggerObservers = new ArrayList<>();

    /**
     * Add trigger observer
     *
     * @param triggerObserver observer
     */
    public void addTriggerObserver(ITriggerObserver triggerObserver) {
        triggerObservers.add(triggerObserver);
    }

    /**
     * Remove trigger observer
     *
     * @param triggerObserver observer
     */
    public void removeTriggerObserver(ITriggerObserver triggerObserver) {
        triggerObservers.remove(triggerObserver);
    }

    /**
     * Trigger call before insert, update or delete component
     *
     * @param type      insert, update or delete
     * @param component component
     */
    public <E extends IComponent> void triggerBefore(ITriggerObserver.Type type, E component) {
        for (ITriggerObserver triggerObserver : triggerObservers) {
            triggerObserver.triggerBefore(type, component);
        }
    }

    /**
     * Trigger call after insert, update or delete component
     *
     * @param type      insert, update or delete
     * @param component component
     */
    public <E extends IComponent> void triggerAfter(ITriggerObserver.Type type, E component) {
        for (ITriggerObserver triggerObserver : triggerObservers) {
            triggerObserver.triggerAfter(type, component);
        }
    }
}
