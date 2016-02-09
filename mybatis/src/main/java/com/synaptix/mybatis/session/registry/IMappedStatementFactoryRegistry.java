package com.synaptix.mybatis.session.registry;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

public interface IMappedStatementFactoryRegistry {

    /**
     * Create a mapped statement
     *
     * @param configuration configuration
     * @param key           name of mapped statement
     * @return mapped statement or null if not found factory
     */
    MappedStatement createMappedStatement(Configuration configuration, String key);

}
