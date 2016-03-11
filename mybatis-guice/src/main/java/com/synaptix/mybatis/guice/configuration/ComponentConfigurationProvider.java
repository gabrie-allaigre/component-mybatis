package com.synaptix.mybatis.guice.configuration;

import com.google.inject.Inject;
import com.synaptix.mybatis.component.factory.ComponentProxyFactory;
import com.synaptix.mybatis.component.session.ComponentConfiguration;
import com.synaptix.mybatis.component.session.factory.ICacheFactory;
import com.synaptix.mybatis.component.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.component.session.factory.IResultMapFactory;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.mybatis.guice.configuration.ConfigurationProvider;

import java.util.Set;

public class ComponentConfigurationProvider extends ConfigurationProvider {

    @Inject(optional = true)
    private Set<IMappedStatementFactory> mappedStatementFactories;

    @Inject(optional = true)
    private Set<IResultMapFactory> resultMapFactories;

    @Inject(optional = true)
    private Set<ICacheFactory> cacheFactories;

    @Inject(optional = true)
    private INlsColumnHandler nlsColumnHandler;

    @Inject(optional = true)
    private ProxyFactory proxyFactory = new ComponentProxyFactory();

    @Inject
    public ComponentConfigurationProvider(Environment environment) {
        super(environment);
    }

    @Override
    protected Configuration newConfiguration(Environment environment) {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(environment);
        componentConfiguration.setNlsColumnHandler(nlsColumnHandler);
        componentConfiguration.setProxyFactory(proxyFactory);

        if (mappedStatementFactories != null && !mappedStatementFactories.isEmpty()) {
            for (IMappedStatementFactory mappedStatementFactory : mappedStatementFactories) {
                componentConfiguration.getMappedStatementFactoryRegistry().registry(mappedStatementFactory);
            }
        }

        if (resultMapFactories != null && !resultMapFactories.isEmpty()) {
            for (IResultMapFactory resultMapFactory : resultMapFactories) {
                componentConfiguration.getResultMapFactoryRegistry().registry(resultMapFactory);
            }
        }

        if (cacheFactories != null && !cacheFactories.isEmpty()) {
            for (ICacheFactory cacheFactory : cacheFactories) {
                componentConfiguration.getCacheFactoryRegistry().registry(cacheFactory);
            }
        }

        return componentConfiguration;
    }
}
