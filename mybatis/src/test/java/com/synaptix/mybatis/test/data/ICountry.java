package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@Entity(name = "T_COUNTRY")
@ComponentBean
public interface ICountry extends IEntity, ITracable, ICancellable {

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @Column(name = "NAME")
    String getName();

    void setName(String name);

}
