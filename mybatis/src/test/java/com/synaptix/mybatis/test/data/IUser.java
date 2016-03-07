package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.*;

import java.util.List;

@ComponentBean
@Entity(name = "T_USER")
public interface IUser extends IEntity, ITracable {

    @Column(name = "LOGIN")
    String getLogin();

    void setLogin(String login);

    @Collection(propertyTarget = GroupFields.userId)
    List<IGroup> getGroups();

    void setGroups(List<IGroup> groups);

    @Column(name = "COUNTRY_ID")
    IId getCountryId();

    void setCountryId(IId countryId);

    @Association(propertySource = UserFields.countryId)
    ICountry getCountry();

    void setCountry(ICountry country);

    @Column(name = "COUNTRY_CODE")
    String getCountryCode();

    void setCountryCode(String countryCode);

    @Association(propertySource = { UserFields.countryCode, UserFields.countryId }, propertyTarget = { CountryFields.code, CountryFields.id })
    ICountry getCountryOther();

    void setCountryOther(ICountry countryOther);

    @Column(name = "ADDRESS_ID")
    IId getAddressId();

    void setAddressId(IId addressId);

    @Association(propertySource = UserFields.addressId)
    IAddress getAddress();

    void setAddress(IAddress address);

    //@Collection(joinTable = { @JoinTable(name = "t_asso_user_toto", left = "user_id", right = "toto_id"), @JoinTable(name = "t_asso_toto_address", left = "toto_id", right = "address_id") })
    @Collection(joinTable = @JoinTable(name = "t_asso_user_address", left = "user_id", right = "address_id"))
    List<IAddress> getAddresses();

    void setAddresses(List<IAddress> addresses);

}
