package com.synaptix.mybatis.test.data;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.annotation.Entity;

@ComponentBean
@Entity(name = "")
public interface IFake2 extends IComponent {

    String getName();

    void setName(String name);

}
