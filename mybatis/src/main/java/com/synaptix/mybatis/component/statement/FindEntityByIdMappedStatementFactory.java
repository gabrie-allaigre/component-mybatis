package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.entity.helper.EntityHelper;
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

public class FindEntityByIdMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindEntityByIdMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isFindEntityByIdKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInFindEntityByIdKey(key);
            if (componentClass != null) {
                return createFindEntityByIdMappedStatement(componentConfiguration,key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindEntityByIdMappedStatement(ComponentConfiguration componentConfiguration,String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findEntityById for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        String idPropertyName = EntityHelper.findIdPropertyName(componentClass);

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key, new FindComponentsByPropertyNameSqlSource<>(componentConfiguration, componentClass, false, idPropertyName),
                SqlCommandType.SELECT);

        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
