package com.talanlabs.mybatis.component.session.observer;

import com.talanlabs.component.IComponent;
import org.apache.ibatis.session.SqlSession;

public abstract class AbstractTriggerObserver implements ITriggerObserver {

    @Override
    public <E extends IComponent> void triggerBefore(SqlSession sqlSession, Type type, E component) {
        // Nothing
    }

    @Override
    public <E extends IComponent> void triggerAfter(SqlSession sqlSession, Type type, E component) {
        // Nothing
    }
}
