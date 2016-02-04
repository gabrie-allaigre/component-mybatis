package com.talanlabs.mybatis.rsql.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.component.cache.CacheNameHelper;
import com.talanlabs.mybatis.component.resultmap.ResultMapNameHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.statement.sqlsource.CountRsqlSqlSource;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class CountRsqlMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(CountRsqlMappedStatementFactory.class);

    private final IRsqlConfiguration rsqlConfiguration;

    public CountRsqlMappedStatementFactory(IRsqlConfiguration rsqlConfiguration) {
        super();

        this.rsqlConfiguration = rsqlConfiguration;
    }

    @Override
    public boolean acceptKey(String key) {
        return RsqlStatementNameHelper.isCountRsqlKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (RsqlStatementNameHelper.isCountRsqlKey(key)) {
            Class<? extends IComponent> componentClass = RsqlStatementNameHelper.extractComponentClassInCountRsqlKey(key);
            if (componentClass != null) {
                return createCountRsqlMappedStatement(componentConfiguration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createCountRsqlMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create count rsql for " + componentClass);
        }

        ResultMap inlineResultMap = new ResultMap.Builder(componentConfiguration, ResultMapNameHelper.buildResultMapKey(componentClass) + "-count", Integer.class, Collections.emptyList()).build();

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key, new CountRsqlSqlSource<>(componentConfiguration, rsqlConfiguration, componentClass),
                SqlCommandType.SELECT);

        msBuilder.resultMaps(Collections.singletonList(inlineResultMap));
        Cache cache = componentConfiguration.getCache(CacheNameHelper.buildCacheKey(componentClass));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(cache);
        msBuilder.useCache(true);
        return msBuilder.build();
    }
}
