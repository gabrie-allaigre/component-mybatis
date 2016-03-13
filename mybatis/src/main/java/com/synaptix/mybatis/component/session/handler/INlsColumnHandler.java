package com.synaptix.mybatis.component.session.handler;

import com.synaptix.component.IComponent;

import java.util.Map;

public interface INlsColumnHandler {

    /**
     * Get a current context, used for cache
     *
     * @return context
     */
    Object getContext();

    /**
     * Get additional parameter
     *
     * @param componentClass component class
     * @param propertyName   property name
     * @return map or null
     */
    Map<String, Object> getAdditionalParameter(Class<? extends IComponent> componentClass, String propertyName);

    /**
     * Select id for nls column, default parameter
     *
     * @param componentClass component class
     * @param propertyName   property name
     * @return select id
     */
    String getSelectNlsColumnId(Class<? extends IComponent> componentClass, String propertyName);

    /**
     * Merge id for nls column
     *
     * @param componentClass component class
     * @param propertyName   property name
     * @return select id
     */
    String getMergeNlsColumnId(Class<? extends IComponent> componentClass, String propertyName);

    /**
     * Delete id for nls column
     *
     * @param componentClass component class
     * @param propertyName   property name
     * @return select id
     */
    String getDeleteNlsColumnId(Class<? extends IComponent> componentClass, String propertyName);

    /**
     * Update original nls column
     *
     * @param componentClass component class
     * @param propertyName   property name
     * @return
     */
    boolean isUpdateDefaultNlsColumn(Class<? extends IComponent> componentClass, String propertyName);
}
