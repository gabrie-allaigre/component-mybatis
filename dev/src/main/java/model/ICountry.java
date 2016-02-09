package model;

import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;

@Entity(name = "T_COUNTRY")
@SynaptixComponent
public interface ICountry extends IEntity, ITracable, ICancellable {

    @Column(name = "NAME")
    String getName();

    void setName(String name);

}
