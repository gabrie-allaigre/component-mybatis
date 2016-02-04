package com.talanlabs.mybatis.rsql.test.data;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.IId;
import com.talanlabs.entity.ITracable;
import com.talanlabs.entity.annotation.Association;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;

@ComponentBean
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
