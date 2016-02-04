package com.talanlabs.mybatis.component.resultmap.factory;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.factory.ComponentDescriptor;
import com.talanlabs.component.factory.ComponentFactory;
import com.talanlabs.entity.ICancelable;
import com.talanlabs.entity.annotation.Collection;
import com.talanlabs.entity.annotation.FetchType;
import com.talanlabs.entity.annotation.JoinTable;
import com.talanlabs.entity.annotation.OrderBy;
import com.talanlabs.entity.helper.EntityHelper;
import com.talanlabs.mybatis.component.helper.ComponentMyBatisHelper;
import com.talanlabs.mybatis.component.session.ComponentConfiguration;
import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.List;

public class CollectionResultMappingFactory extends AbstractResultMappingFactory<Collection> {

    public CollectionResultMappingFactory() {
        super(Collection.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultMapping buildColumnResultMapping(ComponentConfiguration componentConfiguration, ComponentDescriptor<?> componentDescriptor,
            ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Collection collection = propertyDescriptor.getMethod().getAnnotation(Collection.class);

        String[] propertySource = collection.propertySource();
        if (propertySource.length == 0) {
            String idPropertyName = EntityHelper.findIdPropertyName(componentDescriptor.getComponentClass());
            if (idPropertyName == null) {
                throw new IllegalArgumentException(
                        "Not find Id property for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + ", fill propertySource");
            }
            propertySource = new String[] { idPropertyName };
        }

        Class<?> javaType = collection.javaType() != java.util.Collection.class ? collection.javaType() : propertyDescriptor.getPropertyClass();
        if (!java.util.Collection.class.isAssignableFrom(javaType)) {
            throw new IllegalArgumentException(
                    "Not accept javaType for Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + " javaType=" + javaType);
        }

        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = ComponentResultMapHelper.prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(componentConfiguration, propertyDescriptor.getPropertyName(), column, javaType);
        resultMappingBuilder.composites(ComponentResultMapHelper.buildComposites(componentConfiguration, sourceColumns));
        if (FetchType.LAZY.equals(collection.fetchType())) {
            resultMappingBuilder.lazy(true);
        } else if (FetchType.EAGER.equals(collection.fetchType())) {
            resultMappingBuilder.lazy(false);
        }
        if (StringUtils.isNotBlank(collection.select())) {
            resultMappingBuilder.nestedQueryId(collection.select());
        } else {
            Class<?> ofType = ComponentMyBatisHelper.getCollectionElementClass(componentDescriptor, propertyDescriptor, collection);
            if (!ComponentFactory.getInstance().isComponentType(ofType)) {
                throw new IllegalArgumentException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) ofType;
            ComponentDescriptor<?> subComponentDescriptor = ComponentFactory.getInstance().getDescriptor(subComponentClass);

            String[] propertyTarget = collection.propertyTarget();
            ComponentResultMapHelper.checkTarget(subComponentDescriptor, propertyTarget);

            boolean ignoreCancel = ICancelable.class.isAssignableFrom(subComponentClass);

            OrderBy[] orderBies = collection.orderBy();
            List<Pair<String, String>> os = ComponentResultMapHelper.orderBies(subComponentDescriptor, orderBies);

            JoinTable[] joinTables = collection.joinTable();
            if (joinTables.length > 0) {
                List<Pair<String, Pair<String[], String[]>>> joins = ComponentResultMapHelper.joinTables(componentDescriptor, propertyDescriptor, joinTables, propertySource, propertyTarget);
                resultMappingBuilder.nestedQueryId(
                        StatementNameHelper.buildFindComponentsByJoinTableKey(componentDescriptor.getComponentClass(), subComponentClass, ignoreCancel, joins, propertySource, propertyTarget, os));
            } else {
                if (propertyTarget.length != propertySource.length) {
                    throw new IllegalArgumentException(
                            "Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }

                resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, ignoreCancel, propertyTarget, os));
            }
        }
        return resultMappingBuilder.build();
    }
}
