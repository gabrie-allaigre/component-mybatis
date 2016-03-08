package com.synaptix.mybatis.test.unit;

import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.IGroup;
import com.synaptix.mybatis.test.data.IUser;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.model.IUser1/findEntityById")).isTrue();
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
    public void testBuildFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(null, false)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, "login")).isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, "login"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login&ignoreCancel");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, "login", "password"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login,password");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, "login", "password"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login,password&ignoreCancel");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser,login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser;login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser,login&ignoreCancel")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser,login&toto")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser/findComponentsBy?")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("/findComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("model-IUser/findComponentsBy")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("IUser/findComponentsBy?properties=login")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login")).isEqualTo("com.model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login,password")).isEqualTo("com.model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=login,password&ignoreCancel"))
                .isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractIdParentNameInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("IUser/findComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password")).containsOnly("idUser", "password");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password&ignoreCancel"))
                .containsOnly("idUser", "password");
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
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password&ignoreCancel")).isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(null, null, false, null, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, null, false, null, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Collections.singletonList(Pair.of("t_asso_group_user", Pair.of(new String[] { "group_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" })).isEqualTo(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Arrays.asList(Pair.of("t_asso_group_toto", Pair.of(new String[] { "group_id" }, new String[] { "toto_id" })),
                        Pair.of("t_asso_toto_user", Pair.of(new String[] { "toto_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" })).isEqualTo(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Arrays.asList(Pair.of("t_asso_group_toto", Pair.of(new String[] { "group_code", "group_version" }, new String[] { "toto_id" })),
                        Pair.of("t_asso_toto_user", Pair.of(new String[] { "toto_id" }, new String[] { "user_code" }))), new String[] { "code", "version" }, new String[] { "code" })).isEqualTo(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo("com.synaptix.mybatis.test.data.IGroup");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo("com.synaptix.mybatis.test.data.IGroup");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractSourceComponentNameInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentNameInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentNameInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser");
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentNameInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo("com.synaptix.mybatis.test.data.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractSourcePropertiesInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id"))
                .containsOnly("id");
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .containsOnly("code", "version");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractTargetPropertiesInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id"))
                .containsOnly("targetId");
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .containsOnly("code");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractJoinInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(null)).isNull();
        List<Pair<String, Pair<String[], String[]>>> p1 = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id");
        softAssertions.assertThat(p1.size()).isEqualTo(1);
        softAssertions.assertThat(p1.get(0).getLeft()).isEqualTo("t_asso_group_user");
        softAssertions.assertThat(p1.get(0).getRight().getLeft()).containsExactly("group_id");
        softAssertions.assertThat(p1.get(0).getRight().getRight()).containsExactly("user_id");
        List<Pair<String, Pair<String[], String[]>>> p2 = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code");
        softAssertions.assertThat(p2.size()).isEqualTo(2);
        softAssertions.assertThat(p2.get(0).getLeft()).isEqualTo("t_asso_group_toto");
        softAssertions.assertThat(p2.get(0).getRight().getLeft()).containsExactly("group_code", "group_version");
        softAssertions.assertThat(p2.get(0).getRight().getRight()).containsExactly("toto_id");
        softAssertions.assertThat(p2.get(1).getLeft()).isEqualTo("t_asso_toto_user");
        softAssertions.assertThat(p2.get(1).getRight().getLeft()).containsExactly("toto_id");
        softAssertions.assertThat(p2.get(1).getRight().getRight()).containsExactly("user_code");
        softAssertions.assertAll();
    }

    @Test
    public void testIsIgnoreCancelInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id"))
                .isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id&ignoreCancel"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code&ignoreCancel"))
                .isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildInsertKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildInsertKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/insert");
        softAssertions.assertAll();
    }

    @Test
    public void testIsInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isInsertKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("com.model.IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("com.model.IUser1/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model.IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("/insert")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model-IUser/insert")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInInsertKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInInsertKey("IUser/insert")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInInsertKey("model.IUser/insert")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInInsertKey("com.model.IUser/insert")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testBuildUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildUpdateKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/update");
        softAssertions.assertAll();
    }

    @Test
    public void testIsUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.model.IUser/update")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.model.IUser1/update")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model.IUser/update")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("IUser/update")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("/update")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model-IUser/update")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInUpdateKey("IUser/update")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInUpdateKey("model.IUser/update")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInUpdateKey("com.model.IUser/update")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testBuildDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildDeleteKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/delete");
        softAssertions.assertAll();
    }

    @Test
    public void testIsDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("com.model.IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("com.model.IUser1/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model.IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("/delete")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model-IUser/delete")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInDeleteKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInDeleteKey("IUser/delete")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInDeleteKey("model.IUser/delete")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInDeleteKey("com.model.IUser/delete")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }

    @Test
    public void testBuildNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildNlsKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildNlsKey(IUser.class)).isEqualTo("com.synaptix.mybatis.test.data.IUser/nls");
        softAssertions.assertAll();
    }

    @Test
    public void testIsNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isNlsKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("com.model.IUser/nls")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("com.model.IUser1/nls")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("model.IUser/nls")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("IUser/nls")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("/nls")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isNlsKey("model-IUser/nls")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInNlsKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInNlsKey("IUser/nls")).isEqualTo("IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInNlsKey("model.IUser/nls")).isEqualTo("model.IUser");
        softAssertions.assertThat(StatementNameHelper.extractComponentNameInNlsKey("com.model.IUser/nls")).isEqualTo("com.model.IUser");
        softAssertions.assertAll();
    }
}
