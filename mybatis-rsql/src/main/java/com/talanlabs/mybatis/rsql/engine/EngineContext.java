package com.talanlabs.mybatis.rsql.engine;

import com.talanlabs.mybatis.component.statement.StatementNameHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EngineContext {

    private String defaultTableName = "t";
    private String defaultJoinPrefix = "j";
    private String defaultParamPrefix = "";

    private AtomicInteger paramInteger = new AtomicInteger();
    private AtomicInteger joinInteger = new AtomicInteger();
    private Map<String, String> joinMap = new HashMap<>();

    private EngineContext() {
        super();
    }

    public static EngineContextBuilder newBulder() {
        return new EngineContextBuilder();
    }

    public String getDefaultTableName() {
        return defaultTableName;
    }

    public String getDefaultJoinPrefix() {
        return defaultJoinPrefix;
    }

    public String getDefaultParamPrefix() {
        return defaultParamPrefix;
    }

    /**
     * Get a new param name
     *
     * @return a new param name
     */
    public String getNewParamName() {
        return getDefaultParamPrefix() + StatementNameHelper.buildParam(paramInteger.getAndIncrement());
    }

    /**
     * Get a join name
     *
     * @param joinPropertyName property name with join person.address
     * @return a join name if exists
     */
    public String getJoinName(String joinPropertyName) {
        return joinMap.get(joinPropertyName);
    }

    /**
     * Get a new join name
     *
     * @param joinPropertyName property name with join person.address
     * @return a new join name
     */
    public String getNewJoinName(String joinPropertyName) {
        String name = getDefaultJoinPrefix() + joinInteger.getAndIncrement();
        joinMap.put(joinPropertyName, name);
        return name;
    }

    public static class EngineContextBuilder {

        EngineContext engineContext = new EngineContext();

        public EngineContextBuilder defaultTablePrefix(String defaultTablePrefix) {
            engineContext.defaultTableName = defaultTablePrefix;
            return this;
        }

        public EngineContextBuilder defaultJoinPrefix(String defaultJoinPrefix) {
            engineContext.defaultJoinPrefix = defaultJoinPrefix;
            return this;
        }

        public EngineContextBuilder defaultParamPrefix(String defaultParamPrefix) {
            engineContext.defaultParamPrefix = defaultParamPrefix;
            return this;
        }

        public EngineContext build() {
            return engineContext;
        }
    }
}
