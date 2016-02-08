package com.synaptix.mybatis.component;

import com.synaptix.component.IComponent;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.sqlsource.FindComponentsByPropertyNameSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class ComponentMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentMappedStatementFactory.class);

    private final Configuration configuration;

    public ComponentMappedStatementFactory(Configuration configuration) {
        super();

        this.configuration = configuration;
    }

    public <E extends IComponent> MappedStatement createComponentFindEntityByIdMappedStatement(Class<E> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create findEntityById for " + componentClass);
        }

        String key = new StringBuilder().append(componentClass.getName()).append("/findEntityById").toString();
        ResultMap inlineResultMap = configuration.getResultMap(componentClass.getName());

        String idPropertyName = EntityHelper.findIdPropertyName(componentClass);

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(configuration, key, new FindComponentsByPropertyNameSqlSource<E>(configuration, componentClass, idPropertyName, false),
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

    public <E extends IComponent> MappedStatement createComponentFindChildrenByIdParentMappedStatement(Class<E> componentClass, String idParentPropertyName) {
        return null;
    }
}
