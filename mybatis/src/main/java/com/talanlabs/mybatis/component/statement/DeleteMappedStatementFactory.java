package com.talanlabs.mybatis.component.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.cache.CacheNameHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.sqlsource.DeleteSqlSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class DeleteMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(DeleteMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isDeleteKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isDeleteKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInDeleteKey(key);
            if (componentClass != null) {
                return createDeleteMappedStatement(componentConfiguration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createDeleteMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create delete for " + componentClass);
        }

        ResultMap inlineResultMap = new ResultMap.Builder(componentConfiguration, key + "-Inline", Integer.class, new ArrayList<>(), null).build();
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key, new DeleteSqlSource<>(componentConfiguration, componentClass), SqlCommandType.DELETE);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(true);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
