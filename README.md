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
        componentConfiguration.getResultMapFactoryRegistry().registry(new ComponentResultMapFactory());
        componentConfiguration.getCacheFactoryRegistry().registry(new ComponentCacheFactory());
        componentConfiguration.getTriggerDispatcher().addTriggerObserver(new TracableTriggerObserver(new DefaultUserByHandler()));
        componentConfiguration.getTypeHandlerRegistry().register(IdTypeHandler.class);

        componentConfiguration.addMapper(NlsMapper.class);

        SqlSessionManager sqlSessionManager = SqlSessionManager.newInstance(new SqlSessionFactoryBuilder().build(componentConfiguration));
        ComponentSqlSessionManager componentSqlSessionManager = ComponentSqlSessionManager.newInstance(sqlSessionManager);
```

# Utilisation

On part du principe qu'il y a un component bean :

```java
@ComponentBean
public interface ICountry extends IComponent {
    
}
```

## Liste des Annotations

- @Entity : Donne le nom de la table sur un component bean

| Propriété | Type | Description |
|-----------|------|---------|
| name      | String |  Obligatoire, nom de la table en base de donnée |

Exemple :

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
public interface ICountry extends IComponent {
```


- @Cache : Active le cache pour un component bean. Le cache sera automatiquement vidé dans les cas suivant : insert, update, delete et dés la modification d'un sous component (Association, Collection). Le cache est sensible au nls.

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

- @Column : Permet de déclarer une colonne par rapport à un champs. Elle doit être mise sur le getter uniquement.
 
| Propriété | Type | Description |
|-----------|------|---------|
| name      | String | Obligatoire. Nom de la colonne en base de donnée |
| javaType      | Class<?> | Type java de la colonne. Par défaut void.class, celui-ci est calculé par rapport au type de retour |
| jdbcType      | JdbcType | Type jdbc de la colonne. Par défaut JdbcType.UNDEFINED |
| typeHandler      | Class<? extends TypeHandler<?>> | TypeHandler pour la transformation jdbc. Par défaut UnknownTypeHandler.class |

Exemple : 

```java

    @Column(name = "NAME")
    String getName();
    
    void setName(String name);

```

- @NlsColumn
- @Id
- @Version
- @Association
- @Collection

## Accés

**ComponentSqlSessionManager** fournit différentes fonctions :

- FindById
- Insert
- Update
- Delete

### FindById

```java
componentSqlSessionManager.findById(ICountry.class, 0);
```