package com.synaptix.mybatis.test.data;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.NlsColumn;
import com.synaptix.entity.annotation.Version;

@ComponentBean
public interface IFake extends IComponent {

    String getName();

    void setName(String name);

    @Column(name = "")
    @Version
    String getName2();

    void setName2(String name2);

    @NlsColumn(name = "")
    String getName3();

    void setName3(String name3);

}
