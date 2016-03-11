package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.NlsColumn;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FindNlsColumnSqlSource<E extends IComponent> implements SqlSource {

    private static final Logger LOG = LogManager.getLogger(FindNlsColumnSqlSource.class);

    private final ComponentConfiguration componentConfiguration;

    private final Class<E> componentClass;

    private final String propertyName;

    private final SqlSourceBuilder sqlSourceParser;

    private final Entity entity;

    private final NlsColumn nlsColumn;

    public FindNlsColumnSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass, String propertyName) {
        super();

        this.componentConfiguration = componentConfiguration;
        this.componentClass = componentClass;
        this.propertyName = propertyName;
        this.sqlSourceParser = new SqlSourceBuilder(componentConfiguration);

        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);
        nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(componentDescriptor, componentDescriptor.getPropertyDescriptor(propertyName));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("TABLE_NAME", entity.name());
        additionalParameters.put("COLUMN_NAME", nlsColumn.name());

        INlsColumnHandler nlsColumnHandler = componentConfiguration.getNlsColumnHandler();
        if (nlsColumnHandler == null) {
            throw new IllegalArgumentException("NlsColumnHandler is null in ComponentConfiguration");
        }

        Map<String, Object> aps = nlsColumnHandler.getAdditionalParameter(componentClass, propertyName);
        if (aps != null) {
            additionalParameters.putAll(aps);
        }

        SqlSource sqlSource = createSqlSource((Map<String, Object>) parameterObject, additionalParameters);
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }
        return boundSql;
    }

    @SuppressWarnings("unchecked")
    private SqlSource createSqlSource(Map<String, Object> parameterMap, Map<String, Object> additionalParameters) {
        try {
            String sql = buildFindNlsColumn(parameterMap, additionalParameters);
            Class<?> parameterType = Map.class;
            return sqlSourceParser.parse(sql, parameterType, additionalParameters);
        } catch (Exception e) {
            throw new BuilderException("Error invoking Count method for Select Cause: " + e, e);
        }
    }

    private String buildFindNlsColumn(Map<String, Object> parameterMap, Map<String, Object> additionalParameters) {
        INlsColumnHandler nlsColumnHandler = componentConfiguration.getNlsColumnHandler();
        if (nlsColumnHandler == null) {
            throw new IllegalArgumentException("NlsColumnHandler is null in ComponentConfiguration");
        }

        return nlsColumnHandler.buildFindNlsColumn(componentClass, propertyName, parameterMap, additionalParameters);
    }
}
