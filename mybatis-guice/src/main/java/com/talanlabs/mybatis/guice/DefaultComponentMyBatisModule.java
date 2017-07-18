package com.talanlabs.mybatis.guice;

import com.talanlabs.mybatis.component.cache.ComponentCacheFactory;
import com.talanlabs.mybatis.component.resultmap.ComponentResultMapFactory;
import com.talanlabs.mybatis.component.statement.*;
import com.talanlabs.mybatis.guice.configuration.GuiceTypeHandlerFactory;
import com.talanlabs.mybatis.simple.handler.IdTypeHandler;

public final class DefaultComponentMyBatisModule extends AbstractComponentMyBatisModule {

    @Override
    protected void initialize() {
        addTypeHandlerClass(IdTypeHandler.class);

        addResultMapFactoryClass(ComponentResultMapFactory.class);

        addMappedStatementFactoryClass(FindEntityByIdMappedStatementFactory.class);
        addMappedStatementFactoryClass(FindComponentsByMappedStatementFactory.class);
        addMappedStatementFactoryClass(FindComponentsByJoinTableMappedStatementFactory.class);
        addMappedStatementFactoryClass(FindNlsColumnMappedStatementFactory.class);
        addMappedStatementFactoryClass(InsertMappedStatementFactory.class);
        addMappedStatementFactoryClass(UpdateMappedStatementFactory.class);
        addMappedStatementFactoryClass(DeleteMappedStatementFactory.class);
        addMappedStatementFactoryClass(DeleteEntityByIdMappedStatementFactory.class);
        addMappedStatementFactoryClass(DeleteComponentsByMappedStatementFactory.class);

        addCacheFactoryClass(ComponentCacheFactory.class);

        setTypeHandlerFactory(GuiceTypeHandlerFactory.class);
    }
}
