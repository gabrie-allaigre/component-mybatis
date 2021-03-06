package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.ILikePolicy;
import com.talanlabs.mybatis.rsql.engine.IStringPolicy;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.rtext.Rtext;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public abstract class AbstractColumnRsqlResultFactory<E extends Annotation> extends AbstractRsqlResultFactory<E> {

    private static final Logger LOG = LogManager.getLogger(AbstractColumnRsqlResultFactory.class);

    private final IRsqlConfiguration configuration;
    private final Map<ComparisonOperator, OperatorConvert> operatorConvertMap;

    public AbstractColumnRsqlResultFactory(Class<E> annotationClass, IRsqlConfiguration configuration) {
        super(annotationClass);

        this.configuration = configuration;

        this.operatorConvertMap = new HashMap<>();
        addOperatorConvert(RSQLOperators.EQUAL, OperatorConvert.of("=", "like"));
        addOperatorConvert(RSQLOperators.NOT_EQUAL, OperatorConvert.of("<>", "not like"));
        addOperatorConvert(RSQLOperators.LESS_THAN, OperatorConvert.of("<", null));
        addOperatorConvert(RSQLOperators.LESS_THAN_OR_EQUAL, OperatorConvert.of("<=", null));
        addOperatorConvert(RSQLOperators.GREATER_THAN_OR_EQUAL, OperatorConvert.of(">=", null));
        addOperatorConvert(RSQLOperators.GREATER_THAN, OperatorConvert.of(">", null));
        addOperatorConvert(RSQLOperators.IN, OperatorConvert.of("IN", null));
        addOperatorConvert(RSQLOperators.NOT_IN, OperatorConvert.of("NOT IN", null));
    }

    public void addOperatorConvert(ComparisonOperator comparisonOperator, OperatorConvert operatorConvert) {
        operatorConvertMap.put(comparisonOperator, operatorConvert);
    }

    protected SqlResult buildComponentRsqlResult(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            ComparisonNode node, String previousPropertyName, String nextPropertyName, String tableJoinName, String columnName, Class<?> javaType, JdbcType jdbcType,
            Class<? extends TypeHandler<?>> typeHandlerClass, EngineContext context) {
        OperatorConvert type = operatorConvertMap.get(node.getOperator());
        if (type == null) {
            throw new IllegalArgumentException("Operator not define convert for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        Rtext rtext = configuration.getRtext();
        if (rtext == null) {
            throw new IllegalArgumentException("Rtext is null");
        }

        String name = (StringUtils.isNotBlank(tableJoinName) ? tableJoinName + "." : "") + columnName;
        IStringPolicy stringComparePolicy = configuration.getStringPolicy();

        List<String> arguments = node.getArguments();

        if (!node.getOperator().isMultiValue()) {
            String text = arguments.size() > 0 ? arguments.get(0) : null;

            if (text != null && type.likeSql != null && containWildcard(text)) {
                if (stringComparePolicy != null) {
                    Pair<String, List<String>> res = stringComparePolicy
                            .prepareNameAndParametersForWhere(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName(), node.getOperator(), name, arguments);
                    name = res.getLeft();
                    text = res.getRight().get(0);
                }

                return parseString(type, name, text, context);
            } else {
                if (stringComparePolicy != null && String.class == javaType) {
                    Pair<String, List<String>> res = stringComparePolicy
                            .prepareNameAndParametersForWhere(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName(), node.getOperator(), name, arguments);
                    name = res.getLeft();
                    text = res.getRight().get(0);
                }

                return parseValue(type, propertyDescriptor, rtext, name, javaType, jdbcType, typeHandlerClass, cleanSpecial(text), context);
            }
        } else {
            if (stringComparePolicy != null && String.class == javaType) {
                Pair<String, List<String>> res = stringComparePolicy
                        .prepareNameAndParametersForWhere(componentDescriptor.getComponentClass(), propertyDescriptor.getPropertyName(), node.getOperator(), name, arguments);
                name = res.getLeft();
                arguments = res.getRight();
            }

            return parseValues(type, propertyDescriptor, rtext, name, javaType, jdbcType, typeHandlerClass, arguments, context);
        }
    }

    private boolean containWildcard(String text) {
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '*') {
                return true;
            } else if (text.charAt(i) == '\\') {
                i++;
            }
            i++;
        }
        return false;
    }

    private String cleanSpecial(String text) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '\\') {
                i++;
                if (i < text.length()) {
                    sb.append(text.charAt(i));
                }
            } else {
                sb.append(text.charAt(i));
            }
            i++;
        }
        return sb.toString();
    }

    private SqlResult parseString(OperatorConvert operatorConvert, String name, String argument, EngineContext context) {
        ILikePolicy likePolicy = configuration.getLikePolicy();
        String likeSymbol = likePolicy.getLikeSymbol();

        Map<String, Object> parameterMap = new HashMap<>();
        StringJoiner sj = new StringJoiner(" || ");

        StringBuilder sb = new StringBuilder();
        boolean lastStar = false;
        int i = 0;
        while (i < argument.length()) {
            if (argument.charAt(i) == '*') {
                if (sb.length() > 0) {
                    String param = context.getNewParamName();
                    parameterMap.put(param, sb.toString());
                    sj.add(ComponentMyBatisHelper.buildColumn(String.class, null, null, param));
                }
                if (!lastStar) {
                    sj.add("'" + likeSymbol + "'");
                    lastStar = true;
                    sb = new StringBuilder();
                }
            } else if (argument.charAt(i) == '\\') {
                i++;
                if (i < argument.length()) {
                    sb.append(argument.charAt(i));
                    lastStar = false;
                }
            } else {
                sb.append(argument.charAt(i));
                lastStar = false;
            }
            i++;
        }
        if (sb.length() > 0) {
            String param = context.getNewParamName();
            parameterMap.put(param, sb.toString());
            sj.add(ComponentMyBatisHelper.buildColumn(String.class, null, null, param));
        }

        String res = name + " " + operatorConvert.likeSql + " " + sj.toString();
        if (StringUtils.isNotBlank(likePolicy.getEscapeSymbol())) {
            res += " ESCAPE '" + likePolicy.getEscapeSymbol() + "'";
        }

        return SqlResult.of(Collections.emptyList(), res, parameterMap);
    }

    private SqlResult parseValue(OperatorConvert operatorConvert, ComponentDescriptor.PropertyDescriptor propertyDescriptor, Rtext rtext, String name, Class<?> javaType, JdbcType jdbcType,
            Class<? extends TypeHandler<?>> typeHandlerClass, String text, EngineContext context) {
        Map<String, Object> parameterMap = new HashMap<>();

        String param = context.getNewParamName();
        String valueSql;
        try {
            Object value = rtext.fromText(text, propertyDescriptor.getPropertyType());
            parameterMap.put(param, value);

            valueSql = ComponentMyBatisHelper.buildColumn(javaType, jdbcType, typeHandlerClass, param);
        } catch (Exception e) {
            LOG.trace("Failed to convert text {} on {}", text, propertyDescriptor.getPropertyType(), e);

            parameterMap.put(param, text);

            valueSql = ComponentMyBatisHelper.buildColumn(String.class, null, null, param);
        }

        return SqlResult.of(Collections.emptyList(), name + " " + operatorConvert.sql + " " + valueSql, parameterMap);
    }

    private SqlResult parseValues(OperatorConvert operatorConvert, ComponentDescriptor.PropertyDescriptor propertyDescriptor, Rtext rtext, String name, Class<?> javaType, JdbcType jdbcType,
            Class<? extends TypeHandler<?>> typeHandlerClass, List<String> texts, EngineContext context) {
        Map<String, Object> parameterMap = new HashMap<>();

        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (String text : texts) {
            String param = context.getNewParamName();
            try {
                Object value = rtext.fromText(text, propertyDescriptor.getPropertyType());
                parameterMap.put(param, value);

                sj.add(ComponentMyBatisHelper.buildColumn(javaType, jdbcType, typeHandlerClass, param));
            } catch (Exception e) {
                LOG.trace("Failed to convert text {} on {}", text, propertyDescriptor.getPropertyType(), e);

                parameterMap.put(param, text);

                sj.add(ComponentMyBatisHelper.buildColumn(String.class, null, null, param));
            }
        }
        return SqlResult.of(Collections.emptyList(), name + " " + operatorConvert.sql + " " + sj.toString(), parameterMap);
    }

    public static class OperatorConvert {

        public final String sql;
        public final String likeSql;

        private OperatorConvert(String sql, String likeSql) {
            this.sql = sql;
            this.likeSql = likeSql;
        }

        public static OperatorConvert of(String sql) {
            return new OperatorConvert(sql, null);
        }

        public static OperatorConvert of(String sql, String likeSql) {
            return new OperatorConvert(sql, likeSql);
        }
    }
}
