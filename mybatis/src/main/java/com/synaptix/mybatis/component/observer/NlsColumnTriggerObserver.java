package com.synaptix.mybatis.component.observer;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import com.synaptix.mybatis.component.session.observer.AbstractTriggerObserver;
import org.apache.ibatis.session.SqlSession;

import java.util.Set;

public class NlsColumnTriggerObserver extends AbstractTriggerObserver {

    @Override
    public <E extends IComponent> void triggerBefore(SqlSession sqlSession, Type type, E component) {
        if (sqlSession.getConfiguration() instanceof ComponentConfiguration && ((ComponentConfiguration) sqlSession.getConfiguration()).getNlsColumnHandler() != null) {
            INlsColumnHandler nlsColumnHandler = ((ComponentConfiguration) sqlSession.getConfiguration()).getNlsColumnHandler();

            Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);
            if (ComponentMyBatisHelper.isUseNlsColumn(componentClass)) {
                Set<String> propertyNames = ComponentMyBatisHelper.getPropertyNamesWithNlsColumn(componentClass);
                if (propertyNames != null && !propertyNames.isEmpty()) {
                    for (String propertyName : propertyNames) {

                    }
                }
            }
        }
    }
}
