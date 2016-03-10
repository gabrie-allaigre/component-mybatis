package com.synaptix.mybatis.session.handler;

import com.synaptix.entity.ITracable;

public interface ITracableHandler {

    <E extends ITracable> void fillInsertTracable(E tracable);

}
