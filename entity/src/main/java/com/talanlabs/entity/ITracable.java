package com.talanlabs.entity;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Column;

import java.util.Date;

@ComponentBean
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
