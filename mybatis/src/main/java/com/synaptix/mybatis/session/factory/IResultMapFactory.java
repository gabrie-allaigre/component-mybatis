package com.synaptix.mybatis.session.factory;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

public interface IResultMapFactory {

    ResultMap createResultMap(Configuration configuration, String key);

}
