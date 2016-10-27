package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.annotation.Collection;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.OrderBy;

import java.util.List;

@Entity(name = "T_TRAIN")
@ComponentBean
public interface ITrain2 extends IEntity {

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @Collection(propertyTarget = WagonFields.trainId, orderBy = @OrderBy(property = WagonFields.position))
    List<IWagon> getWagons();

    void setWagons(List<IWagon> wagons);
}
