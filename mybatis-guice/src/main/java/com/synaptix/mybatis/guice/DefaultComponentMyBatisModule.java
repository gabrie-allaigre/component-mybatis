package com.synaptix.mybatis.guice;

import com.synaptix.mybatis.component.cache.ComponentCacheFactory;
import com.synaptix.mybatis.component.observer.NlsColumnTriggerObserver;
import com.synaptix.mybatis.component.resultmap.ComponentResultMapFactory;
import com.synaptix.mybatis.component.statement.*;
import com.synaptix.mybatis.simple.handler.IdTypeHandler;

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

        addCacheFactoryClass(ComponentCacheFactory.class);

        addTriggerObserverClass(NlsColumnTriggerObserver.class);
    }
}
