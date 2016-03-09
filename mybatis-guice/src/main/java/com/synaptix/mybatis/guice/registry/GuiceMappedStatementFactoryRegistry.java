package com.synaptix.mybatis.guice.registry;

import com.google.inject.Inject;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.session.registry.AbstractMappedStatementFactoryRegistry;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Set;

public class GuiceMappedStatementFactoryRegistry extends AbstractMappedStatementFactoryRegistry {

    @Inject(optional = true)
    private Set<IMappedStatementFactory> mappedStatementFactories;

    @Override
    public MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key) {
        for (IMappedStatementFactory mappedStatementFactory : mappedStatementFactories) {
            MappedStatement mappedStatement = mappedStatementFactory.createMappedStatement(componentConfiguration, key);
            if (mappedStatement != null) {
                return mappedStatement;
            }
        }
        return null;
    }
}
