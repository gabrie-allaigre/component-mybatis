package com.synaptix.mybatis.test.unit;

import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IGroup;
import com.synaptix.mybatis.test.data.IUser;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CacheNameTest {

    @Test
    public void testBuildCacheKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(CacheNameHelper.buildCacheKey(null)).isNull();
        softAssertions.assertThat(CacheNameHelper.buildCacheKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/cache");
        softAssertions.assertAll();
    }

    @Test
    public void testIsCacheKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(CacheNameHelper.isCacheKey(null)).isFalse();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("com.model.IUser/cache")).isTrue();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("com.model.IUser1/cache")).isTrue();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("model.IUser/cache")).isTrue();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("IUser/cache")).isTrue();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("model.IUser")).isFalse();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("/cache")).isFalse();
        softAssertions.assertThat(CacheNameHelper.isCacheKey("model-IUser/cache")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInCacheKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(CacheNameHelper.extractComponentNameInCacheKey(null)).isNull();
        softAssertions.assertThat(CacheNameHelper.extractComponentNameInCacheKey("IUser/cache")).isEqualTo("IUser");
        softAssertions.assertThat(CacheNameHelper.extractComponentNameInCacheKey("model.IUser/cache")).isEqualTo("model.IUser");
        softAssertions.assertThat(CacheNameHelper.extractComponentNameInCacheKey("com.model.IUser/cache")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }
}
