package com.talanlabs.mybatis.rsql.engine.where.comparisons.factory;

import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.entity.annotation.Association;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.JoinTable;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.resultmap.factory.ComponentResultMapHelper;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.IllegalPropertyException;
import com.talanlabs.mybatis.rsql.engine.SqlResult;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class AssociationRsqlResultFactory extends AbstractRsqlResultFactory<Association> {

    public AssociationRsqlResultFactory() {
        super(Association.class);
    }

    @Override
    public SqlResult buildComponentRsqlResult(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            ComparisonNode node, String previousPropertyName, String nextPropertyName, String tableJoinName, EngineContext context) {
        String current = (StringUtils.isNotBlank(previousPropertyName) ? previousPropertyName + "." : "") + propertyDescriptor.getPropertyName();

        ComponentDescriptor<?> subComponentDescriptor = ComponentMyBatisHelper.getComponentDescriptorForAssociations(componentDescriptor, propertyDescriptor.getPropertyName());

        String joinTableName = context.getJoinName(current);
        if (StringUtils.isNotBlank(joinTableName)) {
            return rsqlResultContext.visit(subComponentDescriptor, node, current, nextPropertyName, joinTableName, context);
        } else {
            return buildJoins(rsqlResultContext, componentDescriptor, propertyDescriptor, subComponentDescriptor, node, previousPropertyName, nextPropertyName, tableJoinName, context);
        }
    }

    private SqlResult buildJoins(IRsqlResultContext rsqlResultContext, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor,
            ComponentDescriptor<?> subComponentDescriptor, ComparisonNode node, String previousPropertyName, String nextPropertyName, String tablePrefix, EngineContext context) {
        String current = (StringUtils.isNotBlank(previousPropertyName) ? previousPropertyName + "." : "") + propertyDescriptor.getPropertyName();

        Association association = propertyDescriptor.getMethod().getAnnotation(Association.class);
        if (StringUtils.isNotBlank(association.select())) {
            throw new IllegalPropertyException(String.format("Property %s not accepted, not use select association", current));
        }

        Pair<String[], String[]> source = ComponentResultMapHelper.prepareColumns(componentDescriptor, propertyDescriptor, association.propertySource(), false);

        Entity entity = ComponentMyBatisHelper.getEntityAnnotation(subComponentDescriptor);
        if (entity == null) {
            throw new IllegalArgumentException("Not find Entity for Component=" + componentDescriptor.getComponentClass());
        }

        Pair<String[], String[]> target = ComponentResultMapHelper.prepareColumns(subComponentDescriptor, propertyDescriptor, association.propertyTarget(), true);

        String joinTableName = context.getNewJoinName(current);

        List<SqlResult.Join> joins = new ArrayList<>();

        JoinTable[] joinTables = association.joinTable();
        if (joinTables.length > 0) {
            List<Pair<String, Pair<String[], String[]>>> joinDescs = ComponentResultMapHelper.joinTables(componentDescriptor, propertyDescriptor, joinTables, source.getLeft(), target.getLeft());

            String prec = tablePrefix;
            for (int j = 0; j < joinDescs.size(); j++) {
                Pair<String, Pair<String[], String[]>> joinDesc = joinDescs.get(j);

                if (j == 0) {
                    joins.add(buildJoin(joinDesc.getLeft(), joinTableName + "_" + j, prec, source.getRight(), joinDesc.getRight().getLeft()));
                } else {
                    joins.add(buildJoin(joinDesc.getLeft(), joinTableName + "_" + j, prec, joinDescs.get(j - 1).getRight().getRight(), joinDesc.getRight().getLeft()));
                }
                prec = joinTableName + "_" + j;
            }

            joins.add(buildJoin(entity.name(), joinTableName, prec, joinDescs.get(joinDescs.size() - 1).getRight().getRight(), target.getRight()));
        } else {
            if (target.getRight().length != source.getRight().length) {
                throw new IllegalArgumentException(
                        "Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            joins.add(buildJoin(entity.name(), joinTableName, tablePrefix, source.getRight(), target.getRight()));
        }

        SqlResult crr = rsqlResultContext.visit(subComponentDescriptor, node, current, nextPropertyName, joinTableName, context);
        joins.addAll(crr.joins);
        return SqlResult.of(joins, crr.sql, crr.parameterMap);
    }

    private SqlResult.Join buildJoin(String tableName, String joinName, String previousJoinName, String[] sourceColumns, String[] targetColumns) {
        StringJoiner sj = new StringJoiner(" AND ");

        String realJoinName = StringUtils.isNotBlank(joinName) ? joinName + "." : "";
        String realPreviousJoinName = StringUtils.isNotBlank(previousJoinName) ? previousJoinName + "." : "";
        for (int j = 0; j < sourceColumns.length; j++) {
            sj.add(realJoinName + targetColumns[j] + " = " + realPreviousJoinName + sourceColumns[j]);
        }

        return SqlResult.Join.of(SqlResult.Join.Type.Inner, tableName + " " + joinName + " ON " + sj.toString());
    }
}
