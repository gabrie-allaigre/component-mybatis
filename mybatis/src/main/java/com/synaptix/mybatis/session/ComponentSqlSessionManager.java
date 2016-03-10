package com.synaptix.mybatis.session;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.session.defaults.DefaultComponentSqlSession;
import org.apache.ibatis.session.SqlSessionManager;

public class ComponentSqlSessionManager implements IComponentSqlSession {

    private final IComponentSqlSession componentSqlSession;

    private ComponentSqlSessionManager(IComponentSqlSession componentSqlSession) {
        super();

        this.componentSqlSession = componentSqlSession;
    }

    public static ComponentSqlSessionManager newInstance(SqlSessionManager sqlSessionManager) {
        return new ComponentSqlSessionManager(new DefaultComponentSqlSession(sqlSessionManager));
    }

    @Override
    public <E extends IComponent> E findById(Class<E> componentClass, Object id) {
        return componentSqlSession.findById(componentClass, id);
    }

    @Override
    public <E extends IComponent> int insert(E component) {
        return componentSqlSession.insert(component);
    }

    @Override
    public <E extends IComponent> int update(E component) {
        return componentSqlSession.update(component);
    }

    @Override
    public <E extends IComponent> int delete(E component) {
        return componentSqlSession.delete(component);
    }
}
