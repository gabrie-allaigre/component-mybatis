package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.entity.helper.EntityHelper;
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

import java.util.Collections;

public class FindEntityByIdMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindEntityByIdMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isFindEntityByIdKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInFindEntityByIdKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null) {
                return createFindEntityByIdMappedStatement(configuration,key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindEntityByIdMappedStatement(Configuration configuration,String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findEntityById for " + componentClass);
        }

        ResultMap inlineResultMap = configuration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        String idPropertyName = EntityHelper.findIdPropertyName(componentClass);

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new FindComponentsByPropertyNameSqlSource<>(configuration, componentClass, false, idPropertyName),
                SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
