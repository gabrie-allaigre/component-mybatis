package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.FindComponentsByPropertyNameSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class FindComponentsByMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isFindComponentsByKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInFindComponentsByKey(key);
            String[] propertyNames = StatementNameHelper.extractPropertyNamesInFindComponentsByKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null && propertyNames != null && propertyNames.length > 0) {
                return createFindComponentsByMappedStatement(configuration, key, componentClass, ignoreCancel, propertyNames);
            }
        }
        return null;
    }

    public <E extends IComponent> MappedStatement createFindComponentsByMappedStatement(Configuration configuration, String key, Class<E> componentClass, boolean ignoreCancel,
            String... propertyNames) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsBy for " + componentClass);
        }

        ResultMap inlineResultMap = configuration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new FindComponentsByPropertyNameSqlSource<>(configuration, componentClass, ignoreCancel, propertyNames),
                SqlCommandType.SELECT);
        msBuilder.resultMaps(Arrays.asList(inlineResultMap));
        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
