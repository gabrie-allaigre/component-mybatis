package com.talanlabs.mybatis.rsql.statement.sqlsource;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.rsql.configuration.IRsqlConfiguration;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import com.talanlabs.mybatis.rsql.engine.orderby.ComponentSortVisitor;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.sort.SortParser;
import com.talanlabs.mybatis.rsql.statement.Request;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.SqlSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractRsqlSqlSource<E extends IComponent> implements SqlSource {

    protected final Class<E> componentClass;
    protected final SqlSourceBuilder sqlSourceParser;
    protected final IRsqlConfiguration rsqlConfiguration;

    public AbstractRsqlSqlSource(ComponentConfiguration componentConfiguration, IRsqlConfiguration rsqlConfiguration, Class<E> componentClass) {
        super();

        this.rsqlConfiguration = rsqlConfiguration;
        this.componentClass = componentClass;
        this.sqlSourceParser = new SqlSourceBuilder(componentConfiguration);
    }

    protected String buildSqlFromWhereOrderBy(String rsql, Request.ICustomRequest customRequest, String sort, Request.ICustomSort customSortLeft, Request.ICustomSort customSortRight,
            Map<String, Object> additionalParameters, EngineContext context) {
        List<SqlResult> whereSqlResults = new ArrayList<>();

        if (StringUtils.isNotBlank(rsql)) {
            ComponentRsqlVisitor<E> componentRsqlVisitor = rsqlConfiguration.getComponentRsqlVisitor(componentClass);
            RSQLParser rsqlParser = rsqlConfiguration.getRsqlParser();
            whereSqlResults.add(rsqlParser.parse(rsql).accept(componentRsqlVisitor, context));
        }

        if (customRequest != null) {
            whereSqlResults.add(customRequest.buildSqlResult(context));
        }

        SqlResult whereSqlResult = whereSqlResults.stream().collect(SqlResult.SqlResultJoiner.joining(" AND ", "(", ")"));

        List<SqlResult> sortSqlResults = new ArrayList<>();
        if (customSortLeft != null) {
            sortSqlResults.add(customSortLeft.buildSqlResult(context));
        }

        if (StringUtils.isNotBlank(sort)) {
            ComponentSortVisitor<E> componentSortVisitor = rsqlConfiguration.getComponentSortVisitor(componentClass);
            SortParser sortParser = rsqlConfiguration.getSortParser();
            sortSqlResults.add(componentSortVisitor.visit(sortParser.parse(sort), context));
        }

        if (customSortRight != null) {
            sortSqlResults.add(customSortRight.buildSqlResult(context));
        }

        SqlResult sortSqlResult = sortSqlResults.stream().collect(SqlResult.SqlResultJoiner.joining(", ", "", ""));

        ComponentDescriptor<E> cd = ComponentFactory.getInstance().getDescriptor(componentClass);
        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(cd);

        StringBuilder sb = new StringBuilder();
        String prefix = StringUtils.isNotBlank(context.getDefaultTableName()) ? " " + context.getDefaultTableName() : "";
        sb.append("FROM ").append(entity.name()).append(prefix);

        List<SqlResult.Join> joins = new ArrayList<>();
        if (whereSqlResult.joins != null) {
            joins.addAll(whereSqlResult.joins);
        }
        if (sortSqlResult.joins != null) {
            joins.addAll(sortSqlResult.joins);
        }
        if (!joins.isEmpty()) {
            for (SqlResult.Join join : joins) {
                switch (join.type) {
                case Inner:
                    sb.append(" INNER JOIN ").append(join.sql);
                    break;
                case Outer:
                    sb.append(" OUTER JOIN ").append(join.sql);
                    break;
                case LeftOuter:
                    sb.append(" LEFT OUTER JOIN ").append(join.sql);
                    break;
                case RightOuter:
                    sb.append(" RIGHT OUTER JOIN ").append(join.sql);
                    break;
                default:
                    break;
                }
            }
        }

        if (StringUtils.isNotBlank(whereSqlResult.sql)) {
            sb.append(" WHERE ").append(whereSqlResult.sql);
        }
        if (StringUtils.isNotBlank(sortSqlResult.sql)) {
            sb.append(" ORDER BY ").append(sortSqlResult.sql);
        }

        if (whereSqlResult.parameterMap != null) {
            additionalParameters.putAll(whereSqlResult.parameterMap);
        }
        if (sortSqlResult.parameterMap != null) {
            additionalParameters.putAll(sortSqlResult.parameterMap);
        }

        return sb.toString();
    }
}
