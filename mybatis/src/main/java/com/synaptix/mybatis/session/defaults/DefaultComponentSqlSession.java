package com.synaptix.mybatis.session.defaults;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ITracable;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.IComponentSqlSession;
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

        if (sqlSession.getConfiguration() instanceof ComponentConfiguration && component instanceof ITracable
                && ((ComponentConfiguration) sqlSession.getConfiguration()).getTracableHandler() != null) {
            ((ComponentConfiguration) sqlSession.getConfiguration()).getTracableHandler().fillInsertTracable((ITracable) component);
        }

        return sqlSession.insert(StatementNameHelper.buildInsertKey(ComponentFactory.getInstance().getComponentClass(component)), component);
    }

    @Override
    public <E extends IComponent> int update(E component) {
        if (component == null) {
            return 0;
        }

        if (component instanceof ITracable) {

        }

        return sqlSession.update(StatementNameHelper.buildUpdateKey(ComponentFactory.getInstance().getComponentClass(component)), component);
    }

    @Override
    public <E extends IComponent> int delete(E component) {
        if (component == null) {
            return 0;
        }

        return sqlSession.delete(StatementNameHelper.buildDeleteKey(ComponentFactory.getInstance().getComponentClass(component)), component);
    }
}
