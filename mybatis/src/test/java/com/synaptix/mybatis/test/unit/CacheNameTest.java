package com.synaptix.mybatis.test.unit;

import com.synaptix.mybatis.component.cache.CacheNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

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
        softAssertions.assertThat(CacheNameHelper.extractComponentClassInCacheKey(null)).isNull();
        softAssertions.assertThat(CacheNameHelper.extractComponentClassInCacheKey("com.synaptix.mybatis.test.data.IUser/cache")).isEqualTo(IUser.class);
        softAssertions.assertThat(CacheNameHelper.extractComponentClassInCacheKey("model.IUser/cache")).isNull();
        softAssertions.assertAll();
    }
}
