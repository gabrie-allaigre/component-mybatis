package com.synaptix.mybatis.session;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.ComponentMappedStatementFactory;
import com.synaptix.mybatis.component.ComponentResultMapFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class SynaptixConfiguration extends Configuration {

    private static final Logger LOG = LogManager.getLogger(SynaptixConfiguration.class);

    private static final Pattern findEntityByIdPattern = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*/findEntityById");

    private static final Pattern findChildrenByIdParentPattern = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*/findChildrenByIdParent\\?[a-zA-Z_$][a-zA-Z\\d_$]*");

    protected ComponentMappedStatementFactory componentMappedStatementFactory = new ComponentMappedStatementFactory(this);

    protected ComponentResultMapFactory componentResultMapFactory = new ComponentResultMapFactory(this);

    public ComponentMappedStatementFactory getComponentMappedStatementFactory() {
        return componentMappedStatementFactory;
    }

    public void setComponentMappedStatementFactory(ComponentMappedStatementFactory componentMappedStatementFactory) {
        this.componentMappedStatementFactory = componentMappedStatementFactory;
    }

    public ComponentResultMapFactory getComponentResultMapFactory() {
        return componentResultMapFactory;
    }

    public void setComponentResultMapFactory(ComponentResultMapFactory componentResultMapFactory) {
        this.componentResultMapFactory = componentResultMapFactory;
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

    private boolean verifyAndCreateComponentResultMap(String id) {
        boolean res = false;
        Class<? extends IComponent> componentClass = getComponentClass(id);
        if (componentClass != null) {
            ResultMap resultMap = componentResultMapFactory.createComponentResultMap(componentClass);
            res = resultMap != null;
        }
        return res;
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

    private boolean verifyAndCreateComponentMappedStatement(String id) {
        boolean res = false;
        if (id != null) {
            if (findEntityByIdPattern.matcher(id).matches()) {
                String componentName = id.substring(0, id.indexOf("/"));
                Class<? extends IComponent> componentClass = getComponentClass(componentName);
                if (componentClass != null) {
                    MappedStatement mappedStatement = componentMappedStatementFactory.createComponentFindEntityByIdMappedStatement(componentClass);
                    res = mappedStatement != null;
                }
            } else if (findChildrenByIdParentPattern.matcher(id).matches()) {
                String componentName = id.substring(0, id.indexOf("/"));
                Class<? extends IComponent> componentClass = getComponentClass(componentName);
                String idParentPropertyName = id.substring(id.lastIndexOf("?") + 1);
                if (componentClass != null && idParentPropertyName != null) {
                    MappedStatement mappedStatement = componentMappedStatementFactory.createComponentFindChildrenByIdParentMappedStatement(componentClass, idParentPropertyName);
                    res = mappedStatement != null;
                }
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends IComponent> getComponentClass(String componentName) {
        Class<? extends IComponent> res = null;
        if (componentName != null) {
            try {
                Class<?> clazz = this.getClass().getClassLoader().loadClass(componentName);
                if (ComponentFactory.getInstance().isComponentType(clazz)) {
                    res = (Class<? extends IComponent>) clazz;
                }
            } catch (ClassNotFoundException e) {
                LOG.error("Not found Component Class for " + componentName, e);
            }
        }
        return res;
    }
}
