package com.talanlabs.mybatis.component.statement.sqlsource;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.session.handler.INlsColumnHandler;
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
        additionalParameters.entrySet().forEach(e -> boundSql.setAdditionalParameter(e.getKey(), e.getValue()));
        return boundSql;
    }
}
