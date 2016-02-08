package model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;

import javax.persistence.Column;
import javax.persistence.Entity;

@SynaptixComponent
@Entity(name = "T_GROUP")
public interface IGroup extends IEntity {

    @Column(name = "USERID")
    IId getUserId();

    void setUserId(IId userId);

    @Column(name = "NAME")
    String getName();

    void setName(String name);

}
