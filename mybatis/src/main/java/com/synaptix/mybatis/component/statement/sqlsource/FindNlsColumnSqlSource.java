package com.synaptix.mybatis.component.statement.sqlsource;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.NlsColumn;
import com.synaptix.mybatis.component.helper.ComponentMyBatisHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.HashMap;
import java.util.Map;

public class FindNlsColumnSqlSource<E extends IComponent> implements SqlSource {

    private final ComponentConfiguration componentConfiguration;

    private final Class<E> componentClass;

    private final String propertyName;

    private final Entity entity;

    private final NlsColumn nlsColumn;

    public FindNlsColumnSqlSource(ComponentConfiguration componentConfiguration, Class<E> componentClass, String propertyName) {
        super();

        this.componentConfiguration = componentConfiguration;
        this.componentClass = componentClass;
        this.propertyName = propertyName;

        ComponentDescriptor<E> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);
        nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(componentDescriptor, componentDescriptor.getPropertyDescriptor(propertyName));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put("tableName", entity.name());
        additionalParameters.put("columnName", nlsColumn.name());

        INlsColumnHandler nlsColumnHandler = componentConfiguration.getNlsColumnHandler();
        if (nlsColumnHandler == null) {
            throw new IllegalArgumentException("NlsColumnHandler is null in ComponentConfiguration");
        }

        Map<String, Object> aps = nlsColumnHandler.getAdditionalParameter(componentClass, propertyName);
        if (aps != null) {
            additionalParameters.putAll(aps);
        }

        String selectId = nlsColumnHandler.getSelectNlsColumnId(componentClass, propertyName);
        MappedStatement mappedStatement = componentConfiguration.getMappedStatement(selectId);

        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }
        return boundSql;
    }
}
