package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.annotation.Association;
import com.talanlabs.entity.annotation.FetchType;
import com.talanlabs.entity.annotation.JoinTable;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
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
        Association association = propertyDescriptor.getMethod().getAnnotation(Association.class);

        String[] propertySource = association.propertySource();
        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = ComponentResultMapHelper.prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        Class<?> javaType = association.javaType() != void.class ? association.javaType() : propertyDescriptor.getPropertyClass();

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), column, javaType);
        resultMappingBuilder.composites(ComponentResultMapHelper.buildComposites(componentConfiguration, sourceColumns));

        if (FetchType.LAZY.equals(association.fetchType())) {
            resultMappingBuilder.lazy(true);
        } else if (FetchType.EAGER.equals(association.fetchType())) {
            resultMappingBuilder.lazy(false);
        }
        if (StringUtils.isNotBlank(association.select())) {
            resultMappingBuilder.nestedQueryId(association.select());
        } else if (ComponentFactory.getInstance().isComponentType(javaType)) {
            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) javaType;
            String[] propertyTarget = association.propertyTarget();
            if (propertyTarget.length == 0) {
                String idPropertyName = EntityHelper.findIdPropertyName(subComponentClass);
                if (idPropertyName == null) {
                    throw new IllegalArgumentException(
                            "Not find Id property for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + ", fill propertyTarget");
                }
                propertyTarget = new String[] { idPropertyName };
            }

            ComponentDescriptor<?> subComponentDescriptor = ComponentFactory.getInstance().getDescriptor(subComponentClass);
            ComponentResultMapHelper.checkTarget(subComponentDescriptor, propertyTarget);

            JoinTable[] joinTables = association.joinTable();
            if (joinTables.length > 0) {
                List<Pair<String, Pair<String[], String[]>>> joins = ComponentResultMapHelper.joinTables(componentDescriptor, propertyDescriptor, joinTables, propertySource, propertyTarget);
                resultMappingBuilder.nestedQueryId(
                        StatementNameHelper.buildFindComponentsByJoinTableKey(componentDescriptor.getComponentClass(), subComponentClass, false, joins, propertySource, propertyTarget, null));
            } else {
                if (propertyTarget.length != propertySource.length) {
                    throw new IllegalArgumentException(
                            "Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }

                resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, false, propertyTarget, null));
            }
        } else {
            throw new IllegalArgumentException("Not accept Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return resultMappingBuilder.build();
    }
}
