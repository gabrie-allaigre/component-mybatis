package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.factory.IMappedStatementFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MappedStatementFactoryRegistryBuilder {

    private final MyMappedStatementFactoryRegistry mappedStatementFactoryRegistry;

    private MappedStatementFactoryRegistryBuilder() {
        super();

        this.mappedStatementFactoryRegistry = new MyMappedStatementFactoryRegistry();
    }

    public static MappedStatementFactoryRegistryBuilder newBuilder() {
        return new MappedStatementFactoryRegistryBuilder();
    }

    public MappedStatementFactoryRegistryBuilder addMappedStatementFactory(IMappedStatementFactory mappedStatementFactory) {
        mappedStatementFactoryRegistry.mappedStatementFactories.add(mappedStatementFactory);
        return this;
    }

    public IMappedStatementFactoryRegistry build() {
        return mappedStatementFactoryRegistry;
    }

    private static class MyMappedStatementFactoryRegistry extends AbstractMappedStatementFactoryRegistry {

        private List<IMappedStatementFactory> mappedStatementFactories;

        public MyMappedStatementFactoryRegistry() {
            super();

            this.mappedStatementFactories = new ArrayList<>();
        }

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
}
