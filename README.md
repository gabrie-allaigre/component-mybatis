Simplifies the use of MyBatis with Component Bean https://github.com/gabrie-allaigre/component-bean

The overlay allows:

- Simply insert, update and delete a component bean
- Simplify the findById of a component bean
- 0 writing a ResultMap for component bean
- lazy loading of component bean
- Managing a legacy cache for component beans
- Management of multi-lingual on multiple fields in component bean
- Complex request generation, Rsql format, sorting and pagination (extension Rsql)

To use it, add the dependencies in the 'pom.xml' of the project:

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis</artifactId>
        <version>1.0.4</version>
    </dependency>
</dependencies>
```

With Guice

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis-guice</artifactId>
        <version>1.0.4</version>
    </dependency>
</dependencies>
```

Extension Rsql

```xml
<dependencies>
    <dependency>
        <groupId>com.talanlabs</groupId>
        <artifactId>component-mybatis-rsql</artifactId>
        <version>1.0.4</version>
    </dependency>
</dependencies>
```

# Usage

## With Guice (Simpler)

Example with an HSQLDB database:

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

It is assumed that there is a component bean:

```java
@ComponentBean
public interface ICountry extends IComponent {
    
}
```

## List of Annotations

- **@Entity **: Gives the name of the table on a component bean

| Property | Type | Description |
|-----------|------|---------|
| name      | String |  Mandatory, table name in database |

Example :

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
public interface ICountry extends IComponent {
```


- **@Cache** : Activates the cache for a component bean. The cache will be automatically emptied in the following instances: insert, update, delete, and after modifying a sub-component (Association, Collection). The cache is sensitive to nls.

| Propriété | Type | Description |
|-----------|------|---------|
| readWrite      | boolean | If false, the same instance of the object will be supplied by the cache if not a new instance each time. Default false |
| size      | int | Number of objects kept in the cache. Default 512 |
| clearInterval      | long | Cache time with a cleaning in millisecond. Default 1 hour (60 * 60 * 1000) |
| links      | Class<? extends IComponent>[] | List of component bean linked to this cache. If the links are modified the cache is emptied |

Example : 

```java
@ComponentBean
@Entity(name = "T_COUNTRY")
@Cache
public interface ICountry extends IComponent {
```

- **@Column** : Declare a column relative to a field. It must be put on the getter only.
 
| Propriété | Type | Description |
|-----------|------|---------|
| name      | String | Mandatory. Name of column in database |
| javaType      | Class<?> | Java type of the column. By default void.class, this is calculated relative to the return type |
| jdbcType      | JdbcType | The jdbc type of the column. Default JdbcType.UNDEFINED |
| typeHandler      | Class<? extends TypeHandler<?>> | TypeHandler for the jdbc transformation. Default UnknownTypeHandler.class |

Example : 

```java

    @Column(name = "CODE")
    String getCode();
    
    void setCode(String code);

```

- **@Id** : Declare a column as the unique identifier of the table. There can only be one. It must be paired with a **@Column**.

| Propriété | Type | Description |
|-----------|------|---------|
| keyGeneratorId      | String | Id of a key generator. By default "". |
| keyGeneratorClass      | Class<? extends KeyGenerator> | Class of a key generator. By default, NoKeyGenerator.class. |

```java

    @Id
    @Column(name = "ID")
    String getId();
    
    void setId(String id);

```


- **@Version** : Declare a column as the version of the object. There can only be one. It must be paired with a **@Column**. It must be an `int` or` long`.

Example : 

```java

    @Version
    @Column(name = "VERSION")
    int getVersion();
    
    void setVersion(int version);

```

- **@NlsColumn** : Declare a column as translatable. See below for setting INlsColumnHandler

| Propriété | Type | Description |
|-----------|------|---------|
| name      | String | Mandatory. Name of column in database |
| javaType      | Class<?> | Java type of the column. By default void.class, this is calculated relative to the return type |
| jdbcType      | JdbcType | The jdbc type of the column. Default JdbcType.UNDEFINED |
| typeHandler      | Class<? extends TypeHandler<?>> | TypeHandler for the jdbc transformation. Default UnknownTypeHandler.class |
| propertySource      | String[] | List of fields to return to the select. By default [], if empty, it uses the field marked @Id |
| select      | String | Namespace + id of the MyBatis request. By default "", if empty, it uses the dynamic version from INlsColumnHandler |
| fetchType      | FetchType | Type of loading. Default DEFAULT |

Example : 

```java

    @NlsColumn(name = "NAME")
    String getName();
    
    void setName(String name);

```

- **@Association** : Allows to make a relation to 1. The identifier must be in the current component.

| Propriété | Type | Description |
|-----------|------|---------|
| propertySource      | String[] | Mandatory. Name of the field in the source component, there may be more than one |
| propertyTarget      | String[] | Name of the field in the target component, it must be the same number of fields as the source. By default [], the unique identifier of the target will be taken |
| javaType      | Class<?> | The type of association. By default void.class, it will be taken on the return type |
| select      | String | Namespace + id of the MyBatis dream. By default "", if empty, it uses the dynamic |
| fetchType      | FetchType | Type of loading. Default DEFAULT |
| joinTable      | JoinTable[] | Lets you define subjoins for the dynamic query. See below |

Example : 

```java

    @Column(name = "CONTINENT_ID")
    IId getContinentId();
    
    void setContinentId(IId continentId);

    @Association(propertySource = CountryFields.continentId)
    IContinent getContinent();
    
    void setContinent(IContinent continent);

```

- **@Collection** : Allows to make a relation towards n.

| Propriété | Type | Description |
|-----------|------|---------|
| propertySource      | String[] | Name of the field in the source component, there may be more than one. By default [], the unique identifier of the source will be taken |
| propertyTarget      | String[] | Mandatory. Name of the field in the target component, it must be the same number of fields as the source |
| javaType      | Class<? extends java.util.Collection> | The type of the collection. By default Collection.class, it will be taken on the return type |
| ofType      | Class<?> | The type of item in the collection. By default, void.class, it will be taken on the return type in the collection |
| select      | String | Namespace + id of the MyBatis dream. By default "", if empty, it uses the dynamic |
| fetchType      | FetchType | Type of loading. Default DEFAULT |
| joinTable      | JoinTable[] | Lets you define subjoins for the dynamic query. See below |
| orderBy      | OrderBy[] | Lets you define a sort order |

Example :

```java

    @Collection(propertyTarget = StateFields.countryId)
    List<IState> getStates();
    
    void setStates(List<IState> states);

```

### Explanation of the join for association and collection.

Joins the source to the target. The name of the table must be specified and each column must be left and right.

Example, if you need to do 2 joins to get the list of states from a country:

```java
@Collection(propertyTarget = AddressFields.id, { @JoinTable(name = "T_ASSO_COUNTRY_TOTO", left = "COUNTRY_ID", right = "TOTO_ID"), @JoinTable(name = "T_ASSO_TOTO_STATE", left = "TOTO_ID", right = "STATE_ID") })
```

This will give

```sql
select c.* from T_STATE c inner join T_ASSO_TOTO_STATE u1 on u1.STATE_ID = c.ID inner join T_ASSO_COUNTRY_TOTO u2 on u2.TOTO_ID = u1.TOTO_ID where u2.COUNTRY_ID = #{id}
```

### Final example

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

## Component bean help

There are component help beans.

- **IEntity** : It adds an `id` of type` IId` and a `version` of type` int`. The identifier is generated automatically on insertion from `IdFactory`

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

- **ITracable** : Adds creation and update fields by who and when. An observer completes the fields from the `TracableTriggerObserver`, it takes a` IUserByHandler` parameter.

- **ICancelable** : Adds cancellation fields by who and when. It uses the same observer as for the traces.

## Access

**ComponentSqlSessionManager** provides various functions:

- **FindById** : Search for a component bean from its unique identifier

```java
componentSqlSessionManager.findById(ICountry.class, IdFactory.IdString.from("0"));
```

- **Insert** : Inserts a component bean

```java
componentSqlSessionManager.insert(country);
```

- **Update** : Updates a component bean

```java
componentSqlSessionManager.update(country);
```

- **Delete** : Deletes a component bean

```java
componentSqlSessionManager.delete(country);
```

# Advanced

The various keys generated automatically:

## ResultMap

> ResultMapNameHelper

Gives the result map of the object as a parameter, here com.monpackage.ICountry

- "com.monpackage.ICountry/resultMap"

## Cache

> CacheNameHelper

Gives the cache for an object, here com.monpackage.ICountry

- "com.monpackage.ICountry/cache"

## MapperStatement

> StatementNameHelper

Search the object according to its Id

- "com.monpackage.ICountry/findById"

Find objects according to a list of properties

- "com.monpackage.ICountry/findComponentsBy?properties=id"

- "com.monpackage.ICountry/findComponentsBy?properties=code,version"

- "com.monpackage.ICountry/findComponentsBy?properties=id&ignoreCancel"

Finds the object according to joins

- "com.monpackage.ICountry/findComponentsByJoinTable?sourceComponent=com.monpackage.IContinent&sourceProperties=id&targetProperties=id&join=t_continent_toto;continent_id;toto_id;#t_toto_country;toto_id;country_id"

- "com.monpackage.ICountry/findComponentsByJoinTable?sourceComponent=com.monpackage.IContinent&sourceProperties=id&targetProperties=id&join=t_continent_toto;continent_id;toto_id;#t_toto_country;toto_id;country_id&ignoreCancel"

Search translation of a property of the object

- "com.monpackage.ICountry/findNlsColumn?property=name"

Inserting the object (not children)

- "com.monpackage.ICountry/insert"

Updating the object (not children)

- "com.monpackage.ICountry/update"

Updating the object and force updating an NLS property

- "com.monpackage.ICountry/update?nlsProperties=name"

Clears the object or cancels it if it is cancelable

- "com.monpackage.ICountry/delete"

Clears the object according to its Id

- "com.monpackage.ICountry/deleteEntityById"

Clears objects according to properties

- "com.monpackage.ICountry/deleteComponentsBy?properties=code,version"

# RSQL

Add to the configuration

```java
IRsqlConfiguration rsqlConfiguration = RsqlConfigurationBuilder.newBuilder().stringPolicy(stringComparePolicy).nlsColumnRsqlHandler(defaultNlsColumnHandler).pageStatementFactory(new HSQLDBHandler()).build();

componentConfiguration.getMappedStatementFactoryRegistry().registry(new RsqlMappedStatementFactory(rsqlConfiguration));
componentConfiguration.getMappedStatementFactoryRegistry().registry(new CountRsqlMappedStatementFactory(rsqlConfiguration));
```

Easy to use:

```java
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), "code==FRA");
List<IPerson> persons = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(IPerson.class), "firstName==L*,country.code==FR");

Integer count = sqlSession.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), "code==FRA");
```

Use with Request:

```java
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==FRA").build());
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("-code").build());
List<ICountry> countries = sqlSession.selectList(RsqlStatementNameHelper.buildRsqlKey(ICountry.class), Request.newBuilder().rsql("code==E*").sort("-code").rows(Request.Rows.of(1, 3)).build());

Integer count = sqlSession.selectOne(RsqlStatementNameHelper.buildCountRsqlKey(ICountry.class), Request.newBuilder().rsql("code==FRA").build());
```

Let the simple request be a String "code == 10" or be from the object of the `Request`

| Propriété | Type | Description |
|-----------|------|---------|
| rsql      | String | The request RSQL |
| customRequest      | ICustomRequest | Allows to add a custom AND to the request |
| sort      | String | Sorting |
| customSortLeft      | ICustomSort | Allows to add a custom sort to the request, at the beginning of order by |
| customSortRight      | ICustomSort | Allows to add a custom sort to the request, at the end of the order by |
| rows      | Rows | Limit the request in line number |

More info on the Rsql: https://github.com/jirutka/rsql-parser and RsqlBuilder https://github.com/gabrie-allaigre/rsql-builder

# Avancé

The various keys generated automatically:

> RsqlStatementNameHelper

Returns the list of filtered objects

- "com.monpackage.ICountry/rsql"

Counts the number of objects

- "com.monpackage.ICountry/countRsql"