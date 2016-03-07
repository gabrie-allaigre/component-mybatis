package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@Entity(name = "T_GROUP")
@ComponentBean
public interface IGroup extends IEntity, ITracable, ICancellable {

    @Column(name = "USER_ID")
    IId getUserId();

    void setUserId(IId userId);

    @Column(name = "NAME")
    String getName();

    void setName(String name);

}
