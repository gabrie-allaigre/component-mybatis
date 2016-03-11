package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.synaptix.mybatis.component.statement.sqlsource.FindComponentsByJoinTableSqlSource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class FindComponentsByJoinTableMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByJoinTableMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isFindComponentsByJoinTableKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isFindComponentsByJoinTableKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(key);
            Class<? extends IComponent> sourceComponentClass = StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(key);
            String[] sourceProperties = StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(key);
            String[] targetProperties = StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(key);
            List<Pair<String, Pair<String[], String[]>>> joins = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(key);
            if (componentClass != null && sourceComponentClass != null && sourceProperties != null && sourceProperties.length > 0 && targetProperties != null && targetProperties.length > 0
                    && joins != null && !joins.isEmpty()) {
                return createFindComponentsByJoinTableMappedStatement(componentConfiguration, key, componentClass, sourceComponentClass, sourceProperties, targetProperties, joins, ignoreCancel);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindComponentsByJoinTableMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<? extends IComponent> componentClass,
            Class<? extends IComponent> sourceComponentClass, String[] sourceProperties, String[] targetProperties, List<Pair<String, Pair<String[], String[]>>> joins, boolean ignoreCancel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsByJoinTable for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key,
                new FindComponentsByJoinTableSqlSource<E>(componentConfiguration, componentClass, sourceComponentClass, sourceProperties, targetProperties, joins, ignoreCancel), SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
