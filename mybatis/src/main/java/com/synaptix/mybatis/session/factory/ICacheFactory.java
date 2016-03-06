package com.synaptix.mybatis.session.factory;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.session.Configuration;

public interface ICacheFactory {

    Cache createCache(Configuration configuration, String key);

}
