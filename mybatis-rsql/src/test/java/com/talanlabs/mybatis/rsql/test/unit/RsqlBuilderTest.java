package com.talanlabs.mybatis.rsql.test.unit;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;
import com.talanlabs.mybatis.rsql.test.data.IPerson;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;

public class RsqlBuilderTest {

    @Test
    public void testRsqlLongBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newCompletBuilder().is("name", "==", "gaby").query()).isEqualTo("name==gaby");
        softAssertions.assertThat(RsqlBuilder.newCompletBuilder().is("name", "==", "gaby").and().is("code", "==", "GAB").query()).isEqualTo("name==gaby and code==GAB");
        softAssertions.assertThat(RsqlBuilder.newCompletBuilder().is("name", "==", "gaby").or().is("code", "==", "GAB").query()).isEqualTo("name==gaby or code==GAB");
        softAssertions.assertThat(RsqlBuilder.newCompletBuilder().is("code", "==", "GAB").and().openGroup().is("name", "==", "gaby").or().is("name", "==", "toto").closeGroup().query())
                .isEqualTo("code==GAB and (name==gaby or name==toto)");
        softAssertions.assertThat(
                RsqlBuilder.newCompletBuilder().openGroup().openGroup().is("name", "==", "gaby").or().is("name", "==", "toto").closeGroup().and().is("code", "==", "GAB").closeGroup().query())
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

    @Test
    public void testStringRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").eq("gaby").query()).isEqualTo("name=='gaby'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").neq("gaby").query()).isEqualTo("name!='gaby'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").startWith("gaby").query()).isEqualTo("name=='gaby*'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").endWith("gaby").query()).isEqualTo("name=='*gaby'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").containts("gaby").query()).isEqualTo("name=='*gaby*'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").in("gaby", "sandra").query()).isEqualTo("name=in=('gaby','sandra')");
        softAssertions.assertThat(RsqlBuilder.newBuilder().string("name").nin("gaby", "sandra").query()).isEqualTo("name=out=('gaby','sandra')");
        softAssertions.assertAll();
    }

    @Test
    public void testBooleanRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().bool("name").isTrue().query()).isEqualTo("name==true");
        softAssertions.assertThat(RsqlBuilder.newBuilder().bool("name").isFalse().query()).isEqualTo("name==false");
        softAssertions.assertAll();
    }

    @Test
    public void testShortRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").eq((short) 10).query()).isEqualTo("name==10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").neq((short) 10).query()).isEqualTo("name!=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").lt((short) 10).query()).isEqualTo("name=lt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").lte((short) 10).query()).isEqualTo("name=le=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").gte((short) 10).query()).isEqualTo("name=ge=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").gt((short) 10).query()).isEqualTo("name=gt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").in(Arrays.asList((short) 10, (short) 15)).query()).isEqualTo("name=in=(10,15)");
        softAssertions.assertThat(RsqlBuilder.newBuilder().shortNum("name").nin(Arrays.asList((short) 10, (short) 15)).query()).isEqualTo("name=out=(10,15)");
        softAssertions.assertAll();
    }

    @Test
    public void testIntegerRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").eq(10).query()).isEqualTo("name==10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").neq(10).query()).isEqualTo("name!=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").lt(10).query()).isEqualTo("name=lt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").lte(10).query()).isEqualTo("name=le=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").gte(10).query()).isEqualTo("name=ge=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").gt(10).query()).isEqualTo("name=gt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").in(Arrays.asList(10, 15)).query()).isEqualTo("name=in=(10,15)");
        softAssertions.assertThat(RsqlBuilder.newBuilder().intNum("name").nin(Arrays.asList(10, 15)).query()).isEqualTo("name=out=(10,15)");
        softAssertions.assertAll();
    }

    @Test
    public void testFloatRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").eq(10.1f).query()).isEqualTo("name==10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").neq(10.1f).query()).isEqualTo("name!=10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").lt(10.1f).query()).isEqualTo("name=lt=10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").lte(10.1f).query()).isEqualTo("name=le=10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").gte(10.1f).query()).isEqualTo("name=ge=10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").gt(10.1f).query()).isEqualTo("name=gt=10.1");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").in(Arrays.asList(10.5f, 15f)).query()).isEqualTo("name=in=(10.5,15.0)");
        softAssertions.assertThat(RsqlBuilder.newBuilder().floatNum("name").nin(Arrays.asList(10.5f, 15f)).query()).isEqualTo("name=out=(10.5,15.0)");
        softAssertions.assertAll();
    }

    @Test
    public void testDoubleRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").eq(10.5).query()).isEqualTo("name==10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").neq(10.5).query()).isEqualTo("name!=10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").lt(10.5).query()).isEqualTo("name=lt=10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").lte(10.5).query()).isEqualTo("name=le=10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").gte(10.5).query()).isEqualTo("name=ge=10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").gt(10.5).query()).isEqualTo("name=gt=10.5");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").in(Arrays.asList(10.5, 15.1)).query()).isEqualTo("name=in=(10.5,15.1)");
        softAssertions.assertThat(RsqlBuilder.newBuilder().doubleNum("name").nin(Arrays.asList(10.5, 15.1)).query()).isEqualTo("name=out=(10.5,15.1)");
        softAssertions.assertAll();
    }

    @Test
    public void testLongRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").eq(10L).query()).isEqualTo("name==10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").neq(10L).query()).isEqualTo("name!=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").lt(10L).query()).isEqualTo("name=lt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").lte(10L).query()).isEqualTo("name=le=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").gte(10L).query()).isEqualTo("name=ge=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").gt(10L).query()).isEqualTo("name=gt=10");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").in(Arrays.asList(10L, 15L)).query()).isEqualTo("name=in=(10,15)");
        softAssertions.assertThat(RsqlBuilder.newBuilder().longNum("name").nin(Arrays.asList(10L, 15L)).query()).isEqualTo("name=out=(10,15)");
        softAssertions.assertAll();
    }

    @Test
    public void testEnumRsqlBuilder() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(RsqlBuilder.newBuilder().<IPerson.Sexe>enumeration("name").eq(IPerson.Sexe.MAN).query()).isEqualTo("name=='MAN'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().<IPerson.Sexe>enumeration("name").neq(IPerson.Sexe.MAN).query()).isEqualTo("name!='MAN'");
        softAssertions.assertThat(RsqlBuilder.newBuilder().<IPerson.Sexe>enumeration("name").in(IPerson.Sexe.MAN, IPerson.Sexe.WOMAN).query()).isEqualTo("name=in=('MAN','WOMAN')");
        softAssertions.assertThat(RsqlBuilder.newBuilder().<IPerson.Sexe>enumeration("name").nin(IPerson.Sexe.MAN, IPerson.Sexe.WOMAN).query()).isEqualTo("name=out=('MAN','WOMAN')");
        softAssertions.assertAll();
        softAssertions.assertAll();
    }
}
