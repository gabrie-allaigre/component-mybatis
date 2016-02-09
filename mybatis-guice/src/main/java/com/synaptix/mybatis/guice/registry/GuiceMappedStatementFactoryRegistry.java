package com.synaptix.mybatis.guice.registry;

import com.google.inject.Inject;
import com.synaptix.mybatis.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.session.registry.AbstractMappedStatementFactoryRegistry;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.util.Set;

public class GuiceMappedStatementFactoryRegistry extends AbstractMappedStatementFactoryRegistry {

    @Inject(optional = true)
    private Set<IMappedStatementFactory> mappedStatementFactories;

    @Override
    public MappedStatement createMappedStatement(Configuration configuration, String key) {
        for (IMappedStatementFactory mappedStatementFactory : mappedStatementFactories) {
            MappedStatement mappedStatement = mappedStatementFactory.createMappedStatement(configuration, key);
            if (mappedStatement != null) {
                return mappedStatement;
            }
        }
        return null;
    }
}
