package com.talanlabs.entity;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Id;
import com.talanlabs.entity.annotation.Version;
import com.talanlabs.entity.helper.IdKeyGenerator;

@ComponentBean
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
