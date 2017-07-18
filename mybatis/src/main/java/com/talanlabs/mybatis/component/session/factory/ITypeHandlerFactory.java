package com.talanlabs.mybatis.component.session.factory;

import org.apache.ibatis.type.TypeHandler;

public interface ITypeHandlerFactory {

    /**
     * Create type handler
     *
     * @param typeHandlerClass class
     * @return instance of type handler
     */
    <F extends TypeHandler<?>> F create(Class<F> typeHandlerClass);

}
