package model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Association;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

import java.util.List;

@SynaptixComponent
@Entity(name = "T_USER")
public interface IUser extends IEntity, ITracable {

    @Column(name = "LOGIN")
    String getLogin();

    void setLogin(String login);

    List<IGroup> getGroups();

    void setGroups(List<IGroup> groups);

    @Column(name = "COUNTRY_ID")
    IId getCountryId();

    void setCountryId(IId countryId);

    @Association(propertySource = UserFields.countryId)
    ICountry getCountry();

    void setCountry(ICountry country);

}
