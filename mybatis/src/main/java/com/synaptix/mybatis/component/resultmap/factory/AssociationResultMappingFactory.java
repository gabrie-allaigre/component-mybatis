package com.synaptix.mybatis.component.resultmap.factory;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.annotation.Association;
import com.synaptix.entity.annotation.FetchType;
import com.synaptix.entity.annotation.JoinTable;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.List;

public class AssociationResultMappingFactory extends AbstractResultMappingFactory<Association> {

    public AssociationResultMappingFactory() {
        super(Association.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Association assossiation = propertyDescriptor.getMethod().getAnnotation(Association.class);

        String[] propertySource = assossiation.propertySource();
        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = ComponentResultMapHelper.prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        Class<?> javaType = assossiation.javaType() != void.class ? assossiation.javaType() : propertyDescriptor.getPropertyClass();

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), column, javaType);
        resultMappingBuilder.composites(ComponentResultMapHelper.buildComposites(componentConfiguration, sourceColumns));

        if (FetchType.LAZY.equals(assossiation.fetchType())) {
            resultMappingBuilder.lazy(true);
        } else if (FetchType.EAGER.equals(assossiation.fetchType())) {
            resultMappingBuilder.lazy(false);
        }
        if (StringUtils.isNotBlank(assossiation.select())) {
            resultMappingBuilder.nestedQueryId(assossiation.select());
        } else if (ComponentFactory.getInstance().isComponentType(javaType)) {
            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) javaType;
            String[] propertyTarget = assossiation.propertyTarget();
            if (propertyTarget.length == 0) {
                String idPropertyName = EntityHelper.findIdPropertyName(subComponentClass);
                if (idPropertyName == null) {
                    throw new IllegalArgumentException(
                            "Not find Id property for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + ", fill propertyTarget");
                }
                propertyTarget = new String[] { idPropertyName };
            }
            ComponentResultMapHelper.checkTarget(ComponentFactory.getInstance().getDescriptor(subComponentClass), propertyTarget);

            JoinTable[] joinTables = assossiation.joinTable();
            if (joinTables.length > 0) {
                List<Pair<String, Pair<String[], String[]>>> joins = ComponentResultMapHelper.joinTables(componentDescriptor, propertyDescriptor, joinTables, propertySource, propertyTarget);
                resultMappingBuilder
                        .nestedQueryId(StatementNameHelper.buildFindComponentsByJoinTableKey(componentDescriptor.getComponentClass(), subComponentClass, false, joins, propertySource, propertyTarget));
            } else {
                if (propertyTarget.length != propertySource.length) {
                    throw new IllegalArgumentException(
                            "Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }

                resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, false, propertyTarget));
            }
        } else {
            throw new IllegalArgumentException("Not accept Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return resultMappingBuilder.build();
    }
}
