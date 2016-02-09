package com.synaptix.mybatis.session.factory;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

public interface IMappedStatementFactory {

    MappedStatement createMappedStatement(Configuration configuration, String key);

}
