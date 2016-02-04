package com.talanlabs.mybatis.test.data;

import com.talanlabs.component.IComponent;
import com.talanlabs.component.annotation.ComponentBean;
import com.talanlabs.entity.annotation.Collection;
import com.talanlabs.entity.annotation.Column;
import com.talanlabs.entity.annotation.Entity;
import com.talanlabs.entity.annotation.Id;

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
