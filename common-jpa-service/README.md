# common-jpa-service

Core runtime module of the dev-persistence framework. Provides the service abstractions, dynamic query building engine, and reflection-based filter binding that all other modules depend on.

---

## Table of Contents

1. [Overview](#overview)
2. [Configuration](#configuration)
3. [Service Layer](#service-layer)
   - [JpaService interface](#jpaservice-interface)
   - [Extending JpaServiceImpl](#extending-jpaserviceimpl)
4. [Filter Objects](#filter-objects)
   - [BaseParameter](#baseparameter)
   - [IDFilterParameter](#idfilterparameter)
   - [Filter Annotations](#filter-annotations)
5. [QueryParameter](#queryparameter)
   - [TupleParameter and @TupleComparison](#tupleparameter-and-tuplecomparison)
6. [Native SQL Queries](#native-sql-queries)
   - [NativeQueryParameter](#nativequeryparameter)
   - [Zone-based conditions](#zone-based-conditions)
7. [QueryJpql](#queryjpql)
8. [Custom Row Mapping](#custom-row-mapping)
   - [JpaRowMapper](#jparowmapper)
   - [JdbcRowMapper](#jdbcrowmapper)
   - [ResultMapper and @ResultMapping](#resultmapper-and-resultmapping)
   - [@IgnoreResultSet](#ignoreresultset)
9. [JdbcTemplateService](#jdbctemplateservice)
10. [Abstract Search Controllers](#abstract-search-controllers)
    - [BaseSearchController](#basesearchcontroller)
    - [PerformanceSearchController](#performancesearchcontroller)
    - [ModelMapper and PerformanceModelMapper](#modelmapper-and-performancemodelmapper)
11. [Real-world examples](#real-world-examples)
    - [Service with complex conditions and cross-relationship navigation](#service-with-complex-conditions-and-cross-relationship-navigation)
    - [Service with native SQL zone conditions](#service-with-native-sql-zone-conditions)
    - [Filter with date ranges, LIKE, and zone annotations](#filter-with-date-ranges-like-and-zone-annotations)
    - [Manual QueryParameter for programmatic queries](#manual-queryparameter-for-programmatic-queries)

---

## Overview

`common-jpa-service` replaces the typical pattern of writing boilerplate query methods for each entity. Instead, you define a typed filter object whose non-null fields are automatically translated into JPQL WHERE conditions at runtime. The JPQL query strings themselves are pre-built at compile time by the `processor-jpa-service` annotation processor and stored in a generated `QueryJpqlImpl` class.

At runtime the call chain is:

```
YourService.findByFilter(queryParameter)
  → JpaServiceImpl.findByFilter()
  → BaseJpaService (builds dynamic JPQL / native SQL)
  → QueryJpqlImpl (provides pre-built strings and condition maps)
  → EntityManager / JpaRepository
```

---

## Configuration

Enable the framework in your Spring Boot application:

```java
@SpringBootApplication
@EnableJpaService
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`@EnableJpaService` imports `EnableJpaServiceConfiguration`, which registers the `ReflectionCommons` bean used internally for parameter introspection.

---

## Service Layer

### JpaService interface

`JpaService<T, ID>` is the main interface your service interfaces extend. It provides:

**CRUD**

| Method | Description |
|--------|-------------|
| `save(T entity)` | Insert or update |
| `update(T entity)` | Update existing entity |
| `delete(T entity)` | Delete one entity |
| `deleteAll(Collection<T>)` | Bulk delete |
| `findById(ID id)` | Find by primary key |
| `findAll()` | Return all rows |
| `count()` | Total row count |
| `existsById(ID id)` | Check existence |

**Filter-based queries**

| Method | Description |
|--------|-------------|
| `findByFilter(QueryParameter<T,ID>)` | List of entities matching filter |
| `countByFilter(QueryParameter<T,ID>)` | Count matching entities |
| `deleteByFilter(QueryParameter<T,ID>)` | Delete matching entities |
| `singleResultByFilter(QueryParameter<T,ID>)` | Single entity (exception if multiple) |
| `mapFindByFilter(QueryParameter<T,ID>)` | Results as `PersistenceMap<K,V>` |
| `mapKeyFindByFilter(QueryParameter<T,ID>)` | Results keyed by a field |
| `mapKeyListFindByFilter(QueryParameter<T,ID>)` | Results grouped by a key field |

The `key` parameter used by `mapKeyFindByFilter` and `mapKeyListFindByFilter` is a
**dot-notation field path** resolved via reflection — it can traverse relationships:

```java
QueryParameter<Product, Long> qp = new QueryParameter<>();
qp.addParameter("active", true);

// Keyed by the entity's own primary key
PersistenceMap<Long, Product> byId = productService.mapFindByFilter(qp);

// Keyed by a related field: Product → category → categoryId
PersistenceMap<Long, Product> byCategory =
    productService.mapKeyFindByFilter(qp, Long.class, "category.categoryId");
// If multiple products share the same key, the last one wins.

// Grouped by the same field
PersistenceMap<Long, List<Product>> grouped =
    productService.mapKeyListFindByFilter(qp, Long.class, "category.categoryId");
```

**Native SQL queries**

| Method | Description |
|--------|-------------|
| `findNativeByFilter(NativeQueryParameter<K,ID>)` | List mapped from native SQL Tuple results |
| `findNativeByFilter(NativeQueryParameter<K,ID>, JpaRowMapper<K>)` | List via custom row mapper |

### Extending JpaServiceImpl

Concrete service classes extend `JpaServiceImpl<T, ID>` and implement two abstract methods:

```java
@Service
@Transactional
@QueryBuilder(
    distinct = true,
    conditions = {
        @ConditionBuilder(field = "product.name", operation = OperationType.LIKE, parameter = "name",
                          upperLower = UpperLowerType.LOWER),
        @ConditionBuilder(field = "product.active", operation = OperationType.EQUAL, parameter = "active")
    },
    jpaOrder = {
        @JpqlOrderBuilder(key = "name", order = "product.name")
    }
)
public class ProductServiceImpl
        extends JpaServiceImpl<Product, Long>
        implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected JpaRepository<Product, Long> getJpaRepository() {
        return productRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
```

The annotation processor reads `@QueryBuilder` and generates `ProductQueryJpqlImpl` at compile time. Spring autowires it into `JpaServiceImpl` as the `QueryJpql<Product>` bean.

---

## Filter Objects

### BaseParameter

Create a filter DTO by extending `BaseParameter`. Any non-null field is automatically added to the WHERE clause at runtime.

```java
public class ProductFilter extends BaseParameter {

    private String name;

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String description;

    @DateFilter(addDay = 1)
    private Date expiresAfter;

    @ConditionTrigger
    private Boolean deletedAtIsNull;

    // getters / setters
}
```

Usage:

```java
ProductFilter filter = new ProductFilter();
filter.setName("Widget");
filter.addOrderBy("name", OrderType.ASC);
filter.setPageSize(20);
filter.setPageNumber(0);

QueryParameter<Product, Long> qp = new QueryParameter<>(filter);
List<Product> results = productService.findByFilter(qp);
```

Built-in fields (annotated with `@IgnoreMapping` so they are not treated as query conditions):

| Field | Description |
|-------|-------------|
| `pageSize` | Number of results per page |
| `pageNumber` | Zero-based page number |
| `orderBy` | List of `OrderBy(sortKey, OrderType)` items |

### IDFilterParameter

`IDFilterParameter<ID>` extends `BaseParameter` and adds a typed `List<ID> id` field that maps to an `IN (...)` condition on the entity's primary key. It is the standard base class when you need to filter by a list of IDs alongside other criteria.

Override the JSON property name to match your domain naming:

```java
public class ApplicationFilter extends IDFilterParameter<Integer> {

    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    private String name;

    private List<Integer> idEnvironment;
    private List<Integer> idProject;

    // Expose the inherited 'id' field under the domain name in JSON
    @Override
    @JsonProperty("idApplication")
    public List<Integer> getId() { return super.getId(); }

    @Override
    @JsonProperty("idApplication")
    public void setId(List<Integer> id) { super.setId(id); }

    // Convenience method
    public void addIdApplication(Integer... ids) {
        if (ids != null) this.getId().addAll(Arrays.asList(ids));
    }
}
```

### Filter Annotations

#### @DateFilter

Shifts a date/time value by a configurable offset before binding it as a parameter. The most common use case is implementing from/to date range filters.

Supported types: `Date`, `Calendar`, `Timestamp`, `Instant`, `LocalDate`, `LocalDateTime`, `OffsetDateTime`.

```java
public class ExceptionAuditFilter extends IDFilterParameter<Integer> {

    @DateFilter           // no offset — used as lower bound (>=)
    private Calendar updateTimeFrom;

    @DateFilter(addDay = 1)  // adds 1 day — used as upper bound (<) making the range inclusive
    private Calendar updateTimeTo;

    @DateFilter
    private Calendar createTimeFrom;

    @DateFilter(addDay = 1)
    private Calendar createTimeTo;
}
```

Parameters: `addYear`, `addMonth`, `addWeek`, `addDay`, `addHour`, `addMinute`, `addSecond` (all default 0, negative values subtract).

The matching `@ConditionBuilder` entries on the service use `OperationType.GREATER_EQUAL` / `OperationType.LESS` to produce the range:

```java
@ConditionBuilder(field = "entity.updateTime", operation = OperationType.GREATER_EQUAL, parameter = "updateTimeFrom"),
@ConditionBuilder(field = "entity.updateTime", operation = OperationType.LESS,          parameter = "updateTimeTo"),
```

#### @LikeString

Wraps a `String` value in SQL `LIKE` wildcards.

```java
@LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
private String lastName;
// generates: AND LOWER(e.lastName) LIKE LOWER(:lastName)  →  '%smith%'

@LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
private String name;
// generates: AND UPPER(e.name) LIKE :name  (no wildcards — caller supplies them if needed)
```

`LikeType` values: `LEFT` (`%value`), `RIGHT` (`value%`), `LEFT_RIGHT` (`%value%`), `NONE` (no wildcard added).
`UpperLowerType` values: `NONE`, `UPPER`, `LOWER`.

#### @ConditionTrigger

Marks a `Boolean` field as a trigger for a value-less JPQL condition (`IS NULL` / `IS NOT NULL`). When the field is `true`, the engine calls `QueryParameter.addNullable(fieldName)`, activating the matching `@ConditionBuilder` that uses `OperationType.IS_NULL` or `OperationType.IS_NOT_NULL` (no parameter value is bound). When the field is `false` or `null`, the condition is skipped.

```java
public class ServiceRestFilter extends BaseParameter {

    @ConditionTrigger
    private Boolean httpMethodIsNull;     // true → AND e.httpMethod IS NULL

    @ConditionTrigger
    private Boolean httpMethodIsNotNull;  // true → AND e.httpMethod IS NOT NULL
}
```

Service side:

```java
@QueryBuilder(
    conditions = {
        @ConditionBuilder(field = "e.httpMethod", operation = OperationType.IS_NULL,     parameter = "httpMethodIsNull"),
        @ConditionBuilder(field = "e.httpMethod", operation = OperationType.IS_NOT_NULL, parameter = "httpMethodIsNotNull")
    }
)
```

`Collection` / array fields are mapped to `IN` conditions automatically when the matching `@ConditionBuilder` uses `OperationType.IN` or `OperationType.NOT_IN` — no annotation required.

#### @FieldMapping

Overrides the default JPQL parameter name (which defaults to the Java field name).

```java
@FieldMapping("firstName")
private String userFirstName;
// JPQL uses :firstName instead of :userFirstName
```

#### @IgnoreMapping

Prevents a field from being added to the parameter map. Used on fields that should not generate WHERE conditions (e.g., display-only fields, flags set programmatically by hooks).

#### @FilterNullValue

Forces a field to be included in the WHERE clause even when its value is `null` (generates an IS NULL condition).

#### @ConditionsZone / @ConditionsZones

Routes a field's value to a named zone in a native SQL template. Zones allow selective activation of WHERE blocks in native queries:

```java
@LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
@ConditionsZones(@ConditionsZone(key = "appCondition"))
private String email;
// This field is only added when the "appCondition" zone is referenced in the native query
```

Multiple zones can be specified:

```java
@ConditionsZones({
    @ConditionsZone(key = "zone1"),
    @ConditionsZone(key = "zone2")
})
private String value;
```

---

## QueryParameter

`QueryParameter<T, ID>` is the main input object for filter-based queries. There are two ways to populate it:

**Via a typed filter object:**

```java
QueryParameter<Product, Long> qp = new QueryParameter<>(filter);
```

The framework introspects the `BaseParameter` subclass via reflection and extracts all non-null fields into the internal parameter map.

**Manually:**

```java
QueryParameter<Product, Long> qp = new QueryParameter<>();
qp.addParameter("categoryId", 5L);
qp.addNullable("deletedAt");  // always append: AND e.deletedAt IS NULL
```

`addNullable(name)` always appends the condition regardless of value — useful for IS NULL checks.

**Manual `QueryParameter` is the standard pattern inside hook implementations:**

```java
// Inside an AfterFind hook — build a separate query to load related data
QueryParameter<ExAssUserRole, ExAssUserRolePK> queryUserRole = new QueryParameter<>();
queryUserRole.addParameter(ExAssUserRoleQueryJpql.idUser, response.getData().getId());
ExAssUserRole userRole = exAssUserRoleService.singleResultByFilter(queryUserRole);
```

---

## Native SQL Queries

For queries that cannot be expressed in JPQL (vendor-specific syntax, complex joins, tuple comparisons), use native SQL with `NativeQueryParameter`.

Define the native query in a `.sql` file (or inline string) and use `${zoneName}` placeholders for dynamic WHERE blocks:

```sql
SELECT p.id, p.name, p.price
FROM product p
${mainZone}
ORDER BY p.name
```

```java
NativeQueryParameter<ProductDto, Long> nqp = new NativeQueryParameter<>(ProductDto.class);
nqp.addCondition("mainZone", "active", "AND p.active = :active", true);
nqp.addParameter("active", true);

List<ProductDto> results = productService.findNativeByFilter(nqp);
```

### NativeQueryParameter

| Method | Description |
|--------|-------------|
| `addCondition(zone, param, condition, value)` | Add a condition to a named zone |
| `addParameter(name, value)` | Add a raw named parameter |
| `setPageSize(int)` | Pagination |
| `setPageNumber(int)` | Pagination |
| `addOrderBy(sortKey, OrderType)` | Sorting |

### Zone-based conditions

Zones allow the same SQL template to have multiple independently activated WHERE blocks. Each zone corresponds to a `${zoneName}` placeholder in the SQL template. A condition bound to a zone is only injected when the corresponding parameter is non-null **and** the query references that zone.

#### Complete end-to-end example

**Step 1 — Native SQL template with zone placeholder (defined as a constant on the service interface)**

```java
public interface ExApplicationService extends JpaService<ExApplication, Integer> {

    String NAME_APPLICATION =
        "select distinct ea.name\n" +
        "from ex_application ea \n" +
        "join ex_project ep on ea.id_project = ep.id_project \n" +
        "left join (ex_ass_project_user eapu\n" +
        "  join ex_user eu on eu.id_user = eapu.id_user) on ep.id_project = eapu.id_project\n" +
        "${appCondition}";  // zone placeholder — replaced with conditions at runtime
}
```

When `email` is present the zone expands to `and upper(eu.email) = :email`. When absent the zone is removed entirely, returning all applications.

**Step 2 — `@QueryBuilder` with `customNativeConditions` binding the SQL fragment to the zone**

```java
@Service
@Transactional
@QueryBuilder(
    customNativeConditions = {
        @CustomConditionBuilder(
            condition = " and upper(eu.email) = :email ",  // raw SQL injected into the zone
            parameter = "email",                           // activates only when 'email' is non-null
            keys = "appCondition"                          // targets this specific zone
        )
    },
    nativeOrder = {
        @NativeOrderBuilder(key = "name", order = "ea.name")
    }
)
public class ExApplicationServiceImpl
        extends JpaServiceImpl<ExApplication, Integer>
        implements ExApplicationService {
    // ...
}
```

**Step 3a — Filter class with `@ConditionsZones` on a field**

When the zone parameter comes from a filter body field, annotate the field with `@ConditionsZones`:

```java
public class ApplicationFilter extends IDFilterParameter<Integer> {

    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    @ConditionsZones(@ConditionsZone(key = "appCondition"))
    private String email;
    // Routed to "appCondition" zone — not used as a standard JPQL condition
}
```

**Step 3b — Controller method parameter with `@ConditionsZones`**

When the zone parameter comes from a method argument (e.g., `@AuthenticationPrincipal`), annotate the parameter directly:

```java
@PostMapping("/grant/all/names")
@ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
CollectionResponse<ApplicationModel> searchName(
    @AuthenticationPrincipal
    @Param("email")                                          // bind as parameter named "email"
    @ConditionsZones(@ConditionsZone(key = "appCondition"))  // route to the appCondition zone
    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    ExUserSecurity exUserSecurity);
```

The framework extracts the authenticated user's `email` field, routes it to zone `appCondition`, and the SQL becomes:

```sql
select distinct ea.name
from ex_application ea
join ex_project ep on ea.id_project = ep.id_project
left join (ex_ass_project_user eapu
  join ex_user eu on eu.id_user = eapu.id_user) on ep.id_project = eapu.id_project
and upper(eu.email) = :email
```

If `email` is null the `${appCondition}` placeholder is removed and all rows are returned.

`initWhere = true` (default on `@ConditionsZone`) tells the framework to prepend `WHERE` or `AND` before the first condition in the zone automatically.

---

## QueryJpql

`QueryJpql<T>` is the abstract holder for all pre-built JPQL/SQL strings for a specific entity. You never create subclasses manually — they are generated by `processor-jpa-service` at compile time from the `@QueryBuilder` annotation on the `*ServiceImpl` class.

For each `@QueryBuilder`-annotated class the processor generates **two files**:

| Generated file | Purpose |
|---|---|
| `*QueryJpql.java` | Interface with `public static final String` constants for every parameter and sort key name |
| `*QueryJpqlImpl.java` | `@Component` that extends `QueryJpql<T>`, implements the interface, and holds all pre-built strings and maps |

---

### What gets generated

Given this `@QueryBuilder` on `ExApplicationServiceImpl`:

```java
@QueryBuilder(
    conditions = {
        @ConditionBuilder(field = "exApplication.name",
                          operation = OperationType.IN, parameter = "applicationsName"),
        @ConditionBuilder(field = "exApplication.exProject.idProject",
                          operation = OperationType.IN, parameter = "idProjectGrant"),
        @ConditionBuilder(field = "exApplication.name",
                          operation = OperationType.NOT_IN, parameter = "notInName"),
        @ConditionBuilder(field = "exApplication.exProject.idProject",
                          operation = OperationType.IN, parameter = "idProject"),
        @ConditionBuilder(field = "exApplication.exEnvironment.idEnvironment",
                          operation = OperationType.IN, parameter = "idEnvironment"),
    },
    jpaOrder = {
        @JpqlOrderBuilder(key = "desApplicationType",
                          order = "exApplication.exApplicationType.desApplicationType")
    },
    customNativeConditions = {
        @CustomConditionBuilder(condition = " and upper(eu.email)=:email ",
                                parameter = "email", keys = "appCondition")
    },
    nativeOrder = {
        @NativeOrderBuilder(key = "name", order = "ea.name")
    }
)
public class ExApplicationServiceImpl extends JpaServiceImpl<ExApplication, Integer> ...
```

The processor produces:

#### `ExApplicationQueryJpql.java` — constants interface

```java
public interface ExApplicationQueryJpql {

    // One constant per @ConditionBuilder parameter + base fields from the entity
    String applicationsName   = "applicationsName";
    String idProjectGrant     = "idProjectGrant";
    String notInName          = "notInName";
    String idProject          = "idProject";
    String idEnvironment      = "idEnvironment";
    String name               = "name";
    String email              = "email";
    String id                 = "id";
    String idApplication      = "idApplication";
    String updateTime         = "updateTime";
    String updateTimeFrom     = "updateTimeFrom";
    String updateTimeTo       = "updateTimeTo";
    String createTime         = "createTime";
    String createTimeFrom     = "createTimeFrom";
    String createTimeTo       = "createTimeTo";
    // ...

    // Sort key constants (prefixed ord_)
    String ord_desApplicationType = "desApplicationType";
    String ord_name               = "name";
}
```

These constants are used in hook implementations to avoid hardcoded strings:

```java
// In GrantHandlerFindRequest:
parameters.addParameter(ExApplicationQueryJpql.idProjectGrant, projectIds);
//                       ↑ same string that appears in the generated WHERE condition
```

#### `ExApplicationQueryJpqlImpl.java` — implementation

```java
@Component
public class ExApplicationQueryJpqlImpl
        extends QueryJpql<ExApplication>
        implements ExApplicationQueryJpql {

    // ── 1. Static JPQL base strings ─────────────────────────────────────────

    // FROM clause with all mandatory join fetches (derived from the entity's @ManyToOne fields)
    private static final String FROM_BY_FILTER =
        " From ExApplication exApplication " +
        " join fetch exApplication.exProject exProject " +
        " join fetch exApplication.exEnvironment exEnvironment " +
        " join fetch exApplication.exApplicationType exApplicationType ";

    private static final String SELECT_BY_FILTER  = "select distinct exApplication" + FROM_BY_FILTER;
    private static final String COUNT_BY_FILTER   = "select distinct count(exApplication)" + FROM_BY_FILTER;
    private static final String SELECT_ID_BY_FILTER = "select distinct exApplication.idApplication " + FROM_BY_FILTER;
    private static final String DELETE_BY_FILTER  = "delete from ExApplication exApplication ";

    // ── 2. MAP_CONDITIONS — JPQL SELECT/COUNT conditions ────────────────────
    // Key   = parameter name (matches @ConditionBuilder.parameter)
    // Value = JPQL condition fragment appended to the WHERE clause when the parameter is non-null

    private static final Map<String,String> MAP_CONDITIONS = getMapConditions();

    private static Map<String,String> getMapConditions() {
        Map<String,String> map = getMapBaseConditions();   // fields from the entity itself
        // Fields from @ConditionBuilder — navigating through relationships via join-fetched aliases:
        map.put(applicationsName,    " and ((exApplication.name)  in (:applicationsName) )");
        map.put(notInName,           " and ((exApplication.name)  not in (:notInName) )");
        map.put(idProject,           " and (exProject.idProject  in (:idProject) )");
        map.put(idEnvironment,       " and (exEnvironment.idEnvironment  in (:idEnvironment) )");
        map.put(idProjectGrant,      " and (exProject.idProject  in (:idProjectGrant) )");
        // ...
        return map;
    }

    // Base conditions — automatically generated from entity fields + IDFilterParameter
    private static Map<String,String> getMapBaseConditions() {
        Map<String,String> map = new HashMap<>();
        map.put(name,            " and upper(exApplication.name) like :name ");
        map.put(id,              " and exApplication.idApplication in (:id) ");
        map.put(idApplication,   " and exApplication.idApplication in (:idApplication) ");
        map.put(updateTimeFrom,  " and :updateTimeFrom<=exApplication.updateTime ");
        map.put(updateTimeTo,    " and exApplication.updateTime<=:updateTimeTo ");
        map.put(createTimeFrom,  " and :createTimeFrom<=exApplication.createTime ");
        map.put(createTimeTo,    " and exApplication.createTime<=:createTimeTo ");
        // ...
        return map;
    }

    // ── 3. MAP_DELETE_CONDITIONS — JPQL DELETE conditions ───────────────────
    // Identical structure to MAP_CONDITIONS but without join-fetched aliases
    // (DELETE queries cannot use join fetch, so relationships are navigated inline)

    private static final Map<String,String> MAP_DELETE_CONDITIONS = getMapDeleteConditions();

    private static Map<String,String> getMapDeleteConditions() {
        Map<String,String> map = getMapBaseConditions();
        map.put(idProject,      " and (exApplication.exProject.idProject  in (:idProject) )");
        map.put(idProjectGrant, " and (exApplication.exProject.idProject  in (:idProjectGrant) )");
        // notice the full path: exApplication.exProject.idProject vs exProject.idProject in SELECT
        // ...
        return map;
    }

    // ── 4. MAP_NATIVE_CONDITIONS — per-zone native SQL conditions ────────────
    // Outer key = zone name (matches @CustomConditionBuilder.keys)
    // Inner map = parameter name → SQL fragment injected into that zone

    private static final Map<String,Map<String,String>> MAP_NATIVE_CONDITIONS = getMapNativeConditions();

    private static Map<String,Map<String,String>> getMapNativeConditions() {
        Map<String,Map<String,String>> map = new HashMap<>();
        map.put("appCondition", getAppCondition());   // one entry per zone
        return map;
    }

    private static Map<String,String> getAppCondition() {
        Map<String,String> map = new HashMap<>();
        map.put(email, " and upper(eu.email)=:email ");  // SQL fragment for this parameter
        return map;
    }

    // ── 5. MAP_NATIVE_ORDERS — native SQL sort keys ──────────────────────────
    // Key   = sort key string from @NativeOrderBuilder.key
    // Value = SQL column expression used in ORDER BY

    private static final Map<String,String> MAP_NATIVE_ORDERS = getMapNativeOrders();

    private static Map<String,String> getMapNativeOrders() {
        Map<String,String> map = new HashMap<>();
        map.put(ord_name, " ea.name ");
        return map;
    }

    // ── 6. MAP_JPA_ORDERS — JPQL sort keys ──────────────────────────────────
    // Auto-generated for every field reachable via join-fetched aliases
    // + explicit entries from @JpqlOrderBuilder

    private static final Map<String,String> MAP_JPA_ORDERS = getMapJpaOrders();

    private static Map<String,String> getMapJpaOrders() {
        Map<String,String> map = new HashMap<>();
        // Auto-generated from join-fetched paths:
        map.put("exApplication.name",              "exApplication.name");
        map.put("exProject.idProject",             "exProject.idProject");
        map.put("exEnvironment.idEnvironment",     "exEnvironment.idEnvironment");
        map.put("exEnvironment.envName",           "exEnvironment.envName");
        map.put("exProject.prjName",               "exProject.prjName");
        // Explicit entry from @JpqlOrderBuilder:
        map.put(ord_desApplicationType,            " exApplicationType.desApplicationType ");
        // ...
        return map;
    }

    // ── 7. mapOneToMany() — conditional LEFT JOIN FETCH ──────────────────────
    // Called lazily on first access. Registers joins that are added to the SELECT
    // query ONLY when the corresponding parameter is non-null in the filter.
    // This prevents Cartesian products for unneeded collections.

    @Override
    public void mapOneToMany() {
        addJoinOneToMany(idApplicationServer,
            " left join fetch exApplication.exApplicationServers exApplicationServers ");
        addJoinOneToMany(idServiceRest,
            " left join fetch exApplication.exServiceRests exServiceRests ");
        // Each entry: if parameter 'idApplicationServer' is non-null → add the JOIN FETCH
    }
}
```

---

### How the runtime uses these maps

At query execution time `JpaServiceImpl` does the following:

1. Iterates the active parameters from `QueryParameter`.
2. For each parameter name, looks it up in `MAP_CONDITIONS`.
3. If found, appends the condition fragment to the base JPQL string.
4. Checks `mapOneToMany()` — if the parameter has a registered JOIN FETCH, adds it to the FROM clause.
5. Builds the final ORDER BY from `MAP_JPA_ORDERS` using the sort keys in `QueryParameter.orderBy`.

For native queries the same process runs against `MAP_NATIVE_CONDITIONS[zoneName]`, replacing `${zoneName}` in the SQL template with the active condition fragments for that zone.

`QueryJpql` is autowired into `JpaServiceImpl` as a generic Spring bean and is not used directly by application code.

---

## Custom Row Mapping

### JpaRowMapper

For JPA `Tuple`-based native queries that need custom row-to-object mapping, implement `JpaRowMapper<K>`:

```java
@FunctionalInterface
public interface JpaRowMapper<K> {
    void rowMapper(List<K> result, Tuple row, int i);
}
```

The framework calls `rowMapper` once per row in the result set, passing the accumulating list, the current `Tuple`, and the zero-based row index.

```java
List<ProductDto> results = productService.findNativeByFilter(nqp, (list, row, i) -> {
    ProductDto dto = new ProductDto();
    dto.setId(row.get("id", Long.class));
    dto.setName(row.get("name", String.class));
    list.add(dto);
});
```

### JdbcRowMapper

For pure JDBC queries (via `JdbcTemplateService`), implement `JdbcRowMapper<K>`:

```java
@FunctionalInterface
public interface JdbcRowMapper<K> {
    void rowMapper(List<K> list, ResultSet row, int i);
}
```

Works identically to `JpaRowMapper` but receives a JDBC `ResultSet` instead of a JPA `Tuple`:

```java
List<ReportRow> rows = reportService.findNativeByFilter(nqp, (list, rs, i) -> {
    ReportRow row = new ReportRow();
    row.setId(rs.getLong("id"));
    row.setAmount(rs.getBigDecimal("amount"));
    list.add(row);
});
```

### ResultMapper and @ResultMapping

When a native query result model contains a field that cannot be mapped automatically from the column value (e.g., an enum, a JSON blob, or a composite type), implement `ResultMapper<T>` and bind it to the field with `@ResultMapping`.

```java
// 1. Implement the converter
public class StatusMapper implements ResultMapper<StatusEnum> {

    @Override
    public StatusEnum mapToData(Map<String, Object> map) {
        return StatusEnum.fromCode((String) map.get("status_code"));
    }
}

// 2. Annotate the field on the result model
public class OrderSummary {

    private Long id;
    private String name;

    @ResultMapping(StatusMapper.class)   // custom conversion for this field
    private StatusEnum status;
}
```

The reflection engine skips automatic binding for fields annotated with `@ResultMapping` and delegates to the specified `ResultMapper` instead.

`ResultMapper` attributes:

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `Class<? extends ResultMapper<?>>` | The converter class to use; instantiated by the framework |

### @IgnoreResultSet

Marks a field to be excluded entirely from the automatic result-set mapping. Use this for fields that are computed at service level or populated after the query rather than from the SQL result.

```java
public class ProductSummary {

    private String name;
    private BigDecimal price;

    @IgnoreResultSet           // populated by a post-processing step, not from the query
    private String displayLabel;
}
```

---

## JdbcTemplateService

For pure JDBC access (bypassing JPA), extend `JdbcTemplateServiceImpl`:

```java
@Service
public class ReportServiceImpl extends JdbcTemplateServiceImpl<ReportDto, Long>
        implements ReportService {
    // inherited: findByFilter, countByFilter with NamedParameterJdbcTemplate
}
```

`JdbcTemplateService<T, ID>` mirrors the `JpaService` API but executes queries via `NamedParameterJdbcTemplate`.

---

## Abstract Search Controllers

`common-jpa-service` ships two abstract Spring MVC controller base classes that wire a `JpaService` to standard REST endpoints out of the box.  Subclass them when you need a traditional `@RestController` class instead of (or alongside) the `proxy-api-controller` dynamic proxy approach.

### BaseSearchController

`BaseSearchController<E, ID, M, P, MM>` provides three protected methods (search, count, single-result) already wired to the injected `JpaService`. Subclasses expose them as HTTP endpoints and provide the `ModelMapper`:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController
        extends BaseSearchController<Product, Long, ProductDto, ProductFilter, ProductMapper> {

    @PostMapping("/search")
    @ResponseBody
    @Transactional
    public CollectionResponse<ProductDto> search(@RequestBody ProductFilter filter) throws Exception {
        return super.findByFilter(filter);
    }

    @PostMapping("/count")
    @ResponseBody
    @Transactional
    public ObjectResponse<Long> count(@RequestBody ProductFilter filter) throws Exception {
        return super.countByFilter(filter);
    }

    @PostMapping("/search/single")
    @ResponseBody
    @Transactional
    public ObjectResponse<ProductDto> single(@RequestBody ProductFilter filter) throws Exception {
        return super.singleResultFindByFilter(filter);
    }

    @Override
    protected ProductMapper modelMapper() {
        return this.modelMapper;  // inject via @Autowired
    }
}
```

Generic type parameters:

| Parameter | Role |
|-----------|------|
| `E` | JPA entity type |
| `ID` | Primary-key type |
| `M` | Full DTO / model type |
| `P` | Filter type (`BaseParameter` subclass) |
| `MM` | `ModelMapper<E, M>` implementation |

### PerformanceSearchController

`PerformanceSearchController<E, ID, M, PM, P>` extends `BaseSearchController` and adds a second search endpoint (`/performance/search`) that returns a lighter `PM` model. Use this when list views only need a subset of the entity's fields and you want to avoid transferring the full model payload.

```java
@RestController
@RequestMapping("/api/products")
public class ProductController
        extends PerformanceSearchController<Product, Long, ProductDto, ProductSummaryDto, ProductFilter> {

    // POST /api/products/search          → List<ProductDto>       (full model)
    // POST /api/products/performance/search → List<ProductSummaryDto> (lightweight)
    // POST /api/products/count           → ObjectResponse<Long>
    // POST /api/products/search/single-result → ObjectResponse<ProductDto>
}
```

All four endpoints are inherited and require no additional code in the subclass.

Generic type parameters:

| Parameter | Role |
|-----------|------|
| `E` | JPA entity type |
| `ID` | Primary-key type |
| `M` | Full DTO / model type (for `/search`) |
| `PM` | Lightweight performance model (for `/performance/search`) |
| `P` | Filter type |

### ModelMapper and PerformanceModelMapper

`ModelMapper<E, M>` is the single-method interface used by `BaseSearchController` to convert entities to models:

```java
public interface ModelMapper<E, M> {
    M convertToModel(E entity);
}
```

`PerformanceModelMapper<E, M, PM>` extends it with a second conversion method for the lightweight model:

```java
public interface PerformanceModelMapper<E, M extends BaseModel<?>, PM extends BaseModel<?>>
        extends ModelMapper<E, M> {

    PM convertToPerformanceModel(E entity);
}
```

Typically implemented with MapStruct:

```java
@Mapper(componentModel = "spring")
public interface ProductMapper extends PerformanceModelMapper<Product, ProductDto, ProductSummaryDto> {

    @Override
    ProductDto convertToModel(Product entity);

    @Override
    ProductSummaryDto convertToPerformanceModel(Product entity);
}
```

---

## Real-world examples

### Service with complex conditions and cross-relationship navigation

Field paths in `@ConditionBuilder` follow JPQL dot-notation and can traverse multiple levels of relationships. All conditions are inactive by default and activate only when the corresponding filter field is non-null.

```java
@Service
@Transactional
@QueryBuilder(conditions = {
    // Direct field on the entity
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.httpMethod",
                      operation = OperationType.IN, parameter = "method"),

    // LIKE with case normalization
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.path",
                      operation = OperationType.LIKE, parameter = "path",
                      upperLower = UpperLowerType.UPPER),

    // Boolean equality
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.async",
                      operation = OperationType.EQUAL, parameter = "async"),

    // Date range — lower bound
    @ConditionBuilder(field = "exExceptionAudit.updateTime",
                      operation = OperationType.GREATER_EQUAL, parameter = "updateTimeFrom"),

    // Date range — upper bound (note: @DateFilter(addDay=1) shifts the value on the filter side)
    @ConditionBuilder(field = "exExceptionAudit.updateTime",
                      operation = OperationType.LESS, parameter = "updateTimeTo"),

    // Navigation through 3 levels
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.exApplication.idApplication",
                      operation = OperationType.IN, parameter = "idApplication"),

    // Navigation through 4 levels
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.exApplication.exApplicationType.idApplicationType",
                      operation = OperationType.IN, parameter = "idApplicationType"),

    // Same parameter name, different navigation path — for grant-based filtering
    @ConditionBuilder(field = "exExceptionAudit.exServiceRest.exApplication.exProject.idProject",
                      operation = OperationType.IN, parameter = "idProjectGrant"),
},
jpaOrder = {
    @JpqlOrderBuilder(key = "path",    order = "exExceptionAudit.exServiceRest.path"),
    @JpqlOrderBuilder(key = "envName", order = "exExceptionAudit.exServiceRest.exApplication.exEnvironment.envName"),
    @JpqlOrderBuilder(key = "prjName", order = "exExceptionAudit.exServiceRest.exApplication.exProject.prjName")
})
public class ExExceptionAuditServiceImpl
        extends JpaServiceImpl<ExExceptionAudit, Integer>
        implements ExExceptionAuditService {

    @Autowired
    private ExExceptionAuditRepository exExceptionAuditRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() { return this.entityManager; }

    @Override
    protected JpaRepository<ExExceptionAudit, Integer> getJpaRepository() {
        return this.exExceptionAuditRepository;
    }
}
```

### Service with native SQL zone conditions

When a filter field must activate a condition only in a native SQL query (not JPQL), use `@CustomConditionBuilder` with `keys`:

```java
@Service
@Transactional
@QueryBuilder(
    conditions = {
        @ConditionBuilder(field = "exApplication.name",
                          operation = OperationType.IN, parameter = "applicationsName"),
        @ConditionBuilder(field = "exApplication.exProject.idProject",
                          operation = OperationType.IN, parameter = "idProjectGrant"),
    },
    jpaOrder = {
        @JpqlOrderBuilder(key = "desApplicationType",
                          order = "exApplication.exApplicationType.desApplicationType")
    },
    customNativeConditions = {
        // Activates only when "email" parameter is present AND the query references zone "appCondition"
        @CustomConditionBuilder(
            condition = " and upper(eu.email) = :email ",
            parameter = "email",
            keys = "appCondition"
        )
    },
    nativeOrder = {
        @NativeOrderBuilder(key = "name", order = "ea.name")
    }
)
public class ExApplicationServiceImpl
        extends JpaServiceImpl<ExApplication, Integer>
        implements ExApplicationService {

    @Autowired
    private ExApplicationRepository exApplicationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() { return this.entityManager; }

    @Override
    protected JpaRepository<ExApplication, Integer> getJpaRepository() {
        return this.exApplicationRepository;
    }
}
```

### Filter with date ranges, LIKE, and zone annotations

A realistic filter showing combined use of `@DateFilter` for date ranges, `@LikeString` for text search, `@ConditionsZones` for zone routing, and `IDFilterParameter` as base class:

```java
@SuppressWarnings("serial")
public class ExceptionAuditFilter extends IDFilterParameter<Integer> {

    // Text search fields — all normalised to UPPERCASE before comparison
    @LikeString(upperLowerType = UpperLowerType.UPPER)
    private String path;

    @LikeString(upperLowerType = UpperLowerType.UPPER)
    private String className;

    @LikeString(upperLowerType = UpperLowerType.UPPER)
    private String methodName;

    // Date range — from (no shift) / to (shifted +1 day for inclusive upper bound)
    @DateFilter
    private Calendar updateTimeFrom;

    @DateFilter(addDay = 1)
    private Calendar updateTimeTo;

    @DateFilter
    private Calendar createTimeFrom;

    @DateFilter(addDay = 1)
    private Calendar createTimeTo;

    // List/IN conditions
    private List<Integer> idApplication;
    private List<Integer> idApplicationType;
    private List<Integer> idProgressType;
    private List<String>  method;

    // Boolean conditions
    private Boolean async;
    private Boolean scheduled;

    // Field for zone-based native SQL — not in standard JPQL path
    @LikeString(likeType = LikeType.NONE, upperLowerType = UpperLowerType.UPPER)
    @ConditionsZones(@ConditionsZone(key = "appCondition"))
    private String email;

    // Rename inherited 'id' to domain name in JSON
    @Override
    @JsonProperty("idExceptionAudit")
    public void setId(List<Integer> id) { super.setId(id); }
}
```

### Manual QueryParameter for programmatic queries

When you need to query from inside a service or hook (not from a controller), build `QueryParameter` manually:

```java
// Lookup by a single known parameter
QueryParameter<ExAssUserRole, ExAssUserRolePK> qp = new QueryParameter<>();
qp.addParameter("idUser", userId);
ExAssUserRole result = exAssUserRoleService.singleResultByFilter(qp);

// Lookup with pagination and sort
QueryParameter<ExApplication, Integer> qp2 = new QueryParameter<>();
qp2.addParameter("idProject", projectId);
qp2.addParameter("active", true);
qp2.addOrderBy("name", OrderType.ASC);
qp2.setPageSize(50);
qp2.setPageNumber(0);
List<ExApplication> apps = exApplicationService.findByFilter(qp2);

// IS NULL check — add the parameter name to nullables
QueryParameter<ExApplication, Integer> qp3 = new QueryParameter<>();
qp3.addNullable("deletedAt");   // generates: AND e.deletedAt IS NULL
List<ExApplication> active = exApplicationService.findByFilter(qp3);
```

The parameter names must match the names declared in `@ConditionBuilder(parameter = "...")` on the service implementation. Use the generated `*QueryJpql` constants to avoid hardcoded strings:

```java
qp.addParameter(ExApplicationQueryJpql.idProjectGrant, projectIds);
```

---

### TupleParameter and @TupleComparison

`TupleParameter` represents a multi-column row-value comparison for use in `IN` clauses. It is the standard way to express conditions like `(col1, col2) IN ((v1a, v2a), (v1b, v2b))` in JPQL or native SQL.

**Requirements:** at least two parameter names must be declared (fewer than two throws `JpaServiceException` at construction time).

```java
// Create a tuple parameter with two columns
TupleParameter tp = new TupleParameter(new String[]{"productId", "warehouseId"});

// Add one or more row tuples to compare against
tp.setObjects(new Object[]{1L, 10L});
tp.setObjects(new Object[]{2L, 10L});

// Add to the query parameter
QueryParameter<Inventory, Long> qp = new QueryParameter<>();
qp.addParameter("productWarehouse", tp);
```

In combination with a `@CustomConditionBuilder` the generated condition is injected at runtime:

```java
@QueryBuilder(
    customConditions = {
        @CustomConditionBuilder(
            condition = "and (inventory.productId, inventory.warehouseId) in (:productWarehouse)",
            parameter = "productWarehouse"
        )
    }
)
public class InventoryServiceImpl extends JpaServiceImpl<Inventory, Long> implements InventoryService { ... }
```

**`@TupleComparison`** is the declarative equivalent: annotate a `TupleParameter` field on a `BaseParameter` subclass so the reflection engine populates and routes it automatically.

```java
public class InventoryFilter extends BaseParameter {

    @TupleComparison({"productId", "warehouseId"})
    private TupleParameter productWarehouse;

    // getters / setters
}
```

`@TupleComparison` attributes:

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `String[]` | Named parameters forming the tuple; must contain at least two elements |
