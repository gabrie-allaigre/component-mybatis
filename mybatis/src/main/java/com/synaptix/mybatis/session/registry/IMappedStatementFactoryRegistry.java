package com.synaptix.mybatis.session.registry;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.mapping.MappedStatement;

public interface IMappedStatementFactoryRegistry {

    /**
     * Create a mapped statement
     *
     * @param componentConfiguration configuration
     * @param key           name of mapped statement
     * @return mapped statement or null if not found factory
     */
    MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key);

}
