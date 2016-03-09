package com.synaptix.mybatis.session.factory;

import com.synaptix.mybatis.session.ComponentConfiguration;
import org.apache.ibatis.mapping.MappedStatement;

public interface IMappedStatementFactory {

    MappedStatement createMappedStatement(ComponentConfiguration componentConfiguration, String key);

}
