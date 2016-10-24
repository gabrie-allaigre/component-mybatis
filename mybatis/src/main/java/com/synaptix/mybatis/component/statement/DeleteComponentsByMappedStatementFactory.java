package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.synaptix.mybatis.component.statement.sqlsource.DeleteComponentsByPropertyNameSqlSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class DeleteComponentsByMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(DeleteComponentsByMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isDeleteComponentsByKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isDeleteComponentsByKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInDeleteComponentsByKey(key);
            String[] propertyNames = StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey(key);
            if (componentClass != null && propertyNames != null && propertyNames.length > 0) {
                return createDeleteComponentsByMappedStatement(componentConfiguration, key, componentClass, propertyNames);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createDeleteComponentsByMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass,
                                                                                           String... propertyNames) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create deleteComponentsBy for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key,
                new DeleteComponentsByPropertyNameSqlSource<>(componentConfiguration, componentClass, propertyNames), SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
