package com.synaptix.mybatis.test.unit;

import com.synaptix.mybatis.component.resultmap.ResultMapNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class ResultMapNameTest {

    @Test
    public void testBuildResultMapKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ResultMapNameHelper.buildResultMapKey(null)).isNull();
        softAssertions.assertThat(ResultMapNameHelper.buildResultMapKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/resultMap");
        softAssertions.assertAll();
    }

    @Test
    public void testIsResultMapKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey(null)).isFalse();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("com.model.IUser/resultMap")).isTrue();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("com.model.IUser1/resultMap")).isTrue();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("model.IUser/resultMap")).isTrue();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("IUser/resultMap")).isTrue();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("model.IUser")).isFalse();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("/resultMap")).isFalse();
        softAssertions.assertThat(ResultMapNameHelper.isResultMapKey("model-IUser/resultMap")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInResultMapKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(ResultMapNameHelper.extractComponentClassInResultMapKey(null)).isNull();
        softAssertions.assertThat(ResultMapNameHelper.extractComponentClassInResultMapKey("com.synaptix.mybatis.test.data.IUser/resultMap")).isEqualTo(IUser.class);
        softAssertions.assertThat(ResultMapNameHelper.extractComponentClassInResultMapKey("com.model.IUser/resultMap")).isNull();
        softAssertions.assertAll();
    }
}
