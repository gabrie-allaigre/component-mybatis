package model;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.ICancelable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@Entity(name = "T_GROUP")
@ComponentBean
public interface IGroup extends IEntity, ITracable, ICancelable {

    @Column(name = "USER_ID")
    IId getUserId();

    void setUserId(IId userId);

    @Column(name = "NAME")
    String getName();

    void setName(String name);

}
