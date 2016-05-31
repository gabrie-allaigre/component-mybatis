package com.synaptix.mybatis.test.data;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.ComponentBean;
import com.synaptix.entity.annotation.Collection;
import com.synaptix.entity.annotation.Column;
import com.synaptix.entity.annotation.Entity;
import com.synaptix.entity.annotation.Id;

import java.util.List;

@ComponentBean
@Entity(name = "T_CATEGORIE")
public interface ICategorie extends IComponent {

    @Id
    @Column(name = "ID_CATEGORIE")
    Integer getIdCategorie();

    void setIdCategorie(Integer idCategorie);

    @Column(name = "ID_CATEGORIE_PARENT")
    Integer getIdCategorieParent();

    void setIdCategorieParent(Integer idCategorieParent);

    @Column(name = "LIB_CATEGORIE")
    String getLibelleCategorie();

    void setLibelleCategorie(String libelleCategorie);

    @Collection(propertySource = CategorieFields.idCategorie, propertyTarget = CategorieFields.idCategorieParent)
    List<ICategorie> getListSousCategorie();

    void setListSousCategorie(List<ICategorie> listSousCategorie);

}
