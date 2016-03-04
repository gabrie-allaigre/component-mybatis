package com.synaptix.entity;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Id;
import com.synaptix.entity.annotation.Version;
import com.synaptix.entity.helper.IdKeyGenerator;

@SynaptixComponent
public interface IEntity extends IComponent {

    @EqualsKey
    @Id(keyGeneratorClass = IdKeyGenerator.class)
    @Column(name = "ID")
    IId getId();

    void setId(IId id);

    @Version
    @Column(name = "VERSION")
    Integer getVersion();

    void setVersion(Integer version);

}
