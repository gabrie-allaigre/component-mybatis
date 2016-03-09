package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.statement.sqlsource.DeleteSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class DeleteMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(DeleteMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isDeleteKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInDeleteKey(key);
            if (componentClass != null) {
                return createDeleteMappedStatement(configuration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createDeleteMappedStatement(Configuration configuration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create delete for " + componentClass);
        }

        ResultMap inlineResultMap = new ResultMap.Builder(configuration, key + "-Inline", Integer.class, new ArrayList<>(), null).build();
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new DeleteSqlSource<>(configuration, componentClass), SqlCommandType.DELETE);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = configuration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(true);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
