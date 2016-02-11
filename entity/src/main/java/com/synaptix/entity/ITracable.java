package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.annotation.Column;

import java.util.Date;

@SynaptixComponent
public interface ITracable extends IComponent {

    @Column(name = "CREATED_DATE")
    Date getCreatedDate();

    void setCreatedDate(Date createdDate);

    @Column(name = "CREATED_BY")
    String getCreatedBy();

    void setCreatedBy(String createdBy);

    @Column(name = "UPDATED_DATE")
    Date getUpdatedDate();

    void setUpdatedDate(Date updatedDate);

    @Column(name = "UPDATED_BY")
    String getUpdatedBy();

    void setUpdatedBy(String updatedBy);

}
