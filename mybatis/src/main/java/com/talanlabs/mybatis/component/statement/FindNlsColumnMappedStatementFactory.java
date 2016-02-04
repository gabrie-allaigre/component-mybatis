package com.talanlabs.mybatis.component.statement;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.factory.AbstractMappedStatementFactory;
import com.talanlabs.mybatis.component.statement.sqlsource.FindNlsColumnSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class FindNlsColumnMappedStatementFactory extends AbstractMappedStatementFactory {

    private static final Logger LOG = LogManager.getLogger(FindNlsColumnMappedStatementFactory.class);

    @Override
    public boolean acceptKey(String key) {
        return StatementNameHelper.isFindNlsColumnKey(key);
    }

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        if (StatementNameHelper.isFindNlsColumnKey(key)) {
            Class<? extends IComponent> componentClass = StatementNameHelper.extractComponentClassInFindNlsColumnKey(key);
            String propertyName = StatementNameHelper.extractPropertyNameInFindNlsColumnByKey(key);
            if (componentClass != null) {
                return createFindNlsMappedStatement(componentConfiguration, key, componentClass, propertyName);
            }
        }
        return null;
    }

    private <E extends IComponent> MappedStatement createFindNlsMappedStatement(ComponentConfiguration componentConfiguration, String key, Class<E> componentClass, String propertyName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create find nls for " + componentClass);
        }

        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);

        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(componentConfiguration, key + "-Inline", propertyDescriptor.getPropertyClass(), new ArrayList<>(), null);

        MappedStatement.Builder msBuilder = new MappedStatement.Builder(componentConfiguration, key, new FindNlsColumnSqlSource<>(componentConfiguration, componentClass, propertyName),
                SqlCommandType.SELECT);
        msBuilder.resultMaps(Collections.singletonList(inlineResultMapBuilder.build()));
        msBuilder.flushCacheRequired(false);
        msBuilder.cache(null);
        msBuilder.useCache(false);
        return msBuilder.build();
    }
}
