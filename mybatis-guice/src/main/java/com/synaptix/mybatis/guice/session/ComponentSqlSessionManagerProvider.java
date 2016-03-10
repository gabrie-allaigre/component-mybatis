package com.synaptix.mybatis.guice.session;

import com.synaptix.mybatis.session.ComponentSqlSessionManager;
import org.apache.ibatis.session.SqlSessionManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public final class ComponentSqlSessionManagerProvider implements Provider<ComponentSqlSessionManager> {

    private ComponentSqlSessionManager componentSqlSessionManager;

    @Inject
    public void createNewComponentSqlSessionManager(SqlSessionManager sqlSessionManager) {
        this.componentSqlSessionManager = ComponentSqlSessionManager.newInstance(sqlSessionManager);
    }

    @Override
    public ComponentSqlSessionManager get() {
        return componentSqlSessionManager;
    }
}
