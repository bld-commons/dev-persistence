# common-jpa-service

Core runtime module of the dev-persistence framework. Provides the service abstractions, dynamic query building engine, and reflection-based filter binding that all other modules depend on.

---

## Table of Contents

1. [Overview](#overview)
2. [Configuration](#configuration)
3. [Service Layer](#service-layer)
   - [JpaService interface](#jpaservice-interface)
   - [Extending JpaServiceImpl](#extending-jpaserviceimpl)
4. [Filter Objects (BaseParameter)](#filter-objects-baseparameter)
   - [Filter Annotations](#filter-annotations)
5. [QueryParameter](#queryparameter)
6. [Native SQL Queries](#native-sql-queries)
   - [NativeQueryParameter](#nativequeryparameter)
   - [Zone-based conditions](#zone-based-conditions)
7. [QueryJpql](#queryjpql)
8. [Custom Row Mapping](#custom-row-mapping)
9. [JdbcTemplateService](#jdbctemplateservice)

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
        @ConditionBuilder(field = "p.name", operation = OperationType.LIKE, parameter = "name"),
        @ConditionBuilder(field = "p.active", operation = OperationType.EQUALS, parameter = "active")
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name", field = "p.name")
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

## Filter Objects (BaseParameter)

Create a filter DTO by extending `BaseParameter`. Any non-null field is automatically added to the WHERE clause at runtime.

```java
public class ProductFilter extends BaseParameter {

    private String name;

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String description;

    @DateFilter(addDay = 1)
    private Date expiresAfter;

    @ListFilter
    private List<Long> categoryIds;

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

### Filter Annotations

#### @DateFilter

Shifts a `Date` (or `Calendar`) value by a configurable offset before binding it as a parameter.

```java
@DateFilter(addDay = 7)
private Date expiresBy = new Date();
// binds as: now + 7 days
```

Parameters: `addYear`, `addMonth`, `addWeek`, `addDay`, `addHour`, `addMinute`, `addSecond` (all default 0, negative values subtract).

#### @LikeString

Wraps a `String` value in SQL `LIKE` wildcards.

```java
@LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
private String lastName;
// generates: AND LOWER(e.lastName) LIKE LOWER(:lastName)  →  '%smith%'
```

`LikeType` values: `LEFT` (`%value`), `RIGHT` (`value%`), `LEFT_RIGHT` (`%value%`).
`UpperLowerType` values: `NONE`, `UPPER`, `LOWER`.

#### @ListFilter

Maps a `Collection` to an SQL `IN (…)` clause.

```java
@ListFilter
private List<String> statuses;
// generates: AND o.status IN (:statuses)
```

#### @FieldMapping

Overrides the default JPQL parameter name (which defaults to the Java field name).

```java
@FieldMapping("firstName")
private String userFirstName;
// JPQL uses :firstName instead of :userFirstName
```

#### @IgnoreMapping

Prevents a field from being added to the parameter map. Used on fields that should not generate WHERE conditions (e.g., display-only fields, flags).

#### @FilterNullValue

Forces a field to be included in the WHERE clause even when its value is `null` (generates an IS NULL condition).

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

Zones allow the same SQL template to have multiple independently activated WHERE blocks. Each zone corresponds to a `${zoneName}` placeholder in the SQL template.

Use `@ConditionsZone` to create custom zone annotations for use with `@QueryBuilder#customNativeConditions()`:

```java
@ConditionsZone(key = "dateRange", initWhere = true)
@Retention(RUNTIME)
@Target(FIELD)
public @interface DateRangeZone { }

public class ReportFilter extends BaseParameter {
    @DateRangeZone
    private Date from;

    @DateRangeZone
    private Date to;
}
```

`initWhere = true` (default) tells the framework to prepend `WHERE` or `AND` before the first condition in the zone automatically.

---

## QueryJpql

`QueryJpql<T>` is the abstract holder for all pre-built JPQL/SQL strings for a specific entity. You never create subclasses manually — they are generated by `processor-jpa-service`.

The generated `*QueryJpqlImpl` class provides:

- Static JPQL strings: SELECT, COUNT, DELETE, SELECT-ID
- Condition map: `Map<String, String>` — parameter name → JPQL condition fragment (e.g., `"name" → "AND e.name = :name"`)
- Order map: `Map<String, String>` — sort key → JPQL field expression
- Native condition map: `Map<String, Map<String, String>>` — zone name → condition map
- `mapOneToMany()` — conditional LEFT JOIN FETCH, added only when the related filter parameter is non-null

`QueryJpql` is autowired into `JpaServiceImpl` as a generic Spring bean and is not used directly by application code.

---

## Custom Row Mapping

For native SQL queries requiring custom Tuple-to-object mapping, implement `JpaRowMapper<K>`:

```java
public class ProductRowMapper implements JpaRowMapper<ProductDto> {
    @Override
    public ProductDto mapRow(Tuple tuple) {
        ProductDto dto = new ProductDto();
        dto.setId(tuple.get("id", Long.class));
        dto.setName(tuple.get("name", String.class));
        return dto;
    }
}

List<ProductDto> results = productService.findNativeByFilter(nqp, new ProductRowMapper());
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
