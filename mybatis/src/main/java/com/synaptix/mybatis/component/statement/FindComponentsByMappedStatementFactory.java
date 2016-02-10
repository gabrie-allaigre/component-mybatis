package com.synaptix.mybatis.component.statement;

import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.mybatis.component.statement.sqlsource.FindComponentsByPropertyNameSqlSource;
import com.synaptix.mybatis.session.factory.AbstractMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class FindComponentsByMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindComponentsByMappedStatementFactory.class);

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        if (StatementNameHelper.isFindComponentsByKey(key)) {
            String componentName = StatementNameHelper.extractComponentNameInFindComponentsByKey(key);
            String[] propertyNames = StatementNameHelper.extractPropertyNamesInFindComponentsByKey(key);
            boolean ignoreCancel = StatementNameHelper.isIgnoreCancelInFindComponentsByKey(key);
            Class<? extends IComponent> componentClass = ComponentHelper.getComponentClass(componentName);
            if (componentClass != null && propertyNames != null && propertyNames.length > 0) {
                return createFindComponentsByMappedStatement(configuration, componentClass, ignoreCancel, propertyNames);
            }
        }
        return null;
    }

    public <E extends IComponent> MappedStatement createFindComponentsByMappedStatement(Configuration configuration, Class<E> componentClass, boolean ignoreCancel, String... propertyNames) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findComponentsBy for " + componentClass);
        }
        String key = StatementNameHelper.buildFindComponentsByKey(componentClass, ignoreCancel, propertyNames);
        ResultMap inlineResultMap = configuration.getResultMap(componentClass.getName());

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new FindComponentsByPropertyNameSqlSource<E>(configuration, componentClass, ignoreCancel, propertyNames),
                SqlCommandType.SELECT);
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
