package com.talanlabs.mybatis.rsql.test.it.config;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.handler.INlsColumnHandler;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.INlsColumnRsqlHandler;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultNlsColumnHandler implements INlsColumnHandler, INlsColumnRsqlHandler {

    private String languageCode;

    public DefaultNlsColumnHandler() {
        super();

        this.languageCode = "fra";
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public Object getContext() {
        return languageCode;
    }

    @Override
    public Map<String, Object> getAdditionalParameter(Class<? extends IComponent> componentClass, String propertyName) {
        Map<String, Object> map = new HashMap<>();
        map.put("languageCode", languageCode);
        return map;
    }

    @Override
    public String getSelectNlsColumnId(Class<? extends IComponent> componentClass, String propertyName) {
        return "com.talanlabs.mybatis.rsql.test.it.mapper.NlsMapper.selectNlsColumn";
    }

    @Override
    public boolean isUpdateDefaultNlsColumn(Class<? extends IComponent> componentClass, String propertyName) {
        return "eng".equals(languageCode);
    }

    @Override
    public String getMergeNlsColumnId(Class<? extends IComponent> componentClass, String propertyName) {
        return "com.talanlabs.mybatis.rsql.test.it.mapper.NlsMapper.mergeNlsColumn";
    }

    @Override
    public String getDeleteNlsColumnsId(Class<? extends IComponent> componentClass) {
        return "com.talanlabs.mybatis.rsql.test.it.mapper.NlsMapper.deleteNlsColumns";
    }

    @Override
    public SqlResult buildNameResultForWhere(Class<? extends IComponent> componentClass, String propertyName, String fullPropertyName, String tableJoinName, EngineContext context) {
        ComponentDescriptor<?> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
        ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);

        NlsColumn nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(componentDescriptor, propertyDescriptor);
        if (nlsColumn == null) {
            throw new IllegalArgumentException("Not find NlsColumn for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyName);
        }

        List<SqlResult.Join> joins = new ArrayList<>();
        String joinTableName = context.getJoinName(fullPropertyName);
        Map<String, Object> parameterMap = new HashMap<>();
        if (StringUtils.isBlank(joinTableName)) {
            joinTableName = context.getNewJoinName(fullPropertyName);

            Entity entity = ComponentMyBatisHelper.getEntityAnnotation(componentDescriptor);
            if (entity == null) {
                throw new IllegalArgumentException("Not find Entity for Component=" + componentDescriptor.getComponentClass());
            }

            ComponentDescriptor.PropertyDescriptor idPpropertyPropertyDescriptor = EntityHelper.findIdPropertyDescriptor(componentClass);
            if (idPpropertyPropertyDescriptor == null) {
                throw new IllegalArgumentException("Not find Id for Component=" + componentDescriptor.getComponentClass());
            }
            Column idColumn = ComponentMyBatisHelper.getColumnAnnotation(componentDescriptor, idPpropertyPropertyDescriptor);
            if (idColumn == null) {
                throw new IllegalArgumentException("Not find Column for Component=" + componentDescriptor.getComponentClass() + " with property=" + idPpropertyPropertyDescriptor.getPropertyName());
            }

            String tableNameParam = context.getNewParamName();
            parameterMap.put(tableNameParam, entity.name());
            String columnNameParam = context.getNewParamName();
            parameterMap.put(columnNameParam, nlsColumn.name());
            String languageCodeParam = context.getNewParamName();
            parameterMap.put(languageCodeParam, languageCode);

            String joinSql =
                    "T_NLS " + joinTableName + " ON " + joinTableName + ".TABLE_NAME = #{" + tableNameParam + ",javaType=java.lang.String} AND " + joinTableName + ".COLUMN_NAME = #{" + columnNameParam
                            + ",javaType=java.lang.String} AND " + joinTableName + ".LANGUAGE_CODE = #{" + languageCodeParam + ",javaType=java.lang.String} AND " + joinTableName + ".TABLE_ID = " + (
                            StringUtils.isNotBlank(tableJoinName) ?
                                    tableJoinName + "." :
                                    "") + idColumn.name();

            joins.add(SqlResult.Join.of(SqlResult.Join.Type.LeftOuter, joinSql));
        }

        String name = "NVL(" + joinTableName + "." + "MEANING" + ", " + (StringUtils.isNotBlank(tableJoinName) ? tableJoinName + "." : "") + nlsColumn.name() + ")";

        return SqlResult.of(joins, name, parameterMap);
    }

    @Override
    public SqlResult buildNameResultForOrderBy(Class<? extends IComponent> componentClass, String propertyName, String fullPropertyName, String tableJoinName, EngineContext context) {
        return buildNameResultForWhere(componentClass, propertyName, fullPropertyName, tableJoinName, context);
    }
}
