package model;

import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.IEntity;
import com.talanlabs.entity.IId;
import com.talanlabs.entity.ITracable;
import com.talanlabs.entity.annotation.Association;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.JoinTable;

import java.time.LocalDate;

@ComponentBean
@Entity(name = "T_PERSON")
public interface IPerson extends IEntity, ITracable {

    @Column(name = "FIRST_NAME")
    String getFirstName();

    void setFirstName(String firstName);

    @Column(name = "LAST_NAME")
    String getLastName();

    void setLastName(String lastName);

    @Column(name = "AGE")
    int getAge();

    void setAge(int age);

    @Column(name = "BIRTHDAY")
    LocalDate getBirthday();

    void setBirthday(LocalDate birthday);

    @Column(name = "ADDRESS_ID")
    IId getAddressId();

    void setAddressId(IId addressId);

    @Association(propertySource = PersonFields.addressId)
    IAddress getAddress();

    void setAddress(IAddress address);

    @Association(propertySource = PersonFields.id, joinTable = @JoinTable(name = "T_ASSO_PERSON_ADDRESS", left = "PERSON_ID", right = "ADDRESS_ID"))
    IAddress getAddress2();

    void setAddress2(IAddress address2);
}
