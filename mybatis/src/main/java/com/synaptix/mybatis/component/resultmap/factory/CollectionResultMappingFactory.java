package com.synaptix.mybatis.component.resultmap.factory;

import com.google.common.reflect.TypeToken;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.annotation.Collection;
import com.synaptix.entity.annotation.FetchType;
import com.synaptix.entity.annotation.JoinTable;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

public class CollectionResultMappingFactory extends AbstractResultMappingFactory<Collection> {

    public CollectionResultMappingFactory() {
        super(Collection.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultMapping buildColumnResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Collection collection = propertyDescriptor.getMethod().getAnnotation(Collection.class);

        String[] propertySource = collection.propertySource();
        if (propertySource == null || propertySource.length == 0) {
            propertySource = new String[] { EntityHelper.findIdPropertyName(componentDescriptor.getComponentClass()) };
        }

        Class<?> javaType = collection.javaType() != null && collection.javaType() != void.class ? collection.javaType() : propertyDescriptor.getPropertyClass();
        if (!java.util.Collection.class.isAssignableFrom(javaType)) {
            throw new IllegalArgumentException(
                    "Not accept javaType for Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + " javaType=" + javaType);
        }

        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = ComponentResultMapHelper.prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyDescriptor.getPropertyName(), column, javaType);
        ComponentResultMapHelper.fillComposite(configuration, sourceColumns, resultMappingBuilder);
        if (FetchType.LAZY.equals(collection.fetchType())) {
            resultMappingBuilder.lazy(true);
        }
        if (StringUtils.isNotBlank(collection.select())) {
            resultMappingBuilder.nestedQueryId(collection.select());
        } else {
            Class<?> ofType = collection.ofType();
            if (ofType == null || ofType == void.class) {
                Type type = getCollectionElementType(TypeToken.of(propertyDescriptor.getPropertyType()));
                if (type == null) {
                    throw new IllegalArgumentException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }
                ofType = TypeToken.of(type).getRawType();
            }
            if (!ComponentFactory.getInstance().isComponentType(ofType)) {
                throw new IllegalArgumentException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) ofType;
            String[] propertyTarget = collection.propertyTarget();
            if (propertyTarget == null || propertyTarget.length == 0) {
                propertyTarget = new String[] { EntityHelper.findIdPropertyName(subComponentClass) };
            }
            ComponentResultMapHelper.checkTraget(ComponentFactory.getInstance().getDescriptor(subComponentClass), propertyTarget);

            boolean ignoreCancel = ICancellable.class.isAssignableFrom(subComponentClass);

            JoinTable[] joinTables = collection.joinTable();
            if (joinTables != null && joinTables.length > 0) {
                List<Pair<String, Pair<String[], String[]>>> joins = ComponentResultMapHelper.joinTables(componentDescriptor, propertyDescriptor, joinTables, propertySource, propertyTarget);
                resultMappingBuilder.nestedQueryId(
                        StatementNameHelper.buildFindComponentsByJoinTableKey(componentDescriptor.getComponentClass(), subComponentClass, ignoreCancel, joins, propertySource, propertyTarget));
            } else {
                if (propertyTarget.length != propertySource.length) {
                    throw new IllegalArgumentException(
                            "Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }

                resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, ignoreCancel, propertyTarget));
            }
        }
        return resultMappingBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private <T> Type getCollectionElementType(TypeToken<T> typeToken) {
        Type collectionType = typeToken.getSupertype((Class<? super T>) java.util.Collection.class).getType();

        if (collectionType instanceof WildcardType) {
            collectionType = ((WildcardType) collectionType).getUpperBounds()[0];
        }
        if (collectionType instanceof ParameterizedType) {
            return ((ParameterizedType) collectionType).getActualTypeArguments()[0];
        }
        return Object.class;
    }
}
