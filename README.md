La surcouche permet : 

- Simplie l'insertion, mise à jour et effacer facilement un component bean
- Simplie le findById d'un component bean
- 0 écriture d'un ResultMap pour les component bean
- Gestion du lazy loading des component bean
- Gestion d'un cache hérité pour les component bean
- Gestion du multi-lingue sur champs multiples dans les componentn bean

Pour l'utiliser, il faut ajouter dans le 'pom.xml' du projet les dépendances :

```xml
<dependencies>
    <dependency>
        <groupId>com.synaptix</groupId>
        <artifactId>component-mybatis</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

Avec Guice

```xml
<dependencies>
    <dependency>
        <groupId>com.synaptix</groupId>
        <artifactId>component-mybatis-guice</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```
