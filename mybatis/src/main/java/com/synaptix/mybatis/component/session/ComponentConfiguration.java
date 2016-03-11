package com.synaptix.mybatis.component.session;

import com.synaptix.mybatis.component.session.dispatcher.TriggerDispatcher;
import com.synaptix.mybatis.component.session.factory.ICacheFactory;
import com.synaptix.mybatis.component.session.factory.IMappedStatementFactory;
import com.synaptix.mybatis.component.session.factory.IResultMapFactory;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;
import com.synaptix.mybatis.component.session.registry.CacheFactoryRegistry;
import com.synaptix.mybatis.component.session.registry.MappedStatementFactoryRegistry;
import com.synaptix.mybatis.component.session.registry.ResultMapFactoryRegistry;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

public class ComponentConfiguration extends Configuration {

    protected MappedStatementFactoryRegistry mappedStatementFactoryRegistry = new MappedStatementFactoryRegistry();

    protected ResultMapFactoryRegistry resultMapFactoryRegistry = new ResultMapFactoryRegistry();

    protected CacheFactoryRegistry cacheFactoryRegistry = new CacheFactoryRegistry();

    protected INlsColumnHandler nlsColumnHandler = null;

    protected TriggerDispatcher triggerDispatcher = new TriggerDispatcher();

    public ComponentConfiguration() {
        super();
    }

    public ComponentConfiguration(Environment environment) {
        super(environment);
    }

    /**
     * @return get mapped statement factory registry, default null
     */
    public MappedStatementFactoryRegistry getMappedStatementFactoryRegistry() {
        return mappedStatementFactoryRegistry;
    }

    /**
     * Set mapped statement factory registry
     */
    public void setMappedStatementFactoryRegistry(MappedStatementFactoryRegistry mappedStatementFactoryRegistry) {
        this.mappedStatementFactoryRegistry = mappedStatementFactoryRegistry;
    }

    /**
     * @return get result map factory registry default null
     */
    public ResultMapFactoryRegistry getResultMapFactoryRegistry() {
        return resultMapFactoryRegistry;
    }

    /**
     * @param resultMapFactoryRegistry result map factory registry
     */
    public void setResultMapFactoryRegistry(ResultMapFactoryRegistry resultMapFactoryRegistry) {
        this.resultMapFactoryRegistry = resultMapFactoryRegistry;
    }

    /**
     * @return get cache factory registry
     */
    public CacheFactoryRegistry getCacheFactoryRegistry() {
        return cacheFactoryRegistry;
    }

    /**
     * @param cacheFactoryRegistry cache factory registry
     */
    public void setCacheFactoryRegistry(CacheFactoryRegistry cacheFactoryRegistry) {
        this.cacheFactoryRegistry = cacheFactoryRegistry;
    }

    /**
     * @return get nls column handler
     */
    public INlsColumnHandler getNlsColumnHandler() {
        return this.nlsColumnHandler;
    }

    /**
     * @param nlsColumnHandler nls column handler
     */
    public void setNlsColumnHandler(INlsColumnHandler nlsColumnHandler) {
        this.nlsColumnHandler = nlsColumnHandler;
    }

    /**
     * @return get trigger dispatcher
     */
    public TriggerDispatcher getTriggerDispatcher() {
        return triggerDispatcher;
    }

    /**
     * @param triggerDispatcher trigger dispatcher
     */
    public void setTriggerDispatcher(TriggerDispatcher triggerDispatcher) {
        this.triggerDispatcher = triggerDispatcher;
    }

    @Override
    public boolean hasCache(String id) {
        boolean res = super.hasCache(id);
        if (!res) {
            res = verifyAndCreateCache(id);
        }
        return res;
    }

    @Override
    public Cache getCache(String id) {
        if (hasCache(id)) {
            return super.getCache(id);
        }
        return super.getCache(id);
    }

    private synchronized boolean verifyAndCreateCache(String id) {
        if (cacheFactoryRegistry != null) {
            ICacheFactory cacheFactory = cacheFactoryRegistry.getCacheFactory(id);
            if (cacheFactory != null) {
                Cache cache = cacheFactory.createCache(this, id);
                if (cache != null) {
                    addCache(cache);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasResultMap(String id) {
        boolean res = super.hasResultMap(id);
        if (!res) {
            res = verifyAndCreateResultMap(id);
        }
        return res;
    }

    @Override
    public ResultMap getResultMap(String id) {
        if (hasResultMap(id)) {
            return super.getResultMap(id);
        }
        return super.getResultMap(id);
    }

    private synchronized boolean verifyAndCreateResultMap(String id) {
        if (resultMapFactoryRegistry != null) {
            IResultMapFactory resultMapFactory = resultMapFactoryRegistry.getResultMapFactory(id);
            if (resultMapFactory != null) {
                ResultMap resultMap = resultMapFactory.createResultMap(this, id);
                if (resultMap != null) {
                    addResultMap(resultMap);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
        boolean res = super.hasStatement(statementName, validateIncompleteStatements);
        if (!res) {
            res = verifyAndCreateMappedStatement(statementName);
        }
        return res;
    }

    @Override
    public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
        if (hasStatement(id, validateIncompleteStatements)) {
            return super.getMappedStatement(id, validateIncompleteStatements);
        }
        return super.getMappedStatement(id, validateIncompleteStatements);
    }

    private synchronized boolean verifyAndCreateMappedStatement(String id) {
        if (mappedStatementFactoryRegistry != null) {
            IMappedStatementFactory mappedStatementFactory = mappedStatementFactoryRegistry.getMappedStatementFactory(id);
            if (mappedStatementFactory != null) {
                MappedStatement mappedStatement = mappedStatementFactory.createMappedStatement(this, id);
                if (mappedStatement != null) {
                    addMappedStatement(mappedStatement);
                    return true;
                }
            }
        }
        return false;
    }
}
