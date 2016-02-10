package com.synaptix.mybatis.component.resultmap;

import com.google.common.reflect.TypeToken;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.annotation.*;
import com.synaptix.entity.helper.EntityHelper;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.session.factory.AbstractResultMapFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ComponentResultMapFactory extends AbstractResultMapFactory {

    private static final Logger LOG = LogManager.getLogger(ComponentResultMapFactory.class);

    @Override
    public ResultMap createResultMap(Configuration configuration, String key) {
        Class<? extends IComponent> componentClass = ComponentHelper.getComponentClass(key);
        if (componentClass != null) {
            return createComponentResultMap(configuration, componentClass);
        }
        return null;
    }

    private ResultMap createComponentResultMap(Configuration configuration, Class<? extends IComponent> componentClass) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Create ResultMap for " + componentClass);
        }

        String key = componentClass.getName();
        List<ResultMapping> resultMappings = createResultMappings(configuration, componentClass);
        ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, key, componentClass, resultMappings, null);
        return inlineResultMapBuilder.build();
    }

    private List<ResultMapping> createResultMappings(Configuration configuration, Class<? extends IComponent> componentClass) {
        ComponentDescriptor<?> componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);

        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

        Set<String> propertyNames = componentDescriptor.getPropertyNames();
        for (String propertyName : propertyNames) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = componentDescriptor.getPropertyDescriptor(propertyName);

            Method method = propertyDescriptor.getMethod();

            if (method.isAnnotationPresent(Column.class)) {
                resultMappings.add(buildColumnResultMapping(configuration, componentDescriptor, propertyDescriptor));
            } else if (method.isAnnotationPresent(Association.class)) {
                resultMappings.add(buildAssociationResultMapping(configuration, componentDescriptor, propertyDescriptor));
            } else if (method.isAnnotationPresent(Collection.class)) {
                resultMappings.add(buildCollectionResultMapping(configuration, componentDescriptor, propertyDescriptor));
            } else {
                System.out.println("Not Yet implemented propertyName=" + propertyDescriptor.getPropertyName());
            }
        }

        return resultMappings;
    }

    private ResultMapping buildColumnResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        String columnName = propertyDescriptor.getMethod().getAnnotation(Column.class).name();
        if (StringUtils.isBlank(columnName)) {
            throw new IllegalArgumentException("Not name in column for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyDescriptor.getPropertyName(), columnName, propertyDescriptor.getPropertyClass());
        if (propertyDescriptor.getMethod().isAnnotationPresent(Id.class)) {
            resultMappingBuilder.flags(Arrays.asList(ResultFlag.ID));
        }
        return resultMappingBuilder.build();
    }

    private ResultMapping buildAssociationResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Association assossiation = propertyDescriptor.getMethod().getAnnotation(Association.class);

        String[] propertySource = assossiation.propertySource();
        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyDescriptor.getPropertyName(), column,
                assossiation.javaType() == void.class ? propertyDescriptor.getPropertyClass() : assossiation.javaType());
        fillComposite(configuration, sourceColumns, resultMappingBuilder);

        if (FetchType.LAZY.equals(assossiation.fetchType())) {
            resultMappingBuilder.lazy(true);
        }
        if (StringUtils.isNotBlank(assossiation.select())) {
            resultMappingBuilder.nestedQueryId(assossiation.select());
        } else if (ComponentFactory.getInstance().isComponentType(propertyDescriptor.getPropertyType())) {
            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) propertyDescriptor.getPropertyClass();
            String[] propertyTarget = assossiation.propertyTarget();
            if (propertyTarget == null || propertyTarget.length == 0) {
                propertyTarget = new String[] { EntityHelper.findIdPropertyName(subComponentClass) };
            }
            checkTraget(ComponentFactory.getInstance().getDescriptor(subComponentClass), propertyTarget);

            if (propertyTarget.length != propertySource.length) {
                throw new RuntimeException("Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, false, propertyTarget));
        } else {
            throw new RuntimeException("Not accept Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        return resultMappingBuilder.build();
    }

    private ResultMapping buildCollectionResultMapping(Configuration configuration, ComponentDescriptor<?> componentDescriptor, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        Collection collection = propertyDescriptor.getMethod().getAnnotation(Collection.class);

        String[] propertySource = collection.propertySource();
        if (propertySource == null || propertySource.length == 0) {
            propertySource = new String[] { EntityHelper.findIdPropertyName(componentDescriptor.getComponentClass()) };
        }

        Class<?> javaType = collection.javaType() == void.class ? propertyDescriptor.getPropertyClass() : collection.javaType();
        if (!java.util.Collection.class.isAssignableFrom(javaType)) {
            throw new RuntimeException(
                    "Not accept javaType for Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName() + " javaType=" + javaType);
        }

        String column = null;
        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns = prepareSourceColumns(componentDescriptor, propertySource);
        if (sourceColumns.size() == 1) {
            column = sourceColumns.get(0).getRight();
        }

        ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(configuration, propertyDescriptor.getPropertyName(), column, javaType);
        fillComposite(configuration, sourceColumns, resultMappingBuilder);
        if (FetchType.LAZY.equals(collection.fetchType())) {
            resultMappingBuilder.lazy(true);
        }
        if (StringUtils.isNotBlank(collection.select())) {
            resultMappingBuilder.nestedQueryId(collection.select());
        } else {
            Class<?> ofType = collection.ofType();
            if (ofType == void.class) {
                Type type = getCollectionElementType(TypeToken.of(propertyDescriptor.getPropertyType()));
                if (type == null) {
                    throw new RuntimeException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
                }
                ofType = TypeToken.of(type).getRawType();
            }
            if (!ComponentFactory.getInstance().isComponentType(ofType)) {
                throw new RuntimeException("Not accept Collection for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            Class<? extends IComponent> subComponentClass = (Class<? extends IComponent>) ofType;
            String[] propertyTarget = collection.propertyTarget();
            if (propertyTarget == null || propertyTarget.length == 0) {
                propertyTarget = new String[] { EntityHelper.findIdPropertyName(subComponentClass) };
            }
            checkTraget(ComponentFactory.getInstance().getDescriptor(subComponentClass), propertyTarget);

            if (propertyTarget.length != propertySource.length) {
                throw new RuntimeException("Not same lenght property Association for Component=" + componentDescriptor.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
            }

            boolean ignoreCancel = ICancellable.class.isAssignableFrom(subComponentClass);
            resultMappingBuilder.nestedQueryId(StatementNameHelper.buildFindComponentsByKey(subComponentClass, ignoreCancel, propertyTarget));
        }
        return resultMappingBuilder.build();
    }

    private void fillComposite(Configuration configuration, List<Pair<ComponentDescriptor.PropertyDescriptor, String>> sourceColumns, ResultMapping.Builder resultMappingBuilder) {
        if (sourceColumns.size() > 1) {
            List<ResultMapping> composites = new ArrayList<>();
            int i = 0;
            for (Pair<ComponentDescriptor.PropertyDescriptor, String> sourceColumn : sourceColumns) {
                ComponentDescriptor.PropertyDescriptor pd = sourceColumn.getLeft();
                composites.add(new ResultMapping.Builder(configuration, StatementNameHelper.buildParam(i), sourceColumn.getRight(), pd.getPropertyClass()).build());
                i++;
            }
            resultMappingBuilder.composites(composites);
        }
    }

    private void checkTraget(ComponentDescriptor<?> cd, String[] propertyTarget) {
        if (propertyTarget == null || propertyTarget.length == 0) {
            throw new IllegalArgumentException("propertyTarget is null or empty");
        }

        for (String propertyName : propertyTarget) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            checkColumn(cd, propertyDescriptor);
        }
    }

    private List<Pair<ComponentDescriptor.PropertyDescriptor, String>> prepareSourceColumns(ComponentDescriptor<?> cd, String[] propertySource) throws IllegalArgumentException {
        if (propertySource == null || propertySource.length == 0) {
            throw new IllegalArgumentException("propertySource is null or empty");
        }

        List<Pair<ComponentDescriptor.PropertyDescriptor, String>> columnNames = new ArrayList<>();
        for (String propertyName : propertySource) {
            ComponentDescriptor.PropertyDescriptor propertyDescriptor = cd.getPropertyDescriptor(propertyName);
            checkColumn(cd, propertyDescriptor);

            columnNames.add(Pair.of(propertyDescriptor, propertyDescriptor.getMethod().getAnnotation(Column.class).name()));
        }
        return columnNames;
    }

    private void checkColumn(ComponentDescriptor<?> cd, ComponentDescriptor.PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor == null) {
            throw new IllegalArgumentException("Not exists property for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (!propertyDescriptor.getMethod().isAnnotationPresent(Column.class)) {
            throw new IllegalArgumentException("Not present annotation Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
        if (StringUtils.isBlank(propertyDescriptor.getMethod().getAnnotation(Column.class).name())) {
            throw new IllegalArgumentException("Not name in Column for Component=" + cd.getComponentClass() + " with property=" + propertyDescriptor.getPropertyName());
        }
    }

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
