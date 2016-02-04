package com.talanlabs.mybatis.component.session.dispatcher;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.session.observer.ITriggerObserver;
import org.apache.ibatis.session.SqlSession;

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
     * @param sqlSession current sqlSession
     * @param type       insert, update or delete
     * @param component  component
     */
    public <E extends IComponent> void triggerBefore(SqlSession sqlSession, ITriggerObserver.Type type, E component) {
        for (ITriggerObserver triggerObserver : triggerObservers) {
            triggerObserver.triggerBefore(sqlSession, type, component);
        }
    }

    /**
     * Trigger call after insert, update or delete component
     *
     * @param sqlSession
     * @param type       insert, update or delete
     * @param component  component
     */
    public <E extends IComponent> void triggerAfter(SqlSession sqlSession, ITriggerObserver.Type type, E component) {
        for (ITriggerObserver triggerObserver : triggerObservers) {
            triggerObserver.triggerAfter(sqlSession, type, component);
        }
    }
}
