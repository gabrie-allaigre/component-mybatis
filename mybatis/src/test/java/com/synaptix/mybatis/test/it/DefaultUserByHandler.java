package com.synaptix.mybatis.test.it;

import com.synaptix.mybatis.simple.observer.TracableTriggerObserver;

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
