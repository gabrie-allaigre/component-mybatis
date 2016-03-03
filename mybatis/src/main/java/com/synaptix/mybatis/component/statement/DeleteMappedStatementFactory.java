package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.statement.sqlsource.DeleteSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

public class DeleteMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(DeleteMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isDeleteKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInDeleteKey(key);
            Class<? extends IComponent> componentClass = ComponentMyBatisHelper.loadComponentClass(componentName);
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
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new DeleteSqlSource<E>(configuration, componentClass), SqlCommandType.DELETE);
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
