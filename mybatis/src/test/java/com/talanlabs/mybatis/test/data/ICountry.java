package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.ICancelable;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.ITracable;
import com.talanlabs.entity.annotation.Cache;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.NlsColumn;

@Entity(name = "T_COUNTRY")
@ComponentBean
@Cache(links = IUser.class)
public interface ICountry extends IEntity, ITracable, ICancelable {

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @NlsColumn(name = "NAME")
    String getName();

    void setName(String name);

}
