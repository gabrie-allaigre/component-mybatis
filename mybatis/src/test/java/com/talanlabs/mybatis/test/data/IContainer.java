package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.IId;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;


@Entity(name = "T_CONTAINER")
@ComponentBean
public interface IContainer extends IComponent {

    @Column(name = "WAGON_ID")
    IId getWagonId();

    void setWagonId(IId wagonId);

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

}
