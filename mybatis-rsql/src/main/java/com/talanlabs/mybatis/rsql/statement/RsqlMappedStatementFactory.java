package com.talanlabs.mybatis.rsql.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.cache.CacheNameHelper;
import com.talanlabs.mybatis.component.resultmap.ResultMapNameHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.statement.sqlsource.RsqlSqlSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class RsqlMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(RsqlMappedStatementFactory.class);

    private final IRsqlConfiguration rsqlConfiguration;

    public RsqlMappedStatementFactory(IRsqlConfiguration rsqlConfiguration) {
        super();

        this.rsqlConfiguration = rsqlConfiguration;
    }

    @Override
    public boolean acceptKey(String key) {
        return RsqlStatementNameHelper.isRsqlKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (RsqlStatementNameHelper.isRsqlKey(key)) {
            Class<? extends IComponent> componentClass = RsqlStatementNameHelper.extractComponentClassInRsqlKey(key);
            if (componentClass != null) {
                return createRsqlMappedStatement(componentConfiguration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createRsqlMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create rsql for " + componentClass);
        }

        ResultMap inlineResultMap = componentConfiguration.getResultMap(ResultMapNameHelper.buildResultMapKey(componentClass));

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key, new RsqlSqlSource<>(componentConfiguration, rsqlConfiguration, componentClass),
                SqlCommandType.SELECT);

        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
