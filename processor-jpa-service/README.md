# processor-jpa-service

Compile-time annotation processor that reads `@QueryBuilder`-annotated service interfaces and generates the corresponding `*QueryJpqlImpl` classes containing pre-built JPQL strings, condition maps, and order maps.

---

## Table of Contents

1. [Overview](#overview)
2. [Setup](#setup)
3. [@QueryBuilder](#querybuilder)
4. [@ConditionBuilder](#conditionbuilder)
   - [OperationType reference](#operationtype-reference)
5. [@CustomConditionBuilder](#customconditionbuilder)
6. [@JpqlOrderBuilder and @NativeOrderBuilder](#jpqlorderbuilder-and-nativeorderbuilder)
7. [Generated code](#generated-code)
   - [Example input](#example-input)
   - [Example output](#example-output)
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

### Example input

```java
@QueryBuilder(
    distinct   = true,
    joins      = { "order.customer" },
    conditions = {
        @ConditionBuilder(field = "order.status",   operation = OperationType.EQUALS, parameter = "status"),
        @ConditionBuilder(field = "order.customer.email", operation = OperationType.LIKE, parameter = "email",
                          upperLower = UpperLowerType.LOWER)
    },
    customConditions = {
        @CustomConditionBuilder(condition = "and order.total > :minTotal", parameter = "minTotal")
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "date", field = "order.createdAt")
    }
)
public interface OrderService extends JpaService<Order, Long> { }
```

### Example output (simplified)

```java
@Component
public class OrderQueryJpqlImpl extends QueryJpql<Order> {

    private static final String SELECT =
        "select distinct order from Order order " +
        "join fetch order.customer customer " +
        "${joinZone} where 1=1 ";

    private static final String COUNT =
        "select count(distinct order) from Order order " +
        "join fetch order.customer customer " +
        "${joinZone} where 1=1 ";

    private static final String DELETE =
        "delete from Order order where 1=1 ";

    private static final Map<String, String> MAP_CONDITIONS = new HashMap<>();
    private static final Map<String, String> MAP_DELETE_CONDITIONS = new HashMap<>();
    private static final Map<String, String> MAP_JPA_ORDERS = new HashMap<>();

    static {
        MAP_CONDITIONS.put("status",   "and order.status = :status");
        MAP_CONDITIONS.put("email",    "and LOWER(order.customer.email) LIKE LOWER(:email)");
        MAP_CONDITIONS.put("minTotal", "and order.total > :minTotal");

        MAP_JPA_ORDERS.put("date", "order.createdAt");
    }

    @Override
    public String selectByFilter()  { return SELECT; }
    @Override
    public String countByFilter()   { return COUNT; }
    @Override
    public String deleteByFilter()  { return DELETE; }
    @Override
    public Map<String, String> mapConditions()  { return MAP_CONDITIONS; }
    @Override
    public Map<String, String> mapJpaOrders()   { return MAP_JPA_ORDERS; }

    @Override
    public void mapOneToMany() {
        // Adds LEFT JOIN FETCH entries for @OneToMany/@ManyToMany fields
        // keyed by the filter parameter name — added only when that parameter is non-null
    }
}
```

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
