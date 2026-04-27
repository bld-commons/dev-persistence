# processor-jpa-service

Compile-time annotation processor that reads `@QueryBuilder`-annotated service interfaces and generates the corresponding `*QueryJpqlImpl` classes containing pre-built JPQL strings, condition maps, and order maps.

---

## Table of Contents

1. [Overview](#overview)
2. [Setup](#setup)
3. [@QueryBuilder](#querybuilder)
   - [Default conditions — auto-generated from entity fields](#default-conditions--auto-generated-from-entity-fields)
4. [@ConditionBuilder](#conditionbuilder)
   - [OperationType reference](#operationtype-reference)
5. [@CustomConditionBuilder](#customconditionbuilder)
6. [@JpqlOrderBuilder and @NativeOrderBuilder](#jpqlorderbuilder-and-nativeorderbuilder)
7. [Generated code](#generated-code)
   - [Minimal example — default fields only](#minimal-example--default-fields-only)
   - [Full example input](#full-example-input)
   - [Full example output](#full-example-output)
8. [How the processor works](#how-the-processor-works)

---

## Overview

The processor runs during the `compile` phase of Maven (or Gradle). It scans for any type annotated with `@QueryBuilder`, extracts the JPA entity class from the service's generic type parameter, reads the annotation attributes, and generates a `*QueryJpqlImpl` class.

The generated class extends `QueryJpql<T>` and is picked up by Spring as a bean. `JpaServiceImpl` then autowires it to build and execute dynamic queries at runtime.

**You never write or maintain the generated class manually.** Delete it and it will be regenerated on the next build.

---

## Setup

Add the processor as an annotation processor path in the Maven compiler plugin. Because the processor packages all its dependencies into a single fat JAR (via Maven Shade), no extra dependency entries are needed.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.bld.commons</groupId>
                <artifactId>processor-jpa-service</artifactId>
                <version>3.0.16</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

---

## @QueryBuilder

Place `@QueryBuilder` on the service interface (or class) that manages a JPA entity. The processor uses it to generate the `QueryJpqlImpl` for that entity.

```java
@QueryBuilder(
    distinct      = true,
    joins         = { "product.category" },
    conditions    = { ... },
    customConditions      = { ... },
    customNativeConditions = { ... },
    jpaOrder      = { ... },
    nativeOrder   = { ... }
)
public interface ProductService extends JpaService<Product, Long> { }
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `distinct` | `boolean` | `true` | Adds `DISTINCT` to the generated SELECT |
| `joins` | `String[]` | `{}` | Dot-notation relationship paths for unconditional `JOIN FETCH` |
| `conditions` | `@ConditionBuilder[]` | `{}` | Standard JPQL conditions (field + operation + parameter) |
| `customConditions` | `@CustomConditionBuilder[]` | `{}` | Hand-written JPQL fragments |
| `customNativeConditions` | `@CustomConditionBuilder[]` | `{}` | Hand-written native SQL fragments grouped into named zones |
| `jpaOrder` | `@JpqlOrderBuilder[]` | `{}` | Sort key → JPQL field mappings |
| `nativeOrder` | `@NativeOrderBuilder[]` | `{}` | Sort key → native SQL column mappings |

### joins — dot-notation relationship paths

Each entry in `joins` is a chain of JPA relationship field names starting from the root entity alias. The processor traverses the path and generates one `JOIN FETCH` clause per hop.

```java
joins = { "genere.postazioneCucina.ristorante" }
```

Generates:

```jpql
join fetch genere.postazioneCucina postazioneCucina
join fetch postazioneCucina.ristorante ristorante
```

These joins are **always** present regardless of which filter parameters are active. For conditional joins (added only when a specific filter parameter is non-null), the processor uses `@OneToMany` / `@ManyToMany` relationship metadata to generate a `mapOneToMany()` entry using `LEFT JOIN FETCH`.

---

### Default conditions — auto-generated from entity fields

When `@QueryBuilder` is placed on a service, the processor automatically generates a base condition for **every persistent field** of the mapped entity (`@Column`, `@Id`, `@EmbeddedId`, `@Enumerated`). These conditions populate `getMapBaseConditions()` and are **always available** at runtime — no `@ConditionBuilder` entry is required for them.

The `OperationType` applied to each field depends on its Java type:

| Java type | Default OperationType | Generated JPQL fragment | Parameter name(s) |
|-----------|----------------------|------------------------|-------------------|
| `String` | `LIKE` with `upper()` | `AND upper(e.field) LIKE :field` | `field` |
| `Date`, `LocalDate`, `LocalDateTime`, … | `GREATER_EQUAL` + `LESS_EQUAL` | `AND :fieldFrom <= e.field` / `AND e.field <= :fieldTo` | `fieldFrom`, `fieldTo` |
| `Boolean` | `EQUAL` | `AND e.field = :field` | `field` |
| `Enum` (`@Enumerated`) | `IN` | `AND e.field IN (:field)` | `field` |
| `@Id` / `@EmbeddedId` | `IN` | `AND e.pk IN (:id)` and `AND e.pk IN (:pkFieldName)` | `id`, `pkFieldName` |
| Numeric and other types | `IN` | `AND e.field IN (:field)` | `field` |

#### Overriding a default condition

To replace the auto-generated condition for a field, declare a `@ConditionBuilder` entry in `conditions[]` using the **same parameter name** as the field. The explicit entry takes priority over the auto-generated one.

```java
// Default for String "name": AND upper(product.name) LIKE :name
// Override to use EQUALS instead:
@QueryBuilder(
    conditions = {
        @ConditionBuilder(field = "product.name", operation = OperationType.EQUALS, parameter = "name")
    }
)
public interface ProductService extends JpaService<Product, Long> { }
```

---

## @ConditionBuilder

Defines a single JPQL WHERE condition. Used inside `@QueryBuilder#conditions()`.

```java
@ConditionBuilder(
    field      = "product.name",
    operation  = OperationType.LIKE,
    parameter  = "name",
    upperLower = UpperLowerType.LOWER,
    nullable   = false
)
```

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `field` | `String` | — | JPQL field path (e.g., `"e.name"`, `"e.address.city"`) |
| `operation` | `OperationType` | — | Comparison type |
| `parameter` | `String` | — | Named parameter name (maps to filter field or `QueryParameter` entry) |
| `upperLower` | `UpperLowerType` | `NONE` | Case transformation applied to both field and parameter |
| `nullable` | `boolean` | `false` | When `true`, always appended even when parameter is null (IS NULL condition) |

### OperationType reference

| Value | Generated JPQL |
|-------|---------------|
| `EQUALS` | `AND e.field = :param` |
| `NOT_EQUALS` | `AND e.field <> :param` |
| `LIKE` | `AND e.field LIKE :param` |
| `NOT_LIKE` | `AND e.field NOT LIKE :param` |
| `IN` | `AND e.field IN (:param)` |
| `NOT_IN` | `AND e.field NOT IN (:param)` |
| `GREATER_THAN` | `AND e.field > :param` |
| `LESS_THAN` | `AND e.field < :param` |
| `GREATER_EQUAL` | `AND e.field >= :param` |
| `LESS_EQUAL` | `AND e.field <= :param` |
| `BETWEEN` | `AND e.field BETWEEN :paramFrom AND :paramTo` |
| `RANGE` | Range comparison using two parameters |

---

## @CustomConditionBuilder

Lets you write raw JPQL or native SQL condition fragments directly, for cases where `@ConditionBuilder` operations are not expressive enough (subqueries, tuple comparisons, vendor-specific syntax).

Used in two distinct attributes of `@QueryBuilder`:

- `customConditions` — JPQL conditions, inserted into `MAP_CONDITIONS`
- `customNativeConditions` — native SQL conditions, grouped into named zones in `MAP_NATIVE_CONDITIONS`

### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `parameter` | `String` | — | Named parameter that activates this condition when non-null |
| `condition` | `String` | — | Raw JPQL or SQL fragment, including the leading `AND`/`OR` keyword |
| `type` | `ConditionType` | `SELECT` | `SELECT` → `MAP_CONDITIONS`, `DELETE` → `MAP_DELETE_CONDITIONS` |
| `keys` | `String[]` | `{"default"}` | Zone names (for native conditions); corresponds to `${zoneName}` placeholders in the SQL template |

### Custom JPQL condition

Use entity alias and JPA field names (not column names):

```java
customConditions = {
    @CustomConditionBuilder(
        condition = "and genere.idGenere in (:genereId)",
        parameter = "genereId"
        // type defaults to SELECT → MAP_CONDITIONS
    )
}
```

### Custom native SQL condition with zones

Use table alias and column names. Specify zone keys that match `${zoneName}` placeholders in the SQL template:

```java
customNativeConditions = {
    @CustomConditionBuilder(
        condition = "and (g.id_genere, pc.id_postazione_cucina) in (:genereTuple)",
        parameter = "genereTuple",
        keys      = {"zone1", "zone2"}
    ),
    @CustomConditionBuilder(
        condition = "and g.id_genere in (:idGenere)",
        parameter = "idGenere",
        keys      = {"zone1", "zone2"}
    )
}
// SQL template: SELECT g.* FROM genere g ${zone1}
```

---

## @JpqlOrderBuilder and @NativeOrderBuilder

Map logical sort keys (used in `OrderBy` objects) to JPQL field expressions or native SQL column references.

```java
jpaOrder = {
    @JpqlOrderBuilder(sortKey = "name",  field = "product.name"),
    @JpqlOrderBuilder(sortKey = "price", field = "product.unitPrice")
}

nativeOrder = {
    @NativeOrderBuilder(sortKey = "name", field = "p.product_name")
}
```

At runtime, `OrderBy("name", OrderType.ASC)` in the filter is resolved to `ORDER BY product.name ASC`.

---

## Generated code

For each `@QueryBuilder`-annotated class the processor generates **two files** in `target/generated-sources/annotations`:

| Generated file | Purpose |
|---|---|
| `*QueryJpql.java` | Interface with `public static final String` constants for every parameter and sort key name |
| `*QueryJpqlImpl.java` | `@Component` that extends `QueryJpql<T>`, implements the interface, and holds all pre-built strings and maps |

---

### Minimal example — default fields only

This example shows exactly what the processor generates when **no `@ConditionBuilder` is declared** — only the `getMapBaseConditions()` populated from the entity's own fields.

#### Entity

```java
@Entity
public class Product {

    @Id
    private Long idProduct;

    @Column
    private String name;

    @Column
    private BigDecimal price;

    @Column
    private Boolean active;

    @Column
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;
}
```

#### Service (minimal annotation)

```java
@Service
@Transactional
@QueryBuilder
public class ProductServiceImpl extends JpaServiceImpl<Product, Long>
        implements ProductService { }
```

#### Generated `ProductQueryJpql.java`

```java
public interface ProductQueryJpql {

    // Auto-generated from @Id
    String id           = "id";
    String idProduct    = "idProduct";

    // Auto-generated from @Column String
    String name         = "name";

    // Auto-generated from @Column BigDecimal
    String price        = "price";

    // Auto-generated from @Column Boolean
    String active       = "active";

    // Auto-generated from @Column LocalDate (two range parameters)
    String createdAtFrom = "createdAtFrom";
    String createdAtTo   = "createdAtTo";

    // Auto-generated from @Enumerated
    String status       = "status";
}
```

#### Generated `ProductQueryJpqlImpl.java`

```java
@Component
public class ProductQueryJpqlImpl
        extends QueryJpql<Product>
        implements ProductQueryJpql {

    // FROM: one JOIN FETCH per @ManyToOne field found on the entity
    private final static String FROM_BY_FILTER =
        " From Product product " +
        " join fetch product.category category ";

    private final static String SELECT_BY_FILTER    = "select distinct product" + FROM_BY_FILTER;
    private final static String COUNT_BY_FILTER     = "select distinct count(product)" + FROM_BY_FILTER;
    private final static String SELECT_ID_BY_FILTER = "select distinct product.idProduct " + FROM_BY_FILTER;
    private final static String DELETE_BY_FILTER    = "delete from Product product ";

    private final static Map<String,String> MAP_CONDITIONS        = getMapConditions();
    private final static Map<String,String> MAP_DELETE_CONDITIONS = getMapDeleteConditions();

    // All conditions come from getMapBaseConditions() — no @ConditionBuilder was declared
    private static Map<String,String> getMapConditions() {
        return getMapBaseConditions();
    }

    private static Map<String,String> getMapDeleteConditions() {
        return getMapBaseConditions();
    }

    // Auto-generated: one entry per persistent field, OperationType chosen by Java type
    private static Map<String,String> getMapBaseConditions() {
        Map<String,String> map = new HashMap<>();

        // @Id → IN (two entries: generic "id" key + field-name key)
        map.put(id,            " and product.idProduct in (:id) ");
        map.put(idProduct,     " and product.idProduct in (:idProduct) ");

        // String → LIKE with upper()
        map.put(name,          " and upper(product.name) like :name ");

        // Numeric (BigDecimal) → IN
        map.put(price,         " and product.price in (:price) ");

        // Boolean → EQUAL
        map.put(active,        " and product.active = :active ");

        // Date/LocalDate → GREATER_EQUAL + LESS_EQUAL (range parameters: From / To)
        map.put(createdAtFrom, " and :createdAtFrom <= product.createdAt ");
        map.put(createdAtTo,   " and product.createdAt <= :createdAtTo ");

        // Enum → IN
        map.put(status,        " and product.status in (:status) ");

        return map;
    }

    @Override public String selectByFilter()              { return SELECT_BY_FILTER; }
    @Override public String selectIdByFilter()            { return SELECT_ID_BY_FILTER; }
    @Override public String countByFilter()               { return COUNT_BY_FILTER; }
    @Override public String deleteByFilter()              { return DELETE_BY_FILTER; }
    @Override public Map<String,String> mapConditions()   { return MAP_CONDITIONS; }
    @Override public Map<String,String> mapDeleteConditions() { return MAP_DELETE_CONDITIONS; }
}
```

At runtime, each condition is appended to the WHERE clause **only when the corresponding parameter in the filter object is non-null**. Fields left null in the filter are simply ignored.

---

### Full example input

```java
@Service
@Transactional
@QueryBuilder(
    conditions = {
        @ConditionBuilder(field = "exApplication.name",
                          operation = OperationType.IN, parameter = "applicationsName"),
        @ConditionBuilder(field = "exApplication.exProject.idProject",
                          operation = OperationType.IN, parameter = "idProjectGrant"),
        @ConditionBuilder(field = "exApplication.exProject.idProject",
                          operation = OperationType.IN, parameter = "idProject"),
        @ConditionBuilder(field = "exApplication.exEnvironment.idEnvironment",
                          operation = OperationType.IN, parameter = "idEnvironment"),
        @ConditionBuilder(field = "exApplication.name",
                          operation = OperationType.NOT_IN, parameter = "notInName"),
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
public class ExApplicationServiceImpl extends JpaServiceImpl<ExApplication, Integer>
        implements ExApplicationService { ... }
```

---

### Full example output

#### `ExApplicationQueryJpql.java` — constants interface

One `String` constant per parameter name and per sort key. Referenced in hook implementations and manual `QueryParameter` calls to avoid hardcoded strings.

```java
public interface ExApplicationQueryJpql {

    // Parameter name constants — one per @ConditionBuilder.parameter + base entity fields
    String applicationsName    = "applicationsName";
    String idProjectGrant      = "idProjectGrant";
    String idProject           = "idProject";
    String idEnvironment       = "idEnvironment";
    String notInName           = "notInName";
    String email               = "email";       // from customNativeConditions
    String name                = "name";        // from base entity fields
    String id                  = "id";
    String idApplication       = "idApplication";
    String updateTime          = "updateTime";
    String updateTimeFrom      = "updateTimeFrom";
    String updateTimeTo        = "updateTimeTo";
    String createTime          = "createTime";
    String createTimeFrom      = "createTimeFrom";
    String createTimeTo        = "createTimeTo";
    // ...

    // Sort key constants (prefixed ord_)
    String ord_desApplicationType = "desApplicationType";  // from @JpqlOrderBuilder
    String ord_name               = "name";                // from @NativeOrderBuilder
}
```

#### `ExApplicationQueryJpqlImpl.java` — full generated class

```java
@Component
public class ExApplicationQueryJpqlImpl
        extends QueryJpql<ExApplication>
        implements ExApplicationQueryJpql {

    // ── Static maps (initialised once at class-load time) ───────────────────
    private final static Map<String,String>              MAP_CONDITIONS        = getMapConditions();
    private final static Map<String,String>              MAP_DELETE_CONDITIONS = getMapDeleteConditions();
    private final static Map<String,Map<String,String>>  MAP_NATIVE_CONDITIONS = getMapNativeConditions();
    private final static Map<String,String>              MAP_NATIVE_ORDERS     = getMapNativeOrders();
    private final static Map<String,String>              MAP_JPA_ORDERS        = getMapJpaOrders();

    // ── Base JPQL strings ────────────────────────────────────────────────────
    // FROM includes all mandatory JOIN FETCH clauses derived from the entity's @ManyToOne fields.
    // The processor walks the entity graph and generates one JOIN FETCH per relationship
    // that is not a @OneToMany / @ManyToMany (those go into mapOneToMany() instead).

    private final static String FROM_BY_FILTER =
        " From ExApplication exApplication " +
        " join fetch exApplication.exProject exProject " +
        " join fetch exApplication.exEnvironment exEnvironment " +
        " join fetch exApplication.exApplicationType exApplicationType ";

    private final static String SELECT_BY_FILTER    = "select distinct exApplication" + FROM_BY_FILTER;
    private final static String COUNT_BY_FILTER     = "select distinct count(exApplication)" + FROM_BY_FILTER;
    private final static String SELECT_ID_BY_FILTER = "select distinct exApplication.idApplication " + FROM_BY_FILTER;
    private final static String DELETE_BY_FILTER    = "delete from ExApplication exApplication ";
    // Note: DELETE has no JOIN FETCH — relationships are navigated inline in MAP_DELETE_CONDITIONS.

    // ── MAP_CONDITIONS — active during SELECT / COUNT queries ────────────────
    // Key   = parameter name
    // Value = JPQL fragment appended to WHERE when the parameter is non-null
    //
    // Two sub-maps are merged:
    //   getMapBaseConditions() — auto-generated from the entity's own fields
    //   explicit entries       — generated from each @ConditionBuilder
    //
    // In SELECT queries, relationship aliases from FROM_BY_FILTER are reused directly
    // (e.g. "exProject" instead of "exApplication.exProject").

    private static Map<String,String> getMapConditions() {
        Map<String,String> map = getMapBaseConditions();
        map.put(applicationsName, " and ((exApplication.name)  in (:applicationsName) )");
        map.put(notInName,        " and ((exApplication.name)  not in (:notInName) )");
        map.put(idProject,        " and (exProject.idProject  in (:idProject) )");
        map.put(idEnvironment,    " and (exEnvironment.idEnvironment  in (:idEnvironment) )");
        map.put(idProjectGrant,   " and (exProject.idProject  in (:idProjectGrant) )");
        return map;
    }

    // Auto-generated base conditions — entity-level fields that are always available
    private static Map<String,String> getMapBaseConditions() {
        Map<String,String> map = new HashMap<>();
        map.put(name,           " and upper(exApplication.name) like :name ");
        map.put(id,             " and exApplication.idApplication in (:id) ");
        map.put(idApplication,  " and exApplication.idApplication in (:idApplication) ");
        map.put(updateTimeFrom, " and :updateTimeFrom<=exApplication.updateTime ");
        map.put(updateTimeTo,   " and exApplication.updateTime<=:updateTimeTo ");
        map.put(createTimeFrom, " and :createTimeFrom<=exApplication.createTime ");
        map.put(createTimeTo,   " and exApplication.createTime<=:createTimeTo ");
        return map;
    }

    // ── MAP_DELETE_CONDITIONS — active during DELETE queries ─────────────────
    // Identical structure to MAP_CONDITIONS but without join-fetched aliases.
    // DELETE JPQL cannot use JOIN FETCH, so every relationship is navigated
    // from the root entity alias using the full dot-notation path.

    private static Map<String,String> getMapDeleteConditions() {
        Map<String,String> map = getMapBaseConditions();
        // Compare with MAP_CONDITIONS:
        //   SELECT: "exProject.idProject in (:idProject)"       ← uses fetched alias
        //   DELETE: "exApplication.exProject.idProject in (:idProject)"  ← full path
        map.put(idProject,      " and (exApplication.exProject.idProject  in (:idProject) )");
        map.put(idEnvironment,  " and (exApplication.exEnvironment.idEnvironment  in (:idEnvironment) )");
        map.put(idProjectGrant, " and (exApplication.exProject.idProject  in (:idProjectGrant) )");
        map.put(applicationsName," and ((exApplication.name)  in (:applicationsName) ) ");
        map.put(notInName,       " and ((exApplication.name)  not in (:notInName) ) ");
        return map;
    }

    // ── MAP_NATIVE_CONDITIONS — per-zone native SQL conditions ───────────────
    // Outer key = zone name (must match ${zoneName} in the SQL template string)
    // Inner map = parameter name → SQL fragment injected into that zone when non-null
    // Generated from @QueryBuilder#customNativeConditions with matching keys.

    private static Map<String,Map<String,String>> getMapNativeConditions() {
        Map<String,Map<String,String>> map = new HashMap<>();
        map.put("appCondition", getAppCondition());
        return map;
    }

    private static Map<String,String> getAppCondition() {
        Map<String,String> map = new HashMap<>();
        map.put(email, " and upper(eu.email)=:email ");
        return map;
    }

    // ── MAP_NATIVE_ORDERS — native SQL sort keys ─────────────────────────────
    // Key   = logical sort key (from @NativeOrderBuilder.key)
    // Value = SQL column expression used in ORDER BY

    private static Map<String,String> getMapNativeOrders() {
        Map<String,String> map = new HashMap<>();
        map.put(ord_name, " ea.name ");
        return map;
    }

    // ── MAP_JPA_ORDERS — JPQL sort keys ──────────────────────────────────────
    // Auto-generated for every reachable field via join-fetched aliases + explicit @JpqlOrderBuilder entries.
    // Key   = sort key string (from OrderBy.sortKey in the filter)
    // Value = JPQL field expression for ORDER BY

    private static Map<String,String> getMapJpaOrders() {
        Map<String,String> map = new HashMap<>();
        // Auto-generated from join-fetched paths:
        map.put("exApplication.name",          "exApplication.name");
        map.put("exProject.idProject",         "exProject.idProject");
        map.put("exEnvironment.idEnvironment", "exEnvironment.idEnvironment");
        map.put("exEnvironment.envName",       "exEnvironment.envName");
        map.put("exProject.prjName",           "exProject.prjName");
        // Explicit entry from @JpqlOrderBuilder:
        map.put(ord_desApplicationType,        " exApplicationType.desApplicationType ");
        return map;
    }

    // ── mapOneToMany() — conditional LEFT JOIN FETCH ──────────────────────────
    // Called lazily the first time getMapOneToMany() is accessed.
    // Each entry registers a LEFT JOIN FETCH that is added to the SELECT/COUNT query
    // ONLY when the corresponding parameter is non-null in the filter.
    // This prevents Cartesian products for collections that are not being filtered on.

    @Override
    public void mapOneToMany() {
        addJoinOneToMany(idApplicationServer,
            " left join fetch exApplication.exApplicationServers exApplicationServers ");
        addJoinOneToMany(idServiceRest,
            " left join fetch exApplication.exServiceRests exServiceRests ");
        // If the filter has idApplicationServer → the JOIN FETCH is added automatically.
        // If it is null → no join, no Cartesian product.
    }

    // ── Bridge methods ────────────────────────────────────────────────────────

    @Override public String selectByFilter()                        { return SELECT_BY_FILTER; }
    @Override public String selectIdByFilter()                      { return SELECT_ID_BY_FILTER; }
    @Override public String countByFilter()                         { return COUNT_BY_FILTER; }
    @Override public String deleteByFilter()                        { return DELETE_BY_FILTER; }
    @Override public Map<String,String> mapConditions()             { return MAP_CONDITIONS; }
    @Override public Map<String,String> mapDeleteConditions()       { return MAP_DELETE_CONDITIONS; }
    @Override public Map<String,Map<String,String>> mapNativeConditions() { return MAP_NATIVE_CONDITIONS; }
    @Override public Map<String,String> mapNativeOrders()           { return MAP_NATIVE_ORDERS; }
    @Override public Map<String,String> mapJpaOrders()              { return MAP_JPA_ORDERS; }
}
```

---

### Key differences between MAP_CONDITIONS and MAP_DELETE_CONDITIONS

The same `@ConditionBuilder` generates **two different condition strings** — one for SELECT and one for DELETE:

| Query type | Path used | Example |
|---|---|---|
| SELECT / COUNT | Short alias from JOIN FETCH | `exProject.idProject in (:idProject)` |
| DELETE | Full path from root alias | `exApplication.exProject.idProject in (:idProject)` |

This is because DELETE JPQL cannot contain JOIN FETCH clauses, so the processor automatically rewrites every condition to use the full dot-notation path starting from the root entity alias.

---

## How the processor works

1. The processor is triggered by the presence of `@QueryBuilder` on any type.
2. It extracts the first generic type argument of the service (the entity class).
3. It traverses the entity class fields using Java annotation mirrors to find:
   - `@Id` / `@EmbeddedId` → primary key type
   - `@OneToMany` / `@ManyToMany` → candidates for conditional `LEFT JOIN FETCH`
   - `@JoinColumn` → relationship metadata
4. It processes each `@ConditionBuilder` and `@CustomConditionBuilder` entry to populate condition maps.
5. It processes `joins[]` paths to generate the static JOIN FETCH chain.
6. Using FreeMarker templates, it writes the complete `*QueryJpqlImpl.java` source file to the generated-sources directory.
7. The Maven compiler then compiles it along with the rest of the project.

The processor output directory is `target/generated-sources/annotations`. The generated class is compiled and available at runtime as any other class in your application.
