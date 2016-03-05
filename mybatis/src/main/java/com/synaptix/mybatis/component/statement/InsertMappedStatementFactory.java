package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.entity.annotation.Id;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.InsertSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

public class InsertMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(InsertMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isInsertKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInInsertKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null) {
                return createInsertMappedStatement(configuration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createInsertMappedStatement(Configuration configuration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create insert for " + componentClass);
        }

        ResultMap inlineResultMap = new ResultMap.Builder(configuration, key + "-Inline", Integer.class, new ArrayList<>(), null).build();
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new InsertSqlSource<E>(configuration, componentClass), SqlCommandType.INSERT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));

        ComponentDescriptor.PropertyDescriptor idPropertyDescriptor = EntityHelper.findIdPropertyDescriptor(componentClass);
        if (idPropertyDescriptor != null) {
            Id id = idPropertyDescriptor.getMethod().getAnnotation(Id.class);

            if (StringUtils.isNotBlank(id.keyGeneratorId())) {
                KeyGenerator keyGenerator = configuration.getKeyGenerator(id.keyGeneratorId());
                if (keyGenerator == null) {
                    throw new IllegalArgumentException("Not found key generator for Component=" + componentClass + " for Id=" + idPropertyDescriptor.getPropertyName());
                }
                msBuilder.keyGenerator(keyGenerator);
            } else {
                Class<? extends KeyGenerator> keyGeneratorClass = id.keyGeneratorClass();
                if (keyGeneratorClass != null) {
                    msBuilder.keyGenerator(buildKeyGenerator(configuration, key, componentClass, idPropertyDescriptor, id.keyGeneratorClass()));
                } else {
                    msBuilder.keyGenerator(new NoKeyGenerator());
                }
            }
        }

        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(true);
        msBuilder.cache(cache);
        msBuilder.useCache(true);

        return msBuilder.build();
    }

    private <E extends IComponent, F extends KeyGenerator> F buildKeyGenerator(Configuration configuration, String key, Class<?> componentClass,
            ComponentDescriptor.PropertyDescriptor idPropertyDescriptor, Class<F> keyGeneratorClass) {
        try {
            Constructor<F> constructor = ConstructorUtils.<F>getAccessibleConstructor(keyGeneratorClass, Configuration.class, String.class, Class.class, String.class);
            if (constructor != null) {
                return constructor.newInstance(configuration, key, componentClass, idPropertyDescriptor.getPropertyName());
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("Not instancy key generator for Component=" + componentClass + " for Id=" + idPropertyDescriptor.getPropertyName());
        }
        try {
            return keyGeneratorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Not instancy key generator for Component=" + componentClass + " for Id=" + idPropertyDescriptor.getPropertyName());
        }
    }
}
