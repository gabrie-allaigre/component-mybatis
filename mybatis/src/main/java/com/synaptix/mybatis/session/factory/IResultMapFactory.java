package com.synaptix.mybatis.session.factory;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.mapping.ResultMap;

public interface IResultMapFactory {

    ResultMap createResultMap(ComponentConfiguration componentConfiguration, String key);

}
