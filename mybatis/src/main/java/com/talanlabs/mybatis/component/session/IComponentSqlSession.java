package com.talanlabs.mybatis.component.session;

import com.talanlabs.component.IComponent;

public interface IComponentSqlSession {

    /**
     * Find component by id
     *
     * @param componentClass component class
     * @param id             identifier unique
     * @return component
     */
    <E extends IComponent> E findById(Class<E> componentClass, Object id);

    /**
     * Insert component, generate id and set version to 0, set Tracable
     *
     * @param component component to insert
     * @return 0 or 1 if insert
     */
    <E extends IComponent> int insert(E component);

    /**
     * Update component, update version +1, set Tracable
     *
     * @param component component to update
     * @return 0 or 1 if insert
     */
    <E extends IComponent> int update(E component);

    /**
     * Delete component
     *
     * @param component component to delete
     * @return 0 or 1 if delete
     */
    <E extends IComponent> int delete(E component);

}
