package com.talanlabs.mybatis.test.unit;

import com.talanlabs.mybatis.component.statement.StatementNameHelper;
import com.talanlabs.mybatis.test.data.IGroup;
import com.talanlabs.mybatis.test.data.IUser;
import com.talanlabs.mybatis.test.data.IWagon;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StatementNameHelperTest {

    @Test
    public void testBuildFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindEntityByIdKey(IUser.class)).isEqualTo("com.talanlabs.mybatis.test.data.IUser/findEntityById");
        softAssertions.assertThat(StatementNameHelper.buildFindEntityByIdKey(IWagon.IWheel.class)).isEqualTo("com.talanlabs.mybatis.test.data.IWagon$IWheel/findEntityById");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.model.IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.model.IUser1/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model.IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.talanlabs.mybatis.test.data.IUser/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("com.talanlabs.mybatis.test.data.IWagon$IWheel/findEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("/findEntityById")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindEntityByIdKey("model-IUser/findEntityById")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey("com.talanlabs.mybatis.test.data.IUser/findEntityById")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey("com.talanlabs.mybatis.test.data.IWagon$IWheel/findEntityById")).isEqualTo(IWagon.IWheel.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindEntityByIdKey("model.IUser/findEntityById")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(null, false, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, new String[] { "login" }, null))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, new String[] { "login" }, null))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&ignoreCancel");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, new String[] { "login", "password" }, null))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, new String[] { "login", "password" }, null))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password&ignoreCancel");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, new String[] { "login" }, Collections.singletonList(Pair.of("rang", "Asc"))))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, false, new String[] { "login" }, Arrays.asList(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc"))))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc#cancel;Desc");
        softAssertions
                .assertThat(StatementNameHelper.buildFindComponentsByKey(IUser.class, true, new String[] { "login", "password" }, Arrays.asList(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc"))))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password&orderBy=rang;Asc#cancel;Desc&ignoreCancel");
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
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc#cancel;Desc")).isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password"))
                .isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password&ignoreCancel"))
                .isEqualTo(IUser.class);
        softAssertions.assertAll();
    }

    @Test
    public void testExtractPropertyNamesInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login")).containsOnly("login");
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
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("model.IUser/findComponentsBy?properties=idUser&ignoreCancel")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByKey("com.model.IUser/findComponentsBy?properties=idUser,password&ignoreCancel")).isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractOrderBysInFindComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc"))
                .containsExactly(Pair.of("rang", "Asc"));
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login&orderBy=rang;Asc#cancel;Desc"))
                .containsExactly(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc"));
        softAssertions.assertThat(
                StatementNameHelper.extractOrderBiesInFindComponentsByKey("com.talanlabs.mybatis.test.data.IUser/findComponentsBy?properties=login,password&orderBy=rang;Asc#cancel;Desc&ignoreCancel"))
                .containsExactly(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc"));
        softAssertions.assertAll();
    }

    @Test
    public void testBuildFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(null, null, false, null, null, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, null, false, null, null, null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Collections.singletonList(Pair.of("t_asso_group_user", Pair.of(new String[] { "group_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" }, null))
                .isEqualTo(
                        "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Arrays.asList(Pair.of("t_asso_group_toto", Pair.of(new String[] { "group_id" }, new String[] { "toto_id" })),
                        Pair.of("t_asso_toto_user", Pair.of(new String[] { "toto_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" }, null)).isEqualTo(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Arrays.asList(Pair.of("t_asso_group_toto", Pair.of(new String[] { "group_code", "group_version" }, new String[] { "toto_id" })),
                        Pair.of("t_asso_toto_user", Pair.of(new String[] { "toto_id" }, new String[] { "user_code" }))), new String[] { "code", "version" }, new String[] { "code" }, null)).isEqualTo(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Collections.singletonList(Pair.of("t_asso_group_user", Pair.of(new String[] { "group_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" },
                Collections.singletonList(Pair.of("rang", "Asc")))).isEqualTo(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id&orderBy=rang;Asc");
        softAssertions.assertThat(StatementNameHelper.buildFindComponentsByJoinTableKey(IUser.class, IGroup.class, false,
                Arrays.asList(Pair.of("t_asso_group_toto", Pair.of(new String[] { "group_id" }, new String[] { "toto_id" })),
                        Pair.of("t_asso_toto_user", Pair.of(new String[] { "toto_id" }, new String[] { "user_id" }))), new String[] { "id" }, new String[] { "id" },
                Arrays.asList(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc")))).isEqualTo(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id&orderBy=rang;Asc#cancel;Desc");
        softAssertions.assertAll();
    }

    @Test
    public void testIsFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id&orderBy=rang;Asc"))
                .isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_toto;group_id;toto_id#t_asso_toto_user;toto_id;user_id&orderBy=rang;Asc#cancel;Desc"))
                .isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo(IGroup.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo(IGroup.class);
        softAssertions.assertAll();
    }

    @Test
    public void testExtractSourceComponentNameInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id"))
                .isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractSourceComponentClassInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=com.talanlabs.mybatis.test.data.IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isEqualTo(IUser.class);
        softAssertions.assertAll();
    }

    @Test
    public void testExtractSourcePropertiesInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id"))
                .containsOnly("id");
        softAssertions.assertThat(StatementNameHelper.extractSourcePropertiesInFindComponentsByJoinTableKey(
                "com.talanlabs.mybatis.test.data.IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .containsOnly("code", "version");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractTargetPropertiesInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id")).containsOnly("targetId");
        softAssertions.assertThat(StatementNameHelper.extractTargetPropertiesInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .containsOnly("code");
        softAssertions.assertAll();
    }

    @Test
    public void testExtractJoinInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(null)).isNull();
        List<Pair<String, Pair<String[], String[]>>> p1 = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id");
        softAssertions.assertThat(p1.size()).isEqualTo(1);
        softAssertions.assertThat(p1.get(0).getLeft()).isEqualTo("t_asso_group_user");
        softAssertions.assertThat(p1.get(0).getRight().getLeft()).containsExactly("group_id");
        softAssertions.assertThat(p1.get(0).getRight().getRight()).containsExactly("user_id");
        List<Pair<String, Pair<String[], String[]>>> p2 = StatementNameHelper.extractJoinInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code");
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
    public void testExtractOrderBysInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByJoinTableKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id&orderBy=rang;Asc"))
                .containsExactly(Pair.of("rang", "Asc"));
        softAssertions.assertThat(StatementNameHelper.extractOrderBiesInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=id&join=t_asso_group_user;group_id;user_id&orderBy=rang;Asc#cancel;Desc"))
                .containsExactly(Pair.of("rang", "Asc"), Pair.of("cancel", "Desc"));
        softAssertions.assertAll();
    }

    @Test
    public void testIsIgnoreCancelInFindComponentsByJoinTableKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=id&targetProperties=targetId&join=t_asso_group_user;group_id;user_id&ignoreCancel")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code"))
                .isFalse();
        softAssertions.assertThat(StatementNameHelper.isIgnoreCancelInFindComponentsByJoinTableKey(
                "IGroup/findComponentsByJoinTable?sourceComponent=IUser&sourceProperties=code,version&targetProperties=code&join=t_asso_group_toto;group_code,group_version;toto_id#t_asso_toto_user;toto_id;user_code&ignoreCancel"))
                .isTrue();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildInsertKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildInsertKey(IUser.class)).isEqualTo("com.talanlabs.mybatis.test.data.IUser/insert");
        softAssertions.assertAll();
    }

    @Test
    public void testIsInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isInsertKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("com.model.IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("com.model.IUser1/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model.IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("com.talanlabs.mybatis.test.data.IUser/insert")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("/insert")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isInsertKey("model-IUser/insert")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInInsertKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey("com.talanlabs.mybatis.test.data.IUser/insert")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInInsertKey("model.IUser/insert")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildUpdateKey(IUser.class)).isEqualTo("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=");
        softAssertions.assertThat(StatementNameHelper.buildUpdateKey(IUser.class, "name")).isEqualTo("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=name");
        softAssertions.assertAll();
    }

    @Test
    public void testIsUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.model.IUser/update")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.model.IUser/update?nlsProperties=")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.model.IUser1/update?nlsProperties=toto")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model.IUser/update?nlsProperties=titi,toto")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("/update?nlsProperties=")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isUpdateKey("model-IUser/update")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=rien")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInUpdateKey("model.IUser/update")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractNlsPropertyNamesInUpdateKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractNlsPropertiesInUpdateKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractNlsPropertiesInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update")).isEmpty();
        softAssertions.assertThat(StatementNameHelper.extractNlsPropertiesInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=")).isEmpty();
        softAssertions.assertThat(StatementNameHelper.extractNlsPropertiesInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=name")).containsExactly("name");
        softAssertions.assertThat(StatementNameHelper.extractNlsPropertiesInUpdateKey("com.talanlabs.mybatis.test.data.IUser/update?nlsProperties=code,name")).containsExactly("code", "name");
        softAssertions.assertAll();
    }

    @Test
    public void testBuildDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildDeleteKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteKey(IUser.class)).isEqualTo("com.talanlabs.mybatis.test.data.IUser/delete");
        softAssertions.assertAll();
    }

    @Test
    public void testIsDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("com.model.IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("com.model.IUser1/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model.IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("com.talanlabs.mybatis.test.data.IUser/delete")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("/delete")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteKey("model-IUser/delete")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInDeleteKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey("com.talanlabs.mybatis.test.data.IUser/delete")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteKey("model.IUser/delete")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(null, null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(IUser.class, "name")).isEqualTo("com.talanlabs.mybatis.test.data.IUser/findNlsColumn?property=name");
        softAssertions.assertThat(StatementNameHelper.buildFindNlsColumnKey(IUser.class, "toto")).isEqualTo("com.talanlabs.mybatis.test.data.IUser/findNlsColumn?property=toto");
        softAssertions.assertAll();
    }

    @Test
    public void testIsNlsKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("com.model.IUser/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("com.model.IUser1/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("model.IUser/findNlsColumn?property=name")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isFindNlsColumnKey("com.talanlabs.mybatis.test.data.IUser/findNlsColumn?property=name")).isTrue();
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
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindNlsColumnKey("com.talanlabs.mybatis.test.data.IUser/findNlsColumn?property=name")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInFindNlsColumnKey("model.IUser/findNlsColumn?property=name")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildDeleteEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildDeleteEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteEntityByIdKey(IUser.class)).isEqualTo("com.talanlabs.mybatis.test.data.IUser/deleteEntityById");
        softAssertions.assertAll();
    }

    @Test
    public void testIsDeleteEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("com.model.IUser/deleteEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("com.model.IUser1/deleteEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("model.IUser/deleteEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("com.talanlabs.mybatis.test.data.IUser/deleteEntityById")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("/deleteEntityById")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteEntityByIdKey("model-IUser/deleteEntityById")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInDeleteEntityByIdKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteEntityByIdKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteEntityByIdKey("com.talanlabs.mybatis.test.data.IUser/deleteEntityById")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteEntityByIdKey("model.IUser/deleteEntityById")).isNull();
        softAssertions.assertAll();
    }

    @Test
    public void testBuildDeleteComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class)).isNull();
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class, "login")).isEqualTo("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login");
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class, "login")).isEqualTo("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login");
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class, "login", "password"))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login,password");
        softAssertions.assertThat(StatementNameHelper.buildDeleteComponentsByKey(IUser.class, "login", "password"))
                .isEqualTo("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login,password");
        softAssertions.assertAll();
    }

    @Test
    public void testIsDeleteComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey(null)).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("com.model.IUser/deleteComponentsBy?properties=login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser,login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser;login")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser,login")).isTrue();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser,login&toto")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?idUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser/deleteComponentsBy?")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model.IUser")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("/deleteComponentsBy")).isFalse();
        softAssertions.assertThat(StatementNameHelper.isDeleteComponentsByKey("model-IUser/deleteComponentsBy")).isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void testExtractComponentNameInDeleteComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteComponentsByKey("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login")).isEqualTo(IUser.class);
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=login")).isNull();
        softAssertions.assertThat(StatementNameHelper.extractComponentClassInDeleteComponentsByKey("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login,password"))
                .isEqualTo(IUser.class);
        softAssertions.assertAll();
    }

    @Test
    public void testExtractIdParentNameInDeleteComponentsByKey() {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey(null)).isNull();
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey("com.talanlabs.mybatis.test.data.IUser/deleteComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=login")).containsOnly("login");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey("model.IUser/deleteComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey("com.model.IUser/deleteComponentsBy?properties=idUser")).containsOnly("idUser");
        softAssertions.assertThat(StatementNameHelper.extractPropertyNamesInDeleteComponentsByKey("com.model.IUser/deleteComponentsBy?properties=idUser,password")).containsOnly("idUser", "password");
        softAssertions.assertAll();
    }
}
