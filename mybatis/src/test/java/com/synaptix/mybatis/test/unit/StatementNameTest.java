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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey("com.synaptix.mybatis.test.data.IUser/findEntityById")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey("model.IUser/findEntityById")).isNull();
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login,password"))
                .isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.synaptix.mybatis.test.data.IUser/findComponentsBy?properties=login,password&ignoreCancel"))
                .isEqualTo(IUser.class);
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo(IGroup.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo(IGroup.class);
        softAssertions.assertAll();
    }

    @Test
    public void testExtractSourceComponentNameInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(
                "com.synaptix.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.synaptix.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo(IUser.class);
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey("com.synaptix.mybatis.test.data.IUser/insert")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey("model.IUser/insert")).isNull();
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("com.synaptix.mybatis.test.data.IUser/update")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("model.IUser/update")).isNull();
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey("com.synaptix.mybatis.test.data.IUser/delete")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey("model.IUser/delete")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(IUser.class, "name")).isEqualTo("com.synaptix.mybatis.test.data.IUser/findNlsColumn?property=name");
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(IUser.class, "toto")).isEqualTo("com.synaptix.mybatis.test.data.IUser/findNlsColumn?property=toto");
        softAssertions.assertAll();
    }

    @Test
    public void testIsNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("com.model.IUser/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("com.model.IUser1/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("model.IUser/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("IUser/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("/findNlsColumn")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("model-IUser/findNlsColumn")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("model.IUser/findNlsColumn")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindNlsColumnKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindNlsColumnKey("com.synaptix.mybatis.test.data.IUser/findNlsColumn?property=name")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindNlsColumnKey("model.IUser/findNlsColumn?property=name")).isNull();
        softAssertions.assertAll();
    }
}
