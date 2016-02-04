package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.annotation.Collection;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;

import java.util.List;

@Entity(name = "T_TRAIN")
@ComponentBean
public interface ITrain extends IEntity {

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @Collection(propertyTarget = WagonFields.trainId)
    List<IWagon> getWagons();

    void setWagons(List<IWagon> wagons);
}
