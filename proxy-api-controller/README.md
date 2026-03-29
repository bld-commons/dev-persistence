# proxy-api-controller

Runtime REST controller framework that generates fully functional Spring MVC endpoints from annotated interfaces, with no controller implementation code required.

---

## Table of Contents

1. [Overview](#overview)
2. [Setup](#setup)
3. [Annotations](#annotations)
   - [@ApiFindController](#apifindcontroller)
   - [@ApiFind](#apifind)
   - [@ApiMapper](#apimapper)
   - [@ApiBeforeFind](#apibeforefind)
   - [@ApiAfterFind](#apiafterfind)
   - [@ApiQuery](#apiquery)
4. [Hook interfaces](#hook-interfaces)
   - [BeforeFind](#beforefind)
   - [AfterFind](#afterfind)
5. [Method parameter binding](#method-parameter-binding)
6. [Response containers](#response-containers)
7. [Execution flow](#execution-flow)
8. [Complete example](#complete-example)

---

## Overview

`proxy-api-controller` implements the [dynamic proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html) pattern on top of `common-jpa-service`. You declare a Java interface, annotate it with `@ApiFindController` and `@ApiFind`, and the framework:

1. Registers the interface as a Spring `@RestController` bean backed by a `java.lang.reflect.Proxy`.
2. Intercepts every HTTP request, extracts parameters from the request, builds a `QueryParameter`, and delegates to the correct `JpaService`.
3. Optionally maps results via a MapStruct (or any bean) mapper before returning them.

No controller code needs to be written or maintained.

---

## Setup

Add `@EnableProxyApiController` to your Spring Boot application class and specify the packages to scan:

```java
@SpringBootApplication
@EnableJpaService
@EnableProxyApiController(basePackages = "com.example.controller")
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`@EnableProxyApiController` attributes:

| Attribute | Description |
|-----------|-------------|
| `value` | Base packages (shorthand) |
| `basePackages` | Packages to scan for `@ApiFindController` interfaces |
| `basePackageClasses` | Type-safe alternative to `basePackages` |
| `clients` | Specific interface classes to register |
| `defaultConfiguration` | Configuration classes applied globally |

---

## Annotations

### @ApiFindController

Marks an interface as a dynamic REST controller. Combine with Spring MVC annotations (`@RequestMapping`, `@PostMapping`, etc.) on the interface and its methods.

```java
@ApiFindController
@RequestMapping("/api/products")
public interface ProductController {
    // methods here
}
```

The interface is registered as a `@RestController` bean via `ApiFindRegistrar`. The proxy intercepts all method calls and routes them through `FindInterceptor`.

### @ApiFind

Binds a controller interface or method to a specific JPA entity and its primary-key type.

```java
@ApiFind(entity = Product.class, id = Long.class)
```

Can be placed at **type level** (inherited by all methods) or at **method level** (overrides the type-level binding for that method):

```java
@ApiFindController
@ApiFind(entity = Product.class, id = Long.class)   // default for all methods
@RequestMapping("/api/products")
public interface ProductController {

    @PostMapping("/search")
    List<ProductDto> search(@RequestBody ProductFilter filter);

    @PostMapping("/orders/search")
    @ApiFind(entity = Order.class, id = Long.class)  // overrides at method level
    List<OrderDto> searchOrders(@RequestBody OrderFilter filter);
}
```

| Attribute | Type | Description |
|-----------|------|-------------|
| `entity` | `Class<?>` | The JPA entity class to query |
| `id` | `Class<?>` | The primary key type of the entity |

### @ApiMapper

Specifies the mapper class (and optionally a specific method) to use for converting query results before returning them.

```java
@ApiMapper(value = ProductMapper.class, method = "toDto")
```

If `method` is blank, the framework auto-resolves the first single-argument method on the mapper class whose parameter type matches the entity. The mapper bean is retrieved from the Spring context.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>` | — | The mapper class (retrieved from Spring context) |
| `method` | `String` | `""` | Method name (auto-resolved if blank) |

Can be placed at type or method level.

### @ApiBeforeFind

Registers a `BeforeFind` hook that runs **before** the query is executed.

```java
@PostMapping("/search")
@ApiBeforeFind(TenantInjector.class)
List<ProductDto> search(@RequestBody ProductFilter filter);
```

The `BeforeFind` implementation receives the current `BaseQueryParameter` (allowing you to add parameters, modify the filter, enforce security constraints, etc.) and the raw method arguments.

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `Class<? extends BeforeFind>` | The `BeforeFind` implementation (Spring bean or no-arg constructor) |

### @ApiAfterFind

Registers an `AfterFind` hook that runs **after** the query results are returned.

```java
@PostMapping("/search")
@ApiAfterFind(OrderEnricher.class)
List<OrderDto> search(@RequestBody OrderFilter filter);
```

The `AfterFind` implementation receives the result object and the raw method arguments, and can modify or replace the result.

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `Class<? extends AfterFind>` | The `AfterFind` implementation (Spring bean or no-arg constructor) |

### @ApiQuery

Optionally placed on a method to customize how the query is executed. When absent, the framework uses the standard `findByFilter` path.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Custom JPQL or native SQL string |
| `jpql` | `boolean` | `true` | `true` for JPQL, `false` for native SQL |
| `orderBy` | `@DefaultOrderBy[]` | `{}` | Default sort orders applied when no `OrderBy` is in the filter |

---

## Hook interfaces

### BeforeFind

```java
public interface BeforeFind<E, ID> {
    void before(BaseQueryParameter<E, ID> parameters, Object... args);
}
```

Implement this interface to run logic before the query. The `parameters` object is mutable — you can add named parameters, set pagination, or inject security constraints.

```java
@Component
public class TenantInjector implements BeforeFind<Product, Long> {

    @Autowired
    private TenantContext tenantContext;

    @Override
    public void before(BaseQueryParameter<Product, Long> parameters, Object... args) {
        parameters.addParameter("tenantId", tenantContext.getCurrentTenantId());
    }
}
```

### AfterFind

```java
public interface AfterFind<T> {
    T after(T result, Object... args);
}
```

Implement this interface to post-process results. The return value replaces the original result.

```java
@Component
public class OrderEnricher implements AfterFind<List<OrderDto>> {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public List<OrderDto> after(List<OrderDto> result, Object... args) {
        result.forEach(dto -> dto.setAvailable(inventoryService.isAvailable(dto.getProductId())));
        return result;
    }
}
```

---

## Method parameter binding

The interceptor extracts values from Spring MVC method parameters based on their annotations:

| Spring annotation | Binding |
|-------------------|---------|
| `@RequestBody` | Treated as a `BaseParameter` filter or added directly to the query |
| `@RequestParam` | Added as a named parameter |
| `@PathVariable` | Added as a named parameter |
| `@AuthenticationPrincipal` | Passed to BeforeFind/AfterFind hooks as an extra arg |
| `@RequestAttribute` | Added as a named parameter |
| None (no annotation) | Ignored |

---

## Response containers

The interceptor determines how to wrap the result based on the method's declared return type:

| Return type | Behaviour |
|-------------|-----------|
| `List<T>` / `Collection<T>` | Returns the mapped entity list |
| `T` (single object) | Returns the single result (calls `singleResultByFilter`) |
| `long` / `Long` / `Number` | Executes `countByFilter` and returns the count |
| `CollectionResponse<T>` | Returns a wrapper with `items`, `totalCount`, `pageNumber`, `pageSize` |
| `ObjectResponse<T>` | Returns a wrapper with `item` (single result or count) |

---

## Execution flow

```
HTTP Request
  │
  ▼
ApiFindInterceptor.invoke()
  │  retrieves FindInterceptor from Spring context
  ▼
FindInterceptor.find()
  │
  ├─ Extract parameters (@RequestBody, @RequestParam, @PathVariable, ...)
  │
  ├─ Invoke @ApiBeforeFind hook (if present)
  │
  ├─ Build QueryParameter or NativeQueryParameter
  │
  ├─ Resolve JpaService<Entity, ID> from Spring context
  │   using ResolvableType with generic bounds
  │
  ├─ Execute query (findByFilter / countByFilter / singleResult / ...)
  │
  ├─ Invoke @ApiAfterFind hook (if present)
  │
  ├─ Map results via @ApiMapper (if present)
  │
  └─ Wrap in response container and return
```

---

## Complete example

**Entity**

```java
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Boolean active;
    // ...
}
```

**Service interface + implementation (generated by jpa-service-plugin-generator)**

```java
@QueryBuilder(
    distinct = true,
    conditions = {
        @ConditionBuilder(field = "product.name",   operation = OperationType.LIKE,   parameter = "name",
                          upperLower = UpperLowerType.LOWER),
        @ConditionBuilder(field = "product.active", operation = OperationType.EQUALS, parameter = "active")
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name", field = "product.name")
    }
)
public interface ProductService extends JpaService<Product, Long> { }

@Service
@Transactional
public class ProductServiceImpl extends JpaServiceImpl<Product, Long> implements ProductService {
    @Autowired private ProductRepository productRepository;
    @PersistenceContext private EntityManager entityManager;

    @Override protected JpaRepository<Product, Long> getJpaRepository() { return productRepository; }
    @Override protected EntityManager getEntityManager() { return entityManager; }
}
```

**Filter**

```java
public class ProductFilter extends BaseParameter {
    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;
    private Boolean active;
    // getters / setters
}
```

**Mapper (MapStruct)**

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
}
```

**Controller**

```java
@ApiFindController
@ApiFind(entity = Product.class, id = Long.class)
@ApiMapper(value = ProductMapper.class, method = "toDto")
@RequestMapping("/api/products")
public interface ProductController {

    @PostMapping("/search")
    List<ProductDto> search(@RequestBody ProductFilter filter);

    @GetMapping("/count")
    long count(@RequestBody ProductFilter filter);
}
```

That is the entire controller implementation. No `@RestController` class needed.
