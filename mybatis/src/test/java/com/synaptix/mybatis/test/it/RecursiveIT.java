package com.synaptix.mybatis.test.it;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.mybatis.component.statement.StatementNameHelper;
import com.synaptix.mybatis.test.data.CategorieFields;
import com.synaptix.mybatis.test.data.ICategorie;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RecursiveIT extends AbstractIntegration {

    @Test
    public void testFindCategorieById() {
        ICategorie categorie = sqlSessionManager.selectOne(StatementNameHelper.buildFindEntityByIdKey(ICategorie.class), 0);

        Assertions.assertThat(categorie).isNotNull();
        Assertions.assertThat(categorie.getLibelleCategorie()).isEqualTo("ELISE");
        Assertions.assertThat(categorie.getListSousCategorie()).isNotNull().hasSize(3).extracting("libelleCategorie").containsOnly("JOSE", "BEATRICE", "MARIE");

        ICategorie beatriceCategorie = ComponentHelper.findComponentBy(categorie.getListSousCategorie(), CategorieFields.idCategorie, 2);
        Assertions.assertThat(beatriceCategorie).isNotNull();
        Assertions.assertThat(beatriceCategorie.getLibelleCategorie()).isEqualTo("BEATRICE");
        Assertions.assertThat(beatriceCategorie.getListSousCategorie()).isNotNull().hasSize(2).extracting("libelleCategorie").containsOnly("GABRIEL", "DAVID");
    }
}
