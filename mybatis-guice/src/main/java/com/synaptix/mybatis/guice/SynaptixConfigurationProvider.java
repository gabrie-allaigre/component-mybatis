package com.synaptix.mybatis.guice;

import com.google.inject.Inject;
import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.session.SynaptixConfiguration;
import com.synaptix.mybatis.session.registry.ICacheFactoryRegistry;
import com.synaptix.mybatis.session.registry.IMappedStatementFactoryRegistry;
import com.synaptix.mybatis.session.registry.IResultMapFactoryRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.mybatis.guice.configuration.ConfigurationProvider;

public class SynaptixConfigurationProvider extends ConfigurationProvider {

    @Inject(optional = true)
    private IMappedStatementFactoryRegistry mappedStatementFactoryRegistry;

    @Inject(optional = true)
    private IResultMapFactoryRegistry resultMapFactoryRegistry;

    @Inject(optional = true)
    private ICacheFactoryRegistry cacheFactoryRegistry;

    @Inject
    public SynaptixConfigurationProvider(Environment environment) {
        super(environment);
    }

    @Override
    protected Configuration newConfiguration(Environment environment) {
        SynaptixConfiguration synaptixConfiguration = new SynaptixConfiguration(environment);
        synaptixConfiguration.setMappedStatementFactoryRegistry(mappedStatementFactoryRegistry);
        synaptixConfiguration.setResultMapFactoryRegistry(resultMapFactoryRegistry);
        synaptixConfiguration.setCacheFactoryRegistry(cacheFactoryRegistry);
        synaptixConfiguration.setProxyFactory(new ComponentProxyFactory());
        return synaptixConfiguration;
    }
}
