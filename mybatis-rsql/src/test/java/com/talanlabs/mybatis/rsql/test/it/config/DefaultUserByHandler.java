package com.talanlabs.mybatis.rsql.test.it.config;

import com.talanlabs.mybatis.simple.observer.TracableTriggerObserver;

public class DefaultUserByHandler implements TracableTriggerObserver.IUserByHandler {

    private String userBy;

    public DefaultUserByHandler() {
        super();

        this.userBy = "unknow";
    }

    @Override
    public String getUserBy() {
        return userBy;
    }

    public void setUserBy(String userBy) {
        this.userBy = userBy;
    }
}
