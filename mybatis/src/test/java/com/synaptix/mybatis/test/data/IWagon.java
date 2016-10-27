package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@Entity(name = "T_WAGON")
@ComponentBean
public interface IWagon extends IEntity {

    @Column(name = "TRAIN_ID")
    IId getTrainId();

    void setTrainId(IId trainId);

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @Column(name = "POSITION")
    Integer getPosition();

    void setPosition(Integer position);

}
