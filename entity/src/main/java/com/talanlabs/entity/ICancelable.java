package com.talanlabs.entity;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Column;

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
