package com.synaptix.mybatis.component.session.handler;

import com.synaptix.component.IComponent;

import java.util.Map;

public interface INlsColumnHandler {

    /**
     * Get a current context
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
     * Build request sql
     *
     * @param componentClass       component class
     * @param propertyName         property name
     * @param parameterMap         parameter map
     * @param additionalParameters additional parameters
     * @return request sql
     */
    String buildFindNlsColumn(Class<? extends IComponent> componentClass, String propertyName, Map<String, Object> parameterMap, Map<String, Object> additionalParameters);

}
