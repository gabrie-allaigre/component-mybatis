package com.talanlabs.mybatis.component.session.observer;

import com.talanlabs.component.IComponent;
import org.apache.ibatis.session.SqlSession;

public interface ITriggerObserver {

    /**
     * Trigger call before insert, update or delete component
     *
     * @param sqlSession current sqlSession
     * @param type       insert, update or delete
     * @param component  component
     */
    <E extends IComponent> void triggerBefore(SqlSession sqlSession, Type type, E component);

    /**
     * Trigger call after insert, update or delete component
     *
     * @param sqlSession current sqlSession
     * @param type       insert, update or delete
     * @param component  component
     */
    <E extends IComponent> void triggerAfter(SqlSession sqlSession, Type type, E component);

    enum Type {
        Insert, Update, Delete
    }
}
