package model;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.ICancelable;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.IId;
import com.talanlabs.entity.ITracable;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;

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
