package com.synaptix.mybatis.test.unit;

import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.component.ComponentMyBatisHelper;
import com.synaptix.mybatis.test.data.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class ComponentMyBatisHelperTest {

    @Test
    public void testLoadComponentClass() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ComponentMyBatisHelper.loadComponentClass("toto.IFake")).isNull();
        softAssertions.assertThat(ComponentMyBatisHelper.loadComponentClass("com.synaptix.mybatis.test.data.IUser")).isEqualTo(IUser.class);
        softAssertions.assertAll();
    }

    @Test
    public void testGetEntityAnnotation() {
        Assertions.assertThat(ComponentMyBatisHelper.getEntityAnnotation(ComponentFactory.getInstance().getDescriptor(IUser.class))).isNotNull();
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper.getEntityAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Not found annotation Entity for Component=interface com.synaptix.mybatis.test.data.IFake");
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper.getEntityAnnotation(ComponentFactory.getInstance().getDescriptor(IFake2.class))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Not name in Entity for Component=interface com.synaptix.mybatis.test.data.IFake2");
    }

    @Test
    public void testGetColumnAnnotation() {
        Assertions.assertThat(ComponentMyBatisHelper
                .getColumnAnnotation(ComponentFactory.getInstance().getDescriptor(IUser.class), ComponentFactory.getInstance().getDescriptor(IUser.class).getPropertyDescriptor(UserFields.addressId)))
                .isNotNull();
        Assertions.assertThat(ComponentMyBatisHelper
                .getColumnAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name)))
                .isNull();
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper
                .getColumnAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name2))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Not name in Column for Component=interface com.synaptix.mybatis.test.data.IFake with property=name2");
    }

    @Test
    public void testGetNlsColumnAnnotation() {
        Assertions.assertThat(ComponentMyBatisHelper.getNlsColumnAnnotation(ComponentFactory.getInstance().getDescriptor(ICountry.class),
                ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.name))).isNotNull();
        Assertions.assertThat(ComponentMyBatisHelper
                .getNlsColumnAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name)))
                .isNull();
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper
                .getNlsColumnAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name3))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Not name in NlsColumn for Component=interface com.synaptix.mybatis.test.data.IFake with property=name3");
    }

    @Test
    public void testGetIdAnnotation() {
        Assertions.assertThat(ComponentMyBatisHelper
                .getIdAnnotation(ComponentFactory.getInstance().getDescriptor(IUser.class), ComponentFactory.getInstance().getDescriptor(IUser.class).getPropertyDescriptor(UserFields.id)))
                .isNotNull();
        Assertions.assertThat(ComponentMyBatisHelper
                .getIdAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name2)))
                .isNull();
    }

    @Test
    public void testGetVersionAnnotation() {
        Assertions.assertThat(ComponentMyBatisHelper.getVersionAnnotation(ComponentFactory.getInstance().getDescriptor(ICountry.class),
                ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.version))).isNotNull();
        Assertions.assertThat(ComponentMyBatisHelper
                .getVersionAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name)))
                .isNull();
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper
                .getVersionAnnotation(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name2))))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Not int or long return type of Version for Component=interface com.synaptix.mybatis.test.data.IFake with property=name2");
    }

    @Test
    public void testBuildSetColumn() {
        Assertions.assertThat(ComponentMyBatisHelper
                .buildSetColumn(ComponentFactory.getInstance().getDescriptor(ICountry.class), ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.code)))
                .isEqualTo("CODE = #{code,javaType=java.lang.String}");
        Assertions.assertThat(ComponentMyBatisHelper
                .buildSetColumn(ComponentFactory.getInstance().getDescriptor(IFake.class), ComponentFactory.getInstance().getDescriptor(IFake.class).getPropertyDescriptor(FakeFields.name))).isNull();
    }

    @Test
    public void testBuildSetIdColumn() {
        Assertions.assertThat(ComponentMyBatisHelper
                .buildSetIdColumn(ComponentFactory.getInstance().getDescriptor(ICountry.class), ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.id)))
                .isEqualTo("ID = #{id,javaType=com.synaptix.entity.IId}");
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper.buildSetIdColumn(ComponentFactory.getInstance().getDescriptor(ICountry.class),
                ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.code)))).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not present annotation Id for Component=interface com.synaptix.mybatis.test.data.ICountry with property=code");
    }

    @Test
    public void testBuildSetVersionColumn() {
        Assertions.assertThat(ComponentMyBatisHelper.buildSetVersionColumn(ComponentFactory.getInstance().getDescriptor(ICountry.class),
                ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.version))).isEqualTo("VERSION = #{version,javaType=java.lang.Integer}");
        Assertions.assertThat(Assertions.catchThrowable(() -> ComponentMyBatisHelper.buildSetVersionColumn(ComponentFactory.getInstance().getDescriptor(ICountry.class),
                ComponentFactory.getInstance().getDescriptor(ICountry.class).getPropertyDescriptor(CountryFields.code)))).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not present annotation Version for Component=interface com.synaptix.mybatis.test.data.ICountry with property=code");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindAllLinks() {
        Assertions.assertThat(ComponentMyBatisHelper.findAllLinks(IGroup.class)).hasSize(0);
        Assertions.assertThat(ComponentMyBatisHelper.findAllLinks(IAddress.class)).hasSize(4).containsOnly(IGroup.class, IAddress.class, ICountry.class, IUser.class);
        Assertions.assertThat(ComponentMyBatisHelper.findAllLinks(ICountry.class)).hasSize(4).containsOnly(IUser.class, IGroup.class, IAddress.class, ICountry.class);
    }

    @Test
    public void testIsUseNlsColumn() {
        Assertions.assertThat(ComponentMyBatisHelper.isUseNlsColumn(IGroup.class)).isFalse();
        Assertions.assertThat(ComponentMyBatisHelper.isUseNlsColumn(ICountry.class)).isTrue();
        Assertions.assertThat(ComponentMyBatisHelper.isUseNlsColumn(IUser.class)).isTrue();
    }
}
