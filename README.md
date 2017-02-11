La surcouche permet : 

- Simplie l'insertion, mise à jour et effacer facilement un component bean
- Simplie le findById d'un component bean
- 0 écriture d'un ResultMap pour les component bean
- Gestion du lazy loading des component bean
- Gestion d'un cache hérité pour les component bean
- Gestion du multi-lingue sur champs multiples dans les component bean
- Génération de requète complexe, format Rsql, tri et pagination (extension Rsql)

Pour l'utiliser, il faut ajouter dans le 'pom.xml' du projet les dépendances :

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

Avec Guice

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis-guice</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

Extension Rsql

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis-rsql</artifactId>
        <version>1.0.1</version>
    </dependency>
</dependencies>
```

# Utilisation

## Avec Guice (Plus simple)

Exemple avec une base HSQLDB :

```java
Injector injector = Guice.createInjector(new MyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.HSQLDB_IN_MEMORY_NAMED);

                install(new DefaultComponentMyBatisModule());

                lazyLoadingEnabled(true);
                aggressiveLazyLoading(false);

                useConfigurationProvider(ComponentConfigurationProvider.class);
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                bindObjectFactoryType(ComponentObjectFactory.class);

                Names.bindProperties(binder(), createTestProperties());
                bind(FooService.class).in(Singleton.class);

                bind(DefaultNlsColumnHandler.class).in(Singleton.class);
                bind(INlsColumnHandler.class).to(DefaultNlsColumnHandler.class).in(Singleton.class);

                bind(DefaultUserByHandler.class).in(Singleton.class);
                bind(TracableTriggerObserver.IUserByHandler.class).to(DefaultUserByHandler.class).in(Singleton.class);

                Multibinder<ITriggerObserver> triggerObserverMultibinder = Multibinder.newSetBinder(binder(), ITriggerObserver.class);
                triggerObserverMultibinder.addBinding().toConstructor(ConstructorUtils.getAccessibleConstructor(TracableTriggerObserver.class, TracableTriggerObserver.IUserByHandler.class));

                bind(ComponentSqlSessionManager.class).toProvider(ComponentSqlSessionManagerProvider.class).in(Scopes.SINGLETON);
                
                addMapper(...);
            }

            private Properties createTestProperties() {
                Properties myBatisProperties = new Properties();
                myBatisProperties.setProperty("mybatis.environment.id", "test");
                myBatisProperties.setProperty("JDBC.schema", "mybatis-guice_TEST");
                myBatisProperties.setProperty("JDBC.username", "sa");
                myBatisProperties.setProperty("JDBC.password", "");
                myBatisProperties.setProperty("JDBC.autoCommit", "false");
                return myBatisProperties;
            }
        });
```

## Normal

```java
        Environment environment = new Environment.Builder("test").dataSource(new PooledDataSource(null, "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:mybatis-guice_TEST", "sa", ""))
                .transactionFactory(new JdbcTransactionFactory()).build();

        ComponentConfiguration componentConfiguration = new ComponentConfiguration(environment);
        componentConfiguration.setObjectFactory(new ComponentObjectFactory());
        componentConfiguration.setProxyFactory(new ComponentProxyFactory());
        componentConfiguration.setLazyLoadingEnabled(true);
        componentConfiguration.setAggressiveLazyLoading(false);
        componentConfiguration.setNlsColumnHandler(new DefaultNlsColumnHandler());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindEntityByIdMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindComponentsByMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindComponentsByJoinTableMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new FindNlsColumnMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new InsertMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new UpdateMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteEntityByIdMappedStatementFactory());
        componentConfiguration.getMappedStatementFactoryRegistry().registry(new DeleteComponentsByMappedStatementFactory());
        componentConfiguration.getResultMapFactoryRegistry().registry(new ComponentResultMapFactory());
        componentConfiguration.getCacheFactoryRegistry().registry(new ComponentCacheFactory());
        componentConfiguration.getTriggerDispatcher().addTriggerObserver(new TracableTriggerObserver(new DefaultUserByHandler()));
        componentConfiguration.getTypeHandlerRegistry().register(IdTypeHandler.class);

        componentConfiguration.addMapper(NlsMapper.class);

        SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(new SqlSessionFactoryBuilder().build(componentConfiguration));
        ComponentSqlSessionManager componentSqlSessionManager = ComponentSqlSessionManager.newInstance(sqlSessionManager);
```

# Base

On part du principe qu'il y a un component bean :

```java
@ComponentBean
public interface ICountry extends IComponent {
    
}
```

## Liste des Annotations

- **@Entity** : Donne le nom de la table sur un component bean

| Propriété | Type | Description |
|-----------|------|---------|
| name      | String |  Obligatoire, nom de la table en base de donnée |

Exemple :

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
public interface ICountry extends IComponent {
```


- **@Cache** : Active le cache pour un component bean. Le cache sera automatiquement vidé dans les cas suivant : insert, update, delete et dés la modification d'un sous component (Association, Collection). Le cache est sensible au nls.

| Propriété | Type | Description |
|-----------|------|---------|
| readWrite      | boolean | Si false, la même instance de l'objet sera fournit par le cache sinon une nouvelle instance à chaque fois. Par défaut false |
| size      | int | Nombre d'objet gardé dans le cache. Par défaut 512 |
| clearInterval      | long | Temps de cache avec un nettoyage en milliseconde. Par défaut 1 heure (60 * 60 * 1000) |
| links      | Class<? extends IComponent>[] | Liste de component bean a lié à ce cache. Si les links sont modifié le cache est vidé |

Exemple : 

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
@Cache
public interface ICountry extends IComponent {
```

- **@Column** : Permet de déclarer une colonne par rapport à un champs. Elle doit être mise sur le getter uniquement.
 
| Propriété | Type | Description |
|-----------|------|---------|
| name      | String | Obligatoire. Nom de la colonne en base de donnée |
| javaType      | Class<?> | Type java de la colonne. Par défaut void.class, celui-ci est calculé par rapport au type de retour |
| jdbcType      | JdbcType | Type jdbc de la colonne. Par défaut JdbcType.UNDEFINED |
| typeHandler      | Class<? extends TypeHandler<?>> | TypeHandler pour la transformation jdbc. Par défaut UnknownTypeHandler.class |

Exemple : 

```java

    @Column(name = "CODE")
    String getCode();
    
    void setCode(String code);

```

- **@Id** : Permet de déclarer une colonne comme l'identifiant unique de la table. Il ne peut y en avoir qu'un. Il doit être couplé avec un **@Column**.

| Propriété | Type | Description |
|-----------|------|---------|
| keyGeneratorId      | String | Id d'un generateur de clef. Par défaut "". |
| keyGeneratorClass      | Class<? extends KeyGenerator> | Class d'un générateur de clef. Par defaut NoKeyGenerator.class. |

```java

    @Id
    @Column(name = "ID")
    String getId();
    
    void setId(String id);

```


- **@Version** : Permet de déclarer une colonne comme la version de l'objet. Il ne peut y en avoir qu'un. Il doit être couplé avec un **@Column**. Il doit être obligatoirement un `int` ou un `long`.

```java

    @Version
    @Column(name = "VERSION")
    int getVersion();
    
    void setVersion(int version);

```

- **@NlsColumn** : Permet de déclarer une colonne comme traduisible. Voir plus bas pour le paramétrage de INlsColumnHandler

| Propriété | Type | Description |
|-----------|------|---------|
| name      | String | Obligatoire. Nom de la colonne en base de donnée |
| javaType      | Class<?> | Type java de la colonne. Par défaut void.class, celui-ci est calculé par rapport au type de retour |
| jdbcType      | JdbcType | Type jdbc de la colonne. Par défaut JdbcType.UNDEFINED |
| typeHandler      | Class<? extends TypeHandler<?>> | TypeHandler pour la transformation jdbc. Par défaut UnknownTypeHandler.class |
| propertySource      | String[] | Liste des champs à retourner au select. Par défaut [], si vide, il utilise le champs noté @Id |
| select      | String | Namespace+id de la requete MyBatis. Par défaut "", si vide, il utilise la version dynamique à partir de INlsColumnHandler |
| fetchType      | FetchType | Type de chargement. Par défaut DEFAULT |

Exemple : 

```java

    @NlsColumn(name = "NAME")
    String getName();
    
    void setName(String name);

```

- **@Association** : Permet de faire une relation vers 1. Il faut que l'identifiant soit dans le component courant.

| Propriété | Type | Description |
|-----------|------|---------|
| propertySource      | String[] | Obligatoire. Nom du champs dans le component source, il peut y en avoir plusieurs |
| propertyTarget      | String[] | Nom du champs dans le component cible, il faut qu'il est le même nombre de champs que la source. Par défaut {}, l'identifiant unique de la cible sera pris |
| javaType      | Class<?> | Le type de l'association. Par defaut void.class, il sera pris sur le type de retour |
| select      | String | Namespace+id de la rêquete MyBatis. Par défaut "",  si vide, il utilise la version dynamique |
| fetchType      | FetchType | Type de chargement. Par défaut DEFAULT |
| joinTable      | JoinTable[] | Permet de définir des sous-jointures pour la rêquete dynamique. Voir plus bas |

```java

    @Column(name = "CONTINENT_ID")
    IId getContinentId();
    
    void setContinentId(IId continentId);

    @Association(propertySource = CountryFields.continentId)
    IContinent getContinent();
    
    void setContinent(IContinent continent);

```

- **@Collection** : Permet de faire une relation vers n.

| Propriété | Type | Description |
|-----------|------|---------|
| propertySource      | String[] | Nom du champs dans le component source, il peut y en avoir plusieurs. Par défaut {}, l'identifiant unique de la source sera pris |
| propertyTarget      | String[] | Obligatoire. Nom du champs dans le component cible, il faut qu'il est le même nombre de champs que la source |
| javaType      | Class<? extends java.util.Collection> | Le type de la collection. Par defaut Collection.class, il sera pris sur le type de retour |
| ofType      | Class<?> | Le type d'élément de la collection. Par defaut void.class, il sera pris sur le type de retour dans la collection |
| select      | String | Namespace+id de la rêquete MyBatis. Par défaut "",  si vide, il utilise la version dynamique |
| fetchType      | FetchType | Type de chargement. Par défaut DEFAULT |
| joinTable      | JoinTable[] | Permet de définir des sous-jointures pour la rêquete dynamique. Voir plus bas |
| orderBy      | OrderBy[] | Permet de définir un ordre de tri |

```java

    @Collection(propertyTarget = StateFields.countryId)
    List<IState> getStates();
    
    void setStates(List<IState> states);

```

### Explication de la jointure pour l'association et la collection.

Permet de faire des jointures de la source vers la cible. Il faut préciser le nom de la table et chaque colonne à gauche et à droite.

Exemple, si on doit faire 2 jointures pour avoir la liste des états à partir d'un pays : 

```java
@Collection(propertyTarget = AddressFields.id, { @JoinTable(name = "T_ASSO_COUNTRY_TOTO", left = "COUNTRY_ID", right = "TOTO_ID"), @JoinTable(name = "T_ASSO_TOTO_STATE", left = "TOTO_ID", right = "STATE_ID") })
```

cela donnera

```sql
select c.* from T_STATE c inner join T_ASSO_TOTO_STATE u1 on u1.STATE_ID = c.ID inner join T_ASSO_COUNTRY_TOTO u2 on u2.TOTO_ID = u1.TOTO_ID where u2.COUNTRY_ID = #{id}
```

### Exemple final

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
public interface ICountry extends IComponent {

    @Id
    @Column(name = "ID")
    String getId();
    
    void setId(String id);

    @Version
    @Column(name = "VERSION")
    int getVersion();
    
    void setVersion(int version);

    @Column(name = "CODE")
    String getCode();
    
    void setCode(String code);

    @NlsColumn(name = "NAME")
    String getName();
    
    void setName(String name);

    @Column(name = "CONTINENT_ID")
    IId getContinentId();
    
    void setContinentId(IId continentId);

    @Association(propertySource = CountryFields.continentId)
    IContinent getContinent();
    
    void setContinent(IContinent continent);

    @Collection(propertyTarget = StateFields.countryId, orderBy = @OrderBy(StateFields.code)
    List<IState> getStates();
    
    void setStates(List<IState> states);

}
```

## Component bean d'aide

Il existe des component bean d'aide.

- **IEntity** : Elle rajoute un `id` de type `IId` et une `version` de type `int`. L'identifiant est généré automatiquement à l'insertion à partir de `IdFactory`

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
public interface ICountry extends IEntity {

    @Column(name = "CODE")
    String getCode();
    
    void setCode(String code);

   ...
   
}
```

- **ITracable** : Rajoute des champs de création et de mise à jour par qui et quand. Un observeur rempli les champs à partir de la `TracableTriggerObserver`, elle prend en paramètre un `IUserByHandler`.

- **ICancelable** : Rajoute des champs d'annulation par qui et quand. Elle utilise le même observeur que pour les traces.

## Accés

**ComponentSqlSessionManager** fournit différentes fonctions :

- **FindById** : Recherche un component bean à partir de son identifiant unique

```java
componentSqlSessionManager.findById(ICountry.class, IdFactory.IdString.from("0"));
```

- **Insert** : Insert un component bean

```java
componentSqlSessionManager.insert(country);
```

- **Update** : Met à jour un component bean

```java
componentSqlSessionManager.update(country);
```

- **Delete** : Efface un component bean

```java
componentSqlSessionManager.delete(country);
```

# Avancé

Les différents clefs générées automatiquement :

## ResultMap

> ResultMapNameHelper

Donne le result map de l'objet en paramètre, ici com.monpackage.ICountry

- "com.monpackage.ICountry/resultMap"

## Cache

> CacheNameHelper

Donne le cache pour un objet, ici com.monpackage.ICountry

- "com.monpackage.ICountry/cache"

## MapperStatement

> StatementNameHelper

Recherche l'objet selon son Id

- "com.monpackage.ICountry/findById"

Recherche les objets selon une liste de propriétés

- "com.monpackage.ICountry/findComponentsBy?properties=id"

- "com.monpackage.ICountry/findComponentsBy?properties=code,version"

- "com.monpackage.ICountry/findComponentsBy?properties=id&ignoreCancel"

Recherche l'objet selon des jointures

- "com.monpackage.ICountry/findComponentsByJoinTable?sourceComponent=com.monpackage.IContinent&sourceProperties=id&targetProperties=id&join=t_continent_toto;continent_id;toto_id;#t_toto_country;toto_id;country_id"

- "com.monpackage.ICountry/findComponentsByJoinTable?sourceComponent=com.monpackage.IContinent&sourceProperties=id&targetProperties=id&join=t_continent_toto;continent_id;toto_id;#t_toto_country;toto_id;country_id&ignoreCancel"

Recherche la traduction d'une propriété de l'objet

- "com.monpackage.ICountry/findNlsColumn?property=name"

Insertion de l'objet (pas des fils)

- "com.monpackage.ICountry/insert"

Mise à jour de l'objet (pas des fils)

- "com.monpackage.ICountry/update"

Mise à jour de l'objet et force la mise à jour d'une propiété NLS

- "com.monpackage.ICountry/update?nlsProperties=name"

Efface l'objet ou l'annule si il est annulable

- "com.monpackage.ICountry/delete"

Efface l'objet selon son Id

- "com.monpackage.ICountry/deleteEntityById"

Efface les objets selon les propriétés

- "com.monpackage.ICountry/deleteComponentsBy?properties=code,version"

# RSQL

Rajouter dans la configuration

```java
IRsqlConfiguration rsqlConfiguration = RsqlConfigurationBuilder.newBuilder().stringPolicy(stringComparePolicy).nlsColumnRsqlHandler(defaultNlsColumnHandler).pageStatementFactory(new HSQLDBHandler()).build();

componentConfiguration.getMappedStatementFactoryRegistry().registry(new RsqlMappedStatementFactory(rsqlConfiguration));
componentConfiguration.getMappedStatementFactoryRegistry().registry(new CountRsqlMappedStatementFactory(rsqlConfiguration));
```

Utilisation simple :

```java
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==FRA");
List<IPerson> persons = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "firstName==L*,country.code==FR");

Integer count = sqlSession.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), "code==FRA");
```

Utilisation avec Request :

```java
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==FRA").build());
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("-code").build());
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("-code").rows(Request.Rows.of(1, 3)).build());

Integer count = sqlSession.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), Request.newBuilder().rsql("code==FRA").build());
```

Soit la requète simple un String "code==10" ou soit à partir de l'objet de la `Request`

| Propriété | Type | Description |
|-----------|------|---------|
| rsql      | String | La requète RSQL |
| customRequest      | ICustomRequest | Permet de rajouter un AND custom à la requète |
| sort      | String | La requète RSQL |
| customSortLeft      | ICustomSort | Permet de rajouter un tri custom à la requète, au début du order by|
| customSortRight      | ICustomSort | Permet de rajouter un tri custom à la requète, à la fin du ordere by |
| rows      | Rows | Limite la requète en nombre de ligne |

Plus d'info sur le Rsql : https://github.com/jirutka/rsql-parser

# Avancé

Les différents clefs générées automatiquement :

> RsqlStatementNameHelper

Renvoi la liste des objets filtré

- "com.monpackage.ICountry/rsql"

Compte le nombre d'objet

- "com.monpackage.ICountry/countRsql"