package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.FindComponentsByJoinTableSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class FindComponentsByJoinTableMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByJoinTableMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isFindComponentsByJoinTableKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInFindComponentsByJoinTableKey(key);
            String sourceComponentName = StatementNameHelper.extractSourceComponentNameInFindComponentsByJoinTableKey(key);
            String[] sourceProperties = StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(key);
            String[] targetProperties = StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(key);
            List<Pair<String, Pair<String[], String[]>>> joins = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            Class<? extends IComponent> sourceComponentClass = ComponentMyBatisHelper.loadComponentClass(sourceComponentName);
            if (componentClass != null && sourceComponentClass != null && sourceProperties != null && sourceProperties.length > 0 && targetProperties != null && targetProperties.length > 0
                    && joins != null && !joins.isEmpty()) {
                return createFindComponentsByJoinTableMappedStatement(configuration, key, componentClass, sourceComponentClass, sourceProperties, targetProperties, joins, ignoreCancel);
            }
        }
        return null;
    }

    public <E extends IComponent> MappedStatement createFindComponentsByJoinTableMappedStatement(Configuration configuration, String key, Class<? extends IComponent> componentClass,
            Class<? extends IComponent> sourceComponentClass, String[] sourceProperties, String[] targetProperties, List<Pair<String, Pair<String[], String[]>>> joins, boolean ignoreCancel) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsByJoinTable for " + componentClass);
        }

        ResultMap inlineResultMap = configuration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key,
                new FindComponentsByJoinTableSqlSource<E>(configuration, componentClass, sourceComponentClass, sourceProperties, targetProperties, joins, ignoreCancel), SqlCommandType.SELECT);
        msBuilder.resultMaps(Arrays.asList(inlineResultMap));
        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
