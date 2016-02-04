package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.annotation.Collection;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.OrderBy;

import java.util.List;

@Entity(name = "T_TRAIN")
@ComponentBean
public interface ITrain4 extends IEntity {

    @Column(name = "CODE")
    String getCode();

    void setCode(String code);

    @Collection(propertyTarget = WagonFields.trainId, orderBy = { @OrderBy(value = WagonFields.position, sort = OrderBy.Sort.Asc), @OrderBy(value = WagonFields.code, sort = OrderBy.Sort.Asc) })
    List<IWagon> getWagons();

    void setWagons(List<IWagon> wagons);
}
