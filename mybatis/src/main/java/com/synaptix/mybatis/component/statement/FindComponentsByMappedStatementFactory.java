package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.FindComponentsByPropertyNameSqlSource;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class FindComponentsByMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isFindComponentsByKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInFindComponentsByKey(key);
            String[] propertyNames = StatementNameHelper.extractPropertyNamesInFindComponentsByKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByKey(key);
            if (componentClass != null && propertyNames != null && propertyNames.length > 0) {
                return createFindComponentsByMappedStatement(componentConfiguration, key, componentClass, ignoreCancel, propertyNames);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindComponentsByMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass, boolean ignoreCancel,
            String... propertyNames) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsBy for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key,
                new FindComponentsByPropertyNameSqlSource<>(componentConfiguration, componentClass, ignoreCancel, propertyNames), SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
