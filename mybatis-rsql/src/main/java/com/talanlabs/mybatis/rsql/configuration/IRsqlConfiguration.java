package com.talanlabs.mybatis.rsql.configuration;

import com.talanlabs.component.IComponent;
import com.talanlabs.mybatis.rsql.engine.EngineContext;
import com.talanlabs.mybatis.rsql.engine.INlsColumnRsqlHandler;
import com.talanlabs.mybatis.rsql.engine.IStringPolicy;
import com.talanlabs.mybatis.rsql.engine.orderby.ComponentSortVisitor;
import com.talanlabs.mybatis.rsql.engine.where.ComponentRsqlVisitor;
import com.talanlabs.mybatis.rsql.sort.SortParser;
import com.talanlabs.mybatis.rsql.statement.IPageStatementFactory;
import com.talanlabs.rtext.Rtext;
import cz.jirutka.rsql.parser.RSQLParser;

public interface IRsqlConfiguration {

    /**
     * @return rtext instance
     */
    Rtext getRtext();

    /**
     * @return nls column rsql handler
     */
    INlsColumnRsqlHandler getNlsColumnRsqlHandler();

    /**
     * @param componentClass component class
     * @return Return a component visitor for component class
     */
    <E extends IComponent> ComponentRsqlVisitor<E> getComponentRsqlVisitor(Class<E> componentClass);

    /**
     * @param componentClass component class
     * @return Return a component visitor for component class
     */
    <E extends IComponent> ComponentSortVisitor<E> getComponentSortVisitor(Class<E> componentClass);

    /**
     * @return a RSQL Parser
     */
    RSQLParser getRsqlParser();

    /**
     * @return a Sort Parser
     */
    SortParser getSortParser();

    /**
     * @return create EngineContext
     */
    EngineContext newEngineContext();

    /**
     * @return a String compare policy
     */
    IStringPolicy getStringPolicy();

    /**
     * @return A like symbol default %
     */
    String getLikeSymbol();

    /**
     * @return A Page create page statement
     */
    IPageStatementFactory getPageStatementFactory();

}
