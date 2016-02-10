package com.synaptix.mybatis.test.unit;

import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IUser;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class StatementNameTest {

    @Test
    public void testBuildFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindEntityByIdKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/findEntityById");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.model.IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model.IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("/findEntityById")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model-IUser/findEntityById")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindEntityByIdKey("IUser/findEntityById")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindEntityByIdKey("model.IUser/findEntityById")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindEntityByIdKey("com.model.IUser/findEntityById")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testBuildFindChildrenByIdParentKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(null, false)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, "login")).isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, "login")).isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login&ignoreCancel");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, "login", "password"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login;password");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, "login", "password"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login;password&ignoreCancel");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindChildrenByIdParentKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser;login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser&login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser;login&ignoreCancel")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser;login&toto")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("/findComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model-IUser/findComponentsBy")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindChildrenByIdParentKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("IUser/findComponentsBy?properties=login")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login")).isEqualTo("com.model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login;password")).isEqualTo("com.model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login;password&ignoreCancel")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractIdParentNameInFindChildrenByIdParentKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("IUser/findComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser;password")).containsOnly("idUser", "password");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser;password&ignoreCancel")).containsOnly("idUser", "password");
        softAssertions.assertAll();
    }

    @Test
    public void testIsIgnoreCancelInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("IUser/findComponentsBy?properties=login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser&ignoreCancel")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser;password")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser;password&ignoreCancel")).isTrue();
        softAssertions.assertAll();
    }
}
