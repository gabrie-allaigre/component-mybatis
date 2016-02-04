package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Entity;

@ComponentBean
@Entity(name = "")
public interface IFake2 extends IComponent {

    String getName();

    void setName(String name);

}
