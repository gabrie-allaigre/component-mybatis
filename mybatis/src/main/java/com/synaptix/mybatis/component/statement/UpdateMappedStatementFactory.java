package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.UpdateSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

public class UpdateMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(UpdateMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isUpdateKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInUpdateKey(key);
            if (componentClass != null) {
                return createUpdateMappedStatement(configuration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createUpdateMappedStatement(Configuration configuration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create update for " + componentClass);
        }

        ComponentDescriptor.PropertyDescriptor versionPropertyDescriptor = EntityHelper.findVersionPropertyDescriptor(componentClass);

        ResultMap inlineResultMap = new ResultMap.Builder(configuration, key + "-Inline", Integer.class, new ArrayList<>(), null).build();
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new UpdateSqlSource<>(configuration, componentClass), SqlCommandType.UPDATE);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        if (versionPropertyDescriptor != null) {
            msBuilder.keyGenerator(new VersionKeyGenerator(versionPropertyDescriptor.getPropertyName(),
                    Integer.class == versionPropertyDescriptor.getPropertyClass() || int.class == versionPropertyDescriptor.getPropertyClass()));
        }

        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(true);
        msBuilder.cache(cache);
        msBuilder.useCache(true);

        return msBuilder.build();
    }

    private static class VersionKeyGenerator implements KeyGenerator {

        private final String versionPropertyName;

        private final boolean intType;

        public VersionKeyGenerator(String versionPropertyName, boolean intType) {
            super();

            this.versionPropertyName = versionPropertyName;
            this.intType = intType;
        }

        @Override
        public void processBefore(Executor executor, MappedStatement mappedStatement, Statement statement, Object parameter) {
        }

        @Override
        public void processAfter(Executor executor, MappedStatement mappedStatement, Statement statement, Object parameter) {
            if (parameter instanceof IComponent) {
                IComponent component = (IComponent) parameter;

                if (intType) {
                    int v = (int) component.straightGetProperty(versionPropertyName);
                    component.straightSetProperty(versionPropertyName, v + 1);
                } else {
                    long v = (long) component.straightGetProperty(versionPropertyName);
                    component.straightSetProperty(versionPropertyName, v + 1);
                }
            }
        }
    }
}
