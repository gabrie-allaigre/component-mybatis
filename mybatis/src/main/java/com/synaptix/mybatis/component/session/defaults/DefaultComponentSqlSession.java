package com.synaptix.mybatis.component.session.defaults;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.NlsColumn;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.IComponentSqlSession;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import com.synaptix.mybatis.component.session.observer.ITriggerObserver;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.*;

public class DefaultComponentSqlSession implements IComponentSqlSession {

    private final SqlSession sqlSession;

    private final Map<Class<? extends IComponent>, NlsComponentCache> nlsComponentCacheMap = Collections.synchronizedMap(new HashMap<>());

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

        Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);

        triggerBefore(ITriggerObserver.Type.Insert, component);
        int res = sqlSession.insert(StatementNameHelper.buildInsertKey(componentClass), component);

        Configuration configuration = sqlSession.getConfiguration();
        mergeNlsComponent(configuration, componentClass, component);

        triggerAfter(ITriggerObserver.Type.Insert, component);
        return res;
    }

    @Override
    public <E extends IComponent> int update(E component) {
        if (component == null) {
            return 0;
        }

        Set<String> nlsPropertyNames = new HashSet<>();

        Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);
        Configuration configuration = sqlSession.getConfiguration();
        if (configuration instanceof ComponentConfiguration && ((ComponentConfiguration) configuration).getNlsColumnHandler() != null) {
            INlsColumnHandler nlsColumnHandler = ((ComponentConfiguration) configuration).getNlsColumnHandler();

            NlsComponentCache nlsComponentCache = buildNlsComponentCache(componentClass);
            if (nlsComponentCache != null && nlsComponentCache.columnMap != null) {
                for (Map.Entry<String, String> entry : nlsComponentCache.columnMap.entrySet()) {
                    String propertyName = entry.getKey();
                    if (nlsColumnHandler.isUpdateDefaultNlsColumn(componentClass, propertyName)) {
                        nlsPropertyNames.add(propertyName);
                    }
                }
            }
        }

        triggerBefore(ITriggerObserver.Type.Update, component);

        int res = sqlSession
                .update(StatementNameHelper.buildUpdateKey(ComponentFactory.getInstance().getComponentClass(component), nlsPropertyNames.toArray(new String[nlsPropertyNames.size()])), component);

        mergeNlsComponent(configuration, componentClass, component);

        triggerAfter(ITriggerObserver.Type.Update, component);
        return res;
    }

    @Override
    public <E extends IComponent> int delete(E component) {
        if (component == null) {
            return 0;
        }

        Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);

        triggerBefore(ITriggerObserver.Type.Delete, component);

        int res = sqlSession.delete(StatementNameHelper.buildDeleteKey(componentClass), component);

        deleteNlsComponent(sqlSession.getConfiguration(), componentClass, component);

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

    private <E extends IComponent> void mergeNlsComponent(Configuration configuration, Class<E> componentClass, E component) {
        if (configuration instanceof ComponentConfiguration && ((ComponentConfiguration) configuration).getNlsColumnHandler() != null) {
            INlsColumnHandler nlsColumnHandler = ((ComponentConfiguration) configuration).getNlsColumnHandler();

            NlsComponentCache nlsComponentCache = nlsComponentCacheMap.get(componentClass);
            if (nlsComponentCache == null) {
                nlsComponentCache = buildNlsComponentCache(componentClass);
                nlsComponentCacheMap.put(componentClass, nlsComponentCache);
            }

            if (nlsComponentCache != null && nlsComponentCache.columnMap != null) {
                for (Map.Entry<String, String> entry : nlsComponentCache.columnMap.entrySet()) {
                    String propertyName = entry.getKey();
                    String mergeId = nlsColumnHandler.getMergeNlsColumnId(componentClass, propertyName);
                    if (StringUtils.isNotBlank(mergeId)) {
                        Map<String, Object> parameter = new HashMap<>();
                        parameter.put("tableName", nlsComponentCache.tableName);
                        parameter.put("columnName", entry.getValue());
                        parameter.put("id", component.straightGetProperty(nlsComponentCache.idName));
                        parameter.put("meaning", component.straightGetProperty(propertyName));

                        Map<String, Object> additionalParameter = nlsColumnHandler.getAdditionalParameter(componentClass, propertyName);
                        if (additionalParameter != null) {
                            parameter.putAll(additionalParameter);
                        }

                        sqlSession.update(mergeId, parameter);
                    }
                }
            }
        }
    }

    private <E extends IComponent> void deleteNlsComponent(Configuration configuration, Class<E> componentClass, E component) {
        if (configuration instanceof ComponentConfiguration && ((ComponentConfiguration) configuration).getNlsColumnHandler() != null) {
            INlsColumnHandler nlsColumnHandler = ((ComponentConfiguration) configuration).getNlsColumnHandler();

            NlsComponentCache nlsComponentCache = nlsComponentCacheMap.get(componentClass);
            if (nlsComponentCache == null) {
                nlsComponentCache = buildNlsComponentCache(componentClass);
                nlsComponentCacheMap.put(componentClass, nlsComponentCache);
            }

            if (nlsComponentCache != null && nlsComponentCache.columnMap != null) {
                String deleteId = nlsColumnHandler.getDeleteNlsColumnsId(componentClass);
                if (StringUtils.isNotBlank(deleteId)) {
                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("tableName", nlsComponentCache.tableName);
                    parameter.put("id", component.straightGetProperty(nlsComponentCache.idName));

                    sqlSession.delete(deleteId, parameter);
                }
            }
        }
    }

    private <E extends IComponent> NlsComponentCache buildNlsComponentCache(Class<E> componentClass) {
        NlsComponentCache res = new NlsComponentCache();

        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);
        res.tableName = entity.name();
        ComponentDescriptor.PropertyDescriptor idPropertyDescriptor = EntityHelper.findIdPropertyDescriptor(componentClass);
        if (idPropertyDescriptor == null) {
            throw new IllegalArgumentException("Not found annotation Id for Component=" + componentClass);
        }
        res.idName = idPropertyDescriptor.getPropertyName();

        if (ComponentMyBatisHelper.isUseNlsColumn(componentClass)) {
            Set<String> propertyNames = ComponentMyBatisHelper.getPropertyNamesWithNlsColumn(componentClass);
            if (propertyNames != null && !propertyNames.isEmpty()) {
                res.columnMap = new HashMap<>(propertyNames.size());
                for (String propertyName : propertyNames) {
                    ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);
                    NlsColumn nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(componentDescriptor, propertyDescriptor);
                    res.columnMap.put(propertyName, nlsColumn.name());
                }
            }
        }
        return res;
    }

    private class NlsComponentCache {

        String tableName = null;

        String idName = null;

        Map<String, String> columnMap = null;

    }
}
