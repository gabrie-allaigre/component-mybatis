package com.talanlabs.mybatis.component.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.cache.CacheNameHelper;
import com.talanlabs.mybatis.component.resultmap.ResultMapNameHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.sqlsource.FindComponentsByPropertyNameSqlSource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class FindComponentsByMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isFindComponentsByKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isFindComponentsByKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInFindComponentsByKey(key);
            String[] propertyNames = StatementNameHelper.extractPropertyNamesInFindComponentsByKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByKey(key);
            List<Pair<String, String>> orderBies = StatementNameHelper.extractOrderBiesInFindComponentsByKey(key);
            if (componentClass != null && propertyNames != null && propertyNames.length > 0) {
                return createFindComponentsByMappedStatement(componentConfiguration, key, componentClass, ignoreCancel, propertyNames, orderBies);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindComponentsByMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass, boolean ignoreCancel,
            String[] propertyNames, List<Pair<String, String>> orderBies) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsBy for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key,
                new FindComponentsByPropertyNameSqlSource<>(componentConfiguration, componentClass, ignoreCancel, propertyNames, orderBies), SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
