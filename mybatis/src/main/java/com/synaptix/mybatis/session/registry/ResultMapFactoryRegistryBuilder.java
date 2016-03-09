package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.IResultMapFactory;
import org.apache.ibatis.mapping.ResultMap;

import java.util.ArrayList;
import java.util.List;

public class ResultMapFactoryRegistryBuilder {

    private final MyResultMapFactoryRegistry resultMapFactoryRegistry;

    private ResultMapFactoryRegistryBuilder() {
        super();

        this.resultMapFactoryRegistry = new MyResultMapFactoryRegistry();
    }

    public static ResultMapFactoryRegistryBuilder newBuilder() {
        return new ResultMapFactoryRegistryBuilder();
    }

    public ResultMapFactoryRegistryBuilder addResultMapFactory(IResultMapFactory resultMapFactory) {
        resultMapFactoryRegistry.resultMapFactories.add(resultMapFactory);
        return this;
    }

    public IResultMapFactoryRegistry build() {
        return resultMapFactoryRegistry;
    }

    private static class MyResultMapFactoryRegistry extends AbstractResultMapFactoryRegistry {

        private List<IResultMapFactory> resultMapFactories;

        public MyResultMapFactoryRegistry() {
            super();

            this.resultMapFactories = new ArrayList<>();
        }

        @Override
        public ResultMap createResultMap(ComponentConfiguration componentConfiguration, String key) {
            for (IResultMapFactory resultMapFactory : resultMapFactories) {
                ResultMap resultMap = resultMapFactory.createResultMap(componentConfiguration, key);
                if (resultMap != null) {
                    return resultMap;
                }
            }
            return null;
        }
    }
}
