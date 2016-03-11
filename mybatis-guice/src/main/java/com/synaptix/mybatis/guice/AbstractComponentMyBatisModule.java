package com.synaptix.mybatis.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;
import com.synaptix.mybatis.component.session.factory.ICacheFactory;
import com.synaptix.mybatis.component.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.component.session.factory.IResultMapFactory;
import com.synaptix.mybatis.component.session.observer.ITriggerObserver;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.guice.configuration.Mappers;
import org.mybatis.guice.configuration.MappingTypeHandlers;
import org.mybatis.guice.mappers.MapperProvider;
import org.mybatis.guice.type.TypeHandlerProvider;

import static com.google.inject.util.Providers.guicify;

public abstract class AbstractComponentMyBatisModule extends AbstractModule {

    private Multibinder<TypeHandler<?>> mappingTypeHandlers;
    private Multibinder<IResultMapFactory> resultMapFactoryMultibinder;
    private Multibinder<IMappedStatementFactory> mappedStatementFactoryMultibinder;
    private Multibinder<ICacheFactory> cacheFactoryMultibinder;
    private Multibinder<ITriggerObserver> triggerObserverMultibinder;
    private Multibinder<Class<?>> mappers;

    @Override
    protected void configure() {
        this.mappingTypeHandlers = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeHandler<?>>() {
        }, MappingTypeHandlers.class);
        this.resultMapFactoryMultibinder = Multibinder.newSetBinder(binder(), IResultMapFactory.class);
        this.mappedStatementFactoryMultibinder = Multibinder.newSetBinder(binder(), IMappedStatementFactory.class);
        this.cacheFactoryMultibinder = Multibinder.newSetBinder(binder(), ICacheFactory.class);
        this.triggerObserverMultibinder = Multibinder.newSetBinder(binder(), ITriggerObserver.class);
        this.mappers = Multibinder.newSetBinder(this.binder(), new TypeLiteral<Class<?>>() {
        }, Mappers.class);

        initialize();
    }

    protected abstract void initialize();

    protected final void addTypeHandlerClass(Class<? extends TypeHandler<?>> handlerClass) {
        this.mappingTypeHandlers.addBinding().to(handlerClass);
        this.bindTypeHandler(handlerClass, null);
    }

    protected final void addResultMapFactoryClass(Class<? extends IResultMapFactory> resultMapFactoryClass) {
        this.resultMapFactoryMultibinder.addBinding().to(resultMapFactoryClass);
    }

    protected final void addMappedStatementFactoryClass(Class<? extends IMappedStatementFactory> mappedStatementFactoryClass) {
        this.mappedStatementFactoryMultibinder.addBinding().to(mappedStatementFactoryClass);
    }

    protected final void addCacheFactoryClass(Class<? extends ICacheFactory> cacheFactoryClass) {
        this.cacheFactoryMultibinder.addBinding().to(cacheFactoryClass);
    }

    protected final void addTriggerObserverClass(Class<? extends ITriggerObserver> triggerObserverClass) {
        this.triggerObserverMultibinder.addBinding().to(triggerObserverClass);
    }

    protected final void addMapperClass(Class<?> mapperClass) {
        this.mappers.addBinding().toInstance(mapperClass);
        this.bindMapper(mapperClass);
    }

    final <TH extends TypeHandler<? extends T>, T> void bindTypeHandler(Class<TH> typeHandlerType, Class<T> type) {
        bind(typeHandlerType).toProvider(guicify(new TypeHandlerProvider<>(typeHandlerType, type))).in(Scopes.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    final <T> void bindMapper(Class<T> mapperType) {
        this.bind(mapperType).toProvider(Providers.guicify(new MapperProvider(mapperType))).in(Scopes.SINGLETON);
    }
}
