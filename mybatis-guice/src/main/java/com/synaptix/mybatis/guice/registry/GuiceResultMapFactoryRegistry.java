package com.synaptix.mybatis.guice.registry;

import com.google.inject.Inject;
import com.synaptix.mybatis.session.factory.IResultMapFactory;
import com.synaptix.mybatis.session.registry.AbstractResultMapFactoryRegistry;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

import java.util.Set;

public class GuiceResultMapFactoryRegistry extends AbstractResultMapFactoryRegistry {

    @Inject(optional = true)
    private Set<IResultMapFactory> resultMapFactories;

    @Override
    public ResultMap createResultMap(Configuration configuration, String key) {
        for (IResultMapFactory resultMapFactory : resultMapFactories) {
            ResultMap resultMap = resultMapFactory.createResultMap(configuration, key);
            if (resultMap != null) {
                return resultMap;
            }
        }
        return null;
    }
}
