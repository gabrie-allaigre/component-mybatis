package com.talanlabs.mybatis.rsql.engine.orderby.sorts.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IStringPolicy;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.sort.SortDirection;
import com.talanlabs.mybatis.rsql.sort.SortDirections;
import com.talanlabs.mybatis.rsql.statement.RsqlMappedStatementFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractColumnSortResultFactory<E extends Annotation> extends AbstractSortResultFactory<E> {

    private static final Logger LOG = LogManager.getLogger(RsqlMappedStatementFactory.class);

    private final IRsqlConfiguration configuration;
    private final Map<SortDirection, DirectionConvert> directionConvertMap;

    public AbstractColumnSortResultFactory(Class<E> annotationClass, IRsqlConfiguration configuration) {
        super(annotationClass);

        this.configuration = configuration;

        this.directionConvertMap = new HashMap<>();
        addOperatorConvert(SortDirections.ASC, DirectionConvert.of("ASC"));
        addOperatorConvert(SortDirections.ASC_NULLS_LAST, DirectionConvert.of("ASC NULLS LAST"));
        addOperatorConvert(SortDirections.ASC_NULLS_FIRST, DirectionConvert.of("ASC NULLS FIRST"));
        addOperatorConvert(SortDirections.DESC, DirectionConvert.of("DESC"));
        addOperatorConvert(SortDirections.DESC_NULLS_LAST, DirectionConvert.of("DESC NULLS LAST"));
        addOperatorConvert(SortDirections.DESC_NULLS_FIRST, DirectionConvert.of("DESC NULLS FIRST"));
    }

    public void addOperatorConvert(SortDirection sortDirection, DirectionConvert directionConvert) {
        directionConvertMap.put(sortDirection, directionConvert);
    }

    protected SqlResult buildComponentSortResult(ISortResultContext sortResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            SortDirection sortDirection, String previousPropertyName, String nextPropertyName, String tableJoinName, String columnName, Class<?> javaType, JdbcType jdbcType,
            Class<? extends TypeHandler<?>> typeHandlerClass, EngineContext context) {
        DirectionConvert type = directionConvertMap.get(sortDirection);
        if (type == null) {
            throw new IllegalArgumentException("SortDirection not define convert for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        String name = (StringUtils.isNotBlank(tableJoinName) ? tableJoinName + "." : "") + columnName;
        IStringPolicy stringComparePolicy = configuration.getStringPolicy();

        if (stringComparePolicy != null && String.class == javaType) {
            name = stringComparePolicy.prepareNameForOrderBy(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName(), sortDirection, name);
        }

        return parseString(type, name, context);
    }

    private SqlResult parseString(DirectionConvert directionConvert, String name, EngineContext context) {
        Map<String, Object> parameterMap = new HashMap<>();
        return SqlResult.of(Collections.emptyList(), name + " " + directionConvert.sql, parameterMap);
    }

    public static class DirectionConvert {

        public final String sql;

        private DirectionConvert(String sql) {
            this.sql = sql;
        }

        public static DirectionConvert of(String sql) {
            return new DirectionConvert(sql);
        }

    }
}
