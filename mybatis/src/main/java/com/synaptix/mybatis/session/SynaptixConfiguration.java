package com.synaptix.mybatis.session;

import com.synaptix.mybatis.session.registry.IMappedStatementFactoryRegistry;
import com.synaptix.mybatis.session.registry.IResultMapFactoryRegistry;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SynaptixConfiguration extends Configuration {

    private static final Logger LOG = LogManager.getLogger(SynaptixConfiguration.class);

    protected IMappedStatementFactoryRegistry mappedStatementFactoryRegistry = null;

    protected IResultMapFactoryRegistry resultMapFactoryRegistry = null;

    public SynaptixConfiguration() {
        super();
    }

    public SynaptixConfiguration(Environment environment) {
        super(environment);
    }

    public IMappedStatementFactoryRegistry getMappedStatementFactoryRegistry() {
        return mappedStatementFactoryRegistry;
    }

    public void setMappedStatementFactoryRegistry(IMappedStatementFactoryRegistry mappedStatementFactoryRegistry) {
        this.mappedStatementFactoryRegistry = mappedStatementFactoryRegistry;
    }

    public IResultMapFactoryRegistry getResultMapFactoryRegistry() {
        return resultMapFactoryRegistry;
    }

    public void setResultMapFactoryRegistry(IResultMapFactoryRegistry resultMapFactoryRegistry) {
        this.resultMapFactoryRegistry = resultMapFactoryRegistry;
    }

    @Override
    public boolean hasResultMap(String id) {
        boolean res = super.hasResultMap(id);
        if (!res) {
            res = verifyAndCreateComponentResultMap(id);
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

    private synchronized boolean verifyAndCreateComponentResultMap(String id) {
        if (resultMapFactoryRegistry != null) {
            ResultMap resultMap = resultMapFactoryRegistry.createResultMap(this, id);
            if (resultMap != null) {
                addResultMap(resultMap);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
        boolean res = super.hasStatement(statementName, validateIncompleteStatements);
        if (!res) {
            res = verifyAndCreateComponentMappedStatement(statementName);
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

    private synchronized boolean verifyAndCreateComponentMappedStatement(String id) {
        if (mappedStatementFactoryRegistry != null) {
            MappedStatement mappedStatement = mappedStatementFactoryRegistry.createMappedStatement(this, id);
            if (mappedStatement != null) {
                addMappedStatement(mappedStatement);
                return true;
            }
        }
        return false;
    }
}
