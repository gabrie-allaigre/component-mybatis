package com.synaptix.mybatis.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.mybatis.simple.handler.IdTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.guice.configuration.MappingTypeHandlers;
import org.mybatis.guice.type.TypeHandlerProvider;

import static com.google.inject.util.Providers.guicify;

public class SimpleMyBatisModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<TypeHandler<?>> mappingTypeHandlers = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeHandler<?>>() {
        }, MappingTypeHandlers.class);
        mappingTypeHandlers.addBinding().to(IdTypeHandler.class);
        bindTypeHandler(IdTypeHandler.class, null);
    }

    final <TH extends TypeHandler<? extends T>, T> void bindTypeHandler(Class<TH> typeHandlerType, Class<T> type) {
        bind(typeHandlerType).toProvider(guicify(new TypeHandlerProvider<>(typeHandlerType, type))).in(Scopes.SINGLETON);
    }
}
