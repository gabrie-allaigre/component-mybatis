package com.talanlabs.mybatis.guice.configuration;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.talanlabs.mybatis.component.session.factory.ITypeHandlerFactory;
import org.apache.ibatis.type.TypeHandler;

@Singleton
public class GuiceTypeHandlerFactory implements ITypeHandlerFactory {

    @Inject
    private Injector injector;

    @Override
    public <F extends TypeHandler<?>> F create(Class<F> typeHandlerClass) {
        return injector.getInstance(typeHandlerClass);
    }
}
