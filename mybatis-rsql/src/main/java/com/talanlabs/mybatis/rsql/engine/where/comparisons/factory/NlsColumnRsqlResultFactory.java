package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.INlsColumnRsqlHandler;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NlsColumnRsqlResultFactory extends AbstractColumnRsqlResultFactory<NlsColumn> {

    private final IRsqlConfiguration configuration;

    public NlsColumnRsqlResultFactory(IRsqlConfiguration configuration) {
        super(NlsColumn.class, configuration);

        this.configuration = configuration;
    }

    @Override
    public SqlResult buildComponentRsqlResult(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            ComparisonNode node, String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context) {
        String current = (StringUtils.isNotBlank(previousPropertyName) ? previousPropertyName + "." : "") + propertyDescriptor.getPropertyName();

        NlsColumn nlsColumn = ComponentMyBatisHelper.getNlsColumnAnnotation(componentDescriptor, propertyDescriptor);
        Class<?> javaType = nlsColumn.javaType() != void.class ? nlsColumn.javaType() : propertyDescriptor.getPropertyClass();
        JdbcType jdbcType = !JdbcType.UNDEFINED.equals(nlsColumn.jdbcType()) ? nlsColumn.jdbcType() : null;
        Class<? extends TypeHandler<?>> typeHandlerClass = !UnknownTypeHandler.class.equals(nlsColumn.typeHandler()) ? nlsColumn.typeHandler() : null;

        INlsColumnRsqlHandler nlsColumnRsqlHandler = configuration.getNlsColumnRsqlHandler();
        if (nlsColumnRsqlHandler == null) {
            throw new IllegalArgumentException("NlsColumnRsqlHandler is null");
        }

        SqlResult res = nlsColumnRsqlHandler.buildNameResultForWhere(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName(), current, tableJoinName, context);
        if (res == null) {
            throw new IllegalArgumentException(
                    "NlsColumnRsqlHandler return null for buildNameResultForWhere with component=" + componentDescriptor.getComponentClass() + " and property=" + propertyDescriptor.getPropertyName());
        }

        SqlResult crr = buildComponentRsqlResult(rsqlResultContext, componentDescriptor, propertyDescriptor, node, previousPropertyName, nextPropertyName, null, res.sql, javaType, jdbcType,
                typeHandlerClass, context);
        List<SqlResult.Join> joins = new ArrayList<>();
        if (res.joins != null) {
            joins.addAll(res.joins);
        }
        joins.addAll(crr.joins);

        Map<String, Object> parameterMap = new HashMap<>();
        if (res.parameterMap != null) {
            parameterMap.putAll(res.parameterMap);
        }
        parameterMap.putAll(crr.parameterMap);

        return SqlResult.of(joins, crr.sql, parameterMap);
    }
}
