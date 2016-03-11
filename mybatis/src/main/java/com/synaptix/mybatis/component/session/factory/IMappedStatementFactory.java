package com.synaptix.mybatis.component.session.factory;

import com.synaptix.mybatis.component.session.ComponentConfiguration;
import org.apache.ibatis.mapping.MappedStatement;

public interface IMappedStatementFactory {

    /**
     * Accept key
     *
     * @param key key at verify
     * @return true or false
     */
    boolean acceptKey(String key);

    /**
     * Create Mapped statement
     *
     * @param componentConfiguration configuration
     * @param key                    a valid key
     * @return Mapped statement
     */
    MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key);

}
