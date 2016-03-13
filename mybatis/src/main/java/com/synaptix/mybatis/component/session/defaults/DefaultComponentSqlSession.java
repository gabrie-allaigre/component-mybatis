package com.synaptix.mybatis.component.session.defaults;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.IComponentSqlSession;
import com.synaptix.mybatis.component.session.observer.ITriggerObserver;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.ibatis.session.SqlSession;

public class DefaultComponentSqlSession implements IComponentSqlSession {

    private final SqlSession sqlSession;

    public DefaultComponentSqlSession(SqlSession sqlSession) {
        super();

        this.sqlSession = sqlSession;
    }

    @Override
    public <E extends IComponent> E findById(Class<E> componentClass, Object id) {
        return sqlSession.<E>selectOne(StatementNameHelper.buildFindEntityByIdKey(componentClass), id);
    }

    @Override
    public <E extends IComponent> int insert(E component) {
        if (component == null) {
            return 0;
        }

        triggerBefore(ITriggerObserver.Type.Insert, component);
        int res = sqlSession.insert(StatementNameHelper.buildInsertKey(ComponentFactory.getInstance().getComponentClass(component)), component);
        triggerAfter(ITriggerObserver.Type.Insert, component);
        return res;
    }

    @Override
    public <E extends IComponent> int update(E component) {
        if (component == null) {
            return 0;
        }

        triggerBefore(ITriggerObserver.Type.Update, component);
        int res = sqlSession.update(StatementNameHelper.buildUpdateKey(ComponentFactory.getInstance().getComponentClass(component)), component);
        triggerAfter(ITriggerObserver.Type.Update, component);
        return res;
    }

    @Override
    public <E extends IComponent> int delete(E component) {
        if (component == null) {
            return 0;
        }

        triggerBefore(ITriggerObserver.Type.Delete, component);
        int res = sqlSession.delete(StatementNameHelper.buildDeleteKey(ComponentFactory.getInstance().getComponentClass(component)), component);
        triggerAfter(ITriggerObserver.Type.Delete, component);
        return res;
    }

    private <E extends IComponent> void triggerBefore(ITriggerObserver.Type type, E component) {
        if (sqlSession.getConfiguration() instanceof ComponentConfiguration && ((ComponentConfiguration) sqlSession.getConfiguration()).getTriggerDispatcher() != null) {
            ((ComponentConfiguration) sqlSession.getConfiguration()).getTriggerDispatcher().triggerBefore(sqlSession, type, component);
        }
    }

    private <E extends IComponent> void triggerAfter(ITriggerObserver.Type type, E component) {
        if (sqlSession.getConfiguration() instanceof ComponentConfiguration && ((ComponentConfiguration) sqlSession.getConfiguration()).getTriggerDispatcher() != null) {
            ((ComponentConfiguration) sqlSession.getConfiguration()).getTriggerDispatcher().triggerAfter(sqlSession, type, component);
        }
    }
}
