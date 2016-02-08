package model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@SynaptixComponent
@Entity(name = "T_USER")
public interface IUser extends IEntity {

    @Column(name = "LOGIN")
    String getLogin();

    void setLogin(String login);

}
