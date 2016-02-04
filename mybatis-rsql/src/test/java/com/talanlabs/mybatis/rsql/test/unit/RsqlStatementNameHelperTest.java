package com.talanlabs.mybatis.rsql.test.unit;

import com.talanlabs.mybatis.rsql.statement.RsqlStatementNameHelper;
import com.talanlabs.mybatis.rsql.test.data.ICountry;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class RsqlStatementNameHelperTest {

    @Test
    public void testBuildRsqlKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlStatementNameHelper.buildRsqlKey(null)).isNull();
        softAssertions.assertThat(RsqlStatementNameHelper.buildRsqlKey(ICountry.class)).isEqualTo("com.talanlabs.mybatis.rsql.test.data.ICountry/rsql");
        softAssertions.assertAll();
    }

    @Test
    public void testIsRsqlKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey(null)).isFalse();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("com.model.IUser/rsql")).isTrue();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("com.model.IUser1/rsql")).isTrue();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("model.IUser/rsql")).isTrue();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("com.talanlabs.mybatis.rsql.test.data.IUser/rsql")).isTrue();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("model.IUser")).isFalse();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("/rsql")).isFalse();
        softAssertions.assertThat(RsqlStatementNameHelper.isRsqlKey("model-IUser/rsql")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInRsqlKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlStatementNameHelper.extractComponentClassInRsqlKey(null)).isNull();
        softAssertions.assertThat(RsqlStatementNameHelper.extractComponentClassInRsqlKey("com.talanlabs.mybatis.rsql.test.data.ICountry/rsql")).isEqualTo(ICountry.class);
        softAssertions.assertThat(RsqlStatementNameHelper.extractComponentClassInRsqlKey("model.IUser/rsql")).isNull();
        softAssertions.assertAll();
    }
}
