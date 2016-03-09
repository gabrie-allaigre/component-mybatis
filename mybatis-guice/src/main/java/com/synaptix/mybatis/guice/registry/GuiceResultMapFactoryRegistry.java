package com.synaptix.mybatis.guice.registry;

import com.google.inject.Inject;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.factory.IResultMapFactory;
import com.synaptix.mybatis.session.registry.AbstractResultMapFactoryRegistry;
import org.apache.ibatis.mapping.ResultMap;

import java.util.Set;

public class GuiceResultMapFactoryRegistry extends AbstractResultMapFactoryRegistry {

    @Inject(optional = true)
    private Set<IResultMapFactory> resultMapFactories;

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
