package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.statement.sqlsource.UpdateSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class UpdateMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(UpdateMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isUpdateKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInUpdateKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
            if (componentClass != null) {
                return createUpdateMappedStatement(configuration, key, componentClass);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createUpdateMappedStatement(Configuration configuration, String key, Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create update for " + componentClass);
        }

        ResultMap inlineResultMap = new ResultMap.Builder(configuration, key + "-Inline", Integer.class, new ArrayList<>(), null).build();
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new UpdateSqlSource<E>(configuration, componentClass), SqlCommandType.UPDATE);
        msBuilder.resultMaps(Arrays.asList(inlineResultMap));
            /*SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(componentClass);
            if (cacheResult != null && cacheResult.isEnabled()) {
                msBuilder.flushCacheRequired(false);
                msBuilder.cache(cacheResult.getCache());
                msBuilder.useCache(true);
            }*/
        return msBuilder.build();
    }
}
