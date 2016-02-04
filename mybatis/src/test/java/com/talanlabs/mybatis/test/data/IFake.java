package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.NlsColumn;
import com.talanlabs.entity.annotation.Version;

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
