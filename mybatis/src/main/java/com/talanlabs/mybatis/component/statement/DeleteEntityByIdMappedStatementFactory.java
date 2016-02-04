package com.talanlabs.mybatis.component.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.cache.CacheNameHelper;
import com.talanlabs.mybatis.component.resultmap.ResultMapNameHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.sqlsource.DeleteComponentsByPropertyNameSqlSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class DeleteEntityByIdMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(DeleteEntityByIdMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isDeleteEntityByIdKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isDeleteEntityByIdKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInDeleteEntityByIdKey(key);
            if (componentClass != null) {
                return createDeleteEntityByIdMappedStatement(componentConfiguration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createDeleteEntityByIdMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create deleteEntityById for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        String idPropertyName = EntityHelper.findIdPropertyName(componentClass);

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key,
                new DeleteComponentsByPropertyNameSqlSource<>(componentConfiguration, componentClass, idPropertyName), SqlCommandType.SELECT);

        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
