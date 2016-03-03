package com.synaptix.mybatis.test.data;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Association;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@SynaptixComponent
@Entity(name = "T_ADDRESS")
public interface IAddress extends IEntity, ITracable {

    @Column(name = "CITY")
    String getCity();

    void setCity(String city);

    @Column(name = "POSTAL_ZIP")
    String getPostalZip();

    void setPostalZip(String postalZip);

    @Column(name = "COUNTRY_ID")
    IId getCountryId();

    void setCountryId(IId countryId);

    @Association(propertySource = AddressFields.countryId)
    ICountry getCountry();

    void setCountry(ICountry country);

}
