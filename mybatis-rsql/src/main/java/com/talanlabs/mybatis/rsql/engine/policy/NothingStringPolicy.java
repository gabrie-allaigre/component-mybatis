package com.talanlabs.mybatis.rsql.engine.policy;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.rsql.engine.IStringPolicy;
import com.talanlabs.mybatis.rsql.sort.SortDirection;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NothingStringPolicy implements IStringPolicy {

    @Override
    public Pair<String, List<String>> prepareNameAndParametersForWhere(Class<? extends IComponent> componentClass, String propertyName, ComparisonOperator operator, String name,
            List<String> parameterValues) {
        return Pair.of(name, parameterValues);
    }

    @Override
    public String prepareNameForOrderBy(Class<? extends IComponent> componentClass, String propertyName, SortDirection sortDirection, String name) {
        return name;
    }
}
