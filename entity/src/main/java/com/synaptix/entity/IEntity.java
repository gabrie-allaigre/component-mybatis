package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Id;
import com.synaptix.entity.annotation.Version;

@SynaptixComponent
public interface IEntity extends IComponent {

    @EqualsKey
    @Id
    @Column(name = "ID")
    IId getId();

    void setId(IId id);

    @Version
    @Column(name = "VERSION")
    Integer getVersion();

    void setVersion(Integer version);

}
