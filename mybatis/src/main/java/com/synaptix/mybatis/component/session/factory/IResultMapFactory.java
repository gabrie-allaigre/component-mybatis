package com.synaptix.mybatis.component.session.factory;

import com.synaptix.mybatis.component.session.ComponentConfiguration;
import org.apache.ibatis.mapping.ResultMap;

public interface IResultMapFactory {

    /**
     * Accept key
     *
     * @param key key at verify
     * @return true or false
     */
    boolean acceptKey(String key);

    /**
     * Create result map for key
     *
     * @param componentConfiguration configuration
     * @param key                    a valid key
     * @return ResultMap
     */
    ResultMap createResultMap(ComponentConfiguration componentConfiguration, String key);

}
