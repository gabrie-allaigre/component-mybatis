package com.synaptix.mybatis.guice;

import com.google.inject.Inject;
import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.session.ComponentConfiguration;
import com.synaptix.mybatis.session.INlsColumnHandler;
import com.synaptix.mybatis.session.registry.ICacheFactoryRegistry;
import com.synaptix.mybatis.session.registry.IMappedStatementFactoryRegistry;
import com.synaptix.mybatis.session.registry.IResultMapFactoryRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.mybatis.guice.configuration.ConfigurationProvider;

public class ComponentConfigurationProvider extends ConfigurationProvider {

    @Inject(optional = true)
    private IMappedStatementFactoryRegistry mappedStatementFactoryRegistry;

    @Inject(optional = true)
    private IResultMapFactoryRegistry resultMapFactoryRegistry;

    @Inject(optional = true)
    private ICacheFactoryRegistry cacheFactoryRegistry;

    @Inject(optional = true)
    private INlsColumnHandler nlsColumnHandler;

    @Inject
    public ComponentConfigurationProvider(Environment environment) {
        super(environment);
    }

    @Override
    protected Configuration newConfiguration(Environment environment) {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(environment);
        componentConfiguration.setMappedStatementFactoryRegistry(mappedStatementFactoryRegistry);
        componentConfiguration.setResultMapFactoryRegistry(resultMapFactoryRegistry);
        componentConfiguration.setCacheFactoryRegistry(cacheFactoryRegistry);
        componentConfiguration.setNlsColumnHandler(nlsColumnHandler);
        componentConfiguration.setProxyFactory(new ComponentProxyFactory());
        return componentConfiguration;
    }
}
