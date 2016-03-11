package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.annotation.Column;

import java.util.Date;

@ComponentBean
public interface ICancelable extends IComponent {

    @Column(name = "CANCELED")
    boolean isCanceled();

    void setCanceled(boolean canceled);

    @Column(name = "CANCELED_DATE")
    Date getCanceledDate();

    void setCanceledDate(Date canceledDate);

    @Column(name = "CANCELED_BY")
    String getCanceledBy();

    void setCanceledBy(String canceledBy);

}
