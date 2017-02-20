package com.talanlabs.mybatis.rsql.test.unit;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class RsqlBuilderTest {

    @Test
    public void testRsqlLongBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newLongBuilder().is("name", "==", "gaby").query()).isEqualTo("name==gaby");
        softAssertions.assertThat(RsqlBuilder.newLongBuilder().is("name", "==", "gaby").and().is("code", "==", "GAB").query()).isEqualTo("name==gaby and code==GAB");
        softAssertions.assertThat(RsqlBuilder.newLongBuilder().is("name", "==", "gaby").or().is("code", "==", "GAB").query()).isEqualTo("name==gaby or code==GAB");
        softAssertions.assertThat(RsqlBuilder.newLongBuilder().is("code", "==", "GAB").and().openGroup().is("name", "==", "gaby").or().is("name", "==", "toto").closeGroup().query())
                .isEqualTo("code==GAB and (name==gaby or name==toto)");
        softAssertions
                .assertThat(RsqlBuilder.newLongBuilder().openGroup().openGroup().is("name", "==", "gaby").or().is("name", "==", "toto").closeGroup().and().is("code", "==", "GAB").closeGroup().query())
                .isEqualTo("((name==gaby or name==toto) and code==GAB)");
        softAssertions.assertAll();
    }

    @Test
    public void testRsqlShortBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions
                .assertThat(RsqlBuilder.newBuilder().openGroup().openGroup().is("name", "==", "gaby").or().is("name", "==", "toto").closeGroup().and().is("code", "==", "GAB").closeGroup().query())
                .isEqualTo("((name==gaby,name==toto);code==GAB)");
        softAssertions.assertAll();
    }
}
