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
   - [@DefaultOrderBy](#defaultorderby)
4. [Hook interfaces](#hook-interfaces)
   - [BeforeFind](#beforefind)
   - [AfterFind](#afterfind)
5. [Method parameter binding](#method-parameter-binding)
6. [Response containers](#response-containers)
7. [Execution flow](#execution-flow)
8. [Internal components](#internal-components)
9. [Error handling](#error-handling)
10. [Real-world examples](#real-world-examples)
   - [Basic controller with security and caching](#basic-controller-with-security-and-caching)
   - [Method-level annotation overrides](#method-level-annotation-overrides)
   - [Named query with @ApiQuery](#named-query-with-apiquery)
   - [BeforeFind hook with @AuthenticationPrincipal](#beforefind-hook-with-authenticationprincipal)
   - [AfterFind hook to enrich results](#afterfind-hook-to-enrich-results)
   - [Filter class with IDFilterParameter](#filter-class-with-idfilterparameter)
9. [Complete end-to-end example](#complete-end-to-end-example)

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

Any Spring annotation that works on a `@RestController` class can be placed on the interface or its methods: `@Transactional`, `@PreAuthorize`, `@Cacheable`, etc.

### @ApiFind

Binds a controller interface or method to a specific JPA entity and its primary-key type.

```java
@ApiFind(entity = Product.class, id = Long.class)
```

Can be placed at **type level** (inherited by all methods) or at **method level** (overrides the type-level binding for that method):

```java
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@RequestMapping("/api-exception/exception-audit")
public interface ExceptionAuditProxyController {

    // Uses type-level binding → queries ExExceptionAudit
    @PostMapping("/search")
    CollectionResponse<ExceptionAuditModel> findByFilter(@RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

    // Overrides entity at method level → queries ExUserAssigned instead
    @PostMapping("/search/by-user")
    @ApiFind(entity = ExUserAssigned.class, id = Integer.class)
    @ApiMapper(value = ExExceptionAuditMapper.class, method = "convertToModel")
    CollectionResponse<ExceptionAuditModel> findByUser(
        @RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal @Param("email")
        @LikeString(likeType = LikeType.NONE, upperLowerType = UpperLowerType.UPPER)
        ExUserSecurity exUserSecurity);
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

Can be placed at **type level** (applies to all methods) or at **method level** (overrides the type-level mapper for that method):

```java
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@ApiMapper(ExApplicationMapper.class)               // default mapper for all methods
@RequestMapping("/api-exception/application")
public interface ApplicationProxyController {

    @PostMapping("/search")
    CollectionResponse<ApplicationModel> findByFilter(@RequestBody ApplicationFilter filter);

    @PostMapping("/search/by-user")
    @ApiFind(entity = ExUserAssigned.class, id = Integer.class)
    @ApiMapper(value = ExExceptionAuditMapper.class, method = "convertToModel") // override
    CollectionResponse<ExceptionAuditModel> findByUser(@RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal ExUserSecurity user);
}
```

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>` | — | The mapper class (retrieved from Spring context) |
| `method` | `String` | `""` | Method name (auto-resolved if blank) |

### @ApiBeforeFind

Registers a `BeforeFind` hook that runs **before** the query is executed.

```java
@PostMapping("/grant/search")
@ApiBeforeFind(GrantHandlerFindRequest.class)
CollectionResponse<ApplicationModel> findByFilter(
    @RequestBody ApplicationFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);
```

The `BeforeFind` implementation receives the current `BaseQueryParameter` (allowing you to add parameters, modify the filter, enforce security constraints, etc.) and the raw method arguments. The extra method arguments (e.g., `@AuthenticationPrincipal`) are passed as `args` in order.

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `Class<? extends BeforeFind>` | The `BeforeFind` implementation (Spring bean or no-arg constructor) |

### @ApiAfterFind

Registers an `AfterFind` hook that runs **after** the query results are returned.

```java
@PostMapping("/search/id")
@ApiAfterFind(UserHandlerFindRequest.class)
ObjectResponse<UserModel> findById(@RequestBody @Valid IdUserFilter idUserFilter);
```

The `AfterFind` implementation receives the result object and the raw method arguments, and can modify or replace the result.

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `Class<? extends AfterFind>` | The `AfterFind` implementation (Spring bean or no-arg constructor) |

### @ApiQuery

Optionally placed on a method to execute a named JPQL query instead of the standard `findByFilter` path. The named query string is typically defined as a constant on the service interface.

```java
@PostMapping("/all/names")
@PreAuthorize("hasAuthority('OWNER')")
@ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
CollectionResponse<ApplicationModel> searchName();
```

When `@ApiQuery` is present with a value, the framework executes that JPQL string directly. The `orderBy` attribute injects a default sort when the request carries no `OrderBy` parameters.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The query string to execute (native SQL or JPQL depending on `jpql`) |
| `jpql` | `boolean` | `false` | `false` (default) = native SQL; `true` = JPQL dynamic mode (auto-build JPQL from `value`) |
| `orderBy` | `@DefaultOrderBy[]` | `{}` | Default sort orders applied when no `OrderBy` is in the filter |

> **Important:** `jpql = false` (default) runs `value` as a native SQL string via `NativeQueryParameter`. `jpql = true` runs the auto-generated JPQL pipeline; in this case `value` is the JPQL string, or leave it blank to let the framework generate the WHERE clause automatically.

### @DefaultOrderBy

Defines a single default `ORDER BY` clause item used inside `@ApiQuery#orderBy()`. Multiple entries produce a multi-column sort applied when the caller provides no explicit sort key.

```java
@PostMapping("/products/all")
@ApiQuery(
    value  = "SELECT p FROM Product p WHERE p.active = true",
    jpql   = true,
    orderBy = {
        @DefaultOrderBy(value = "p.name", orderType = OrderType.asc),
        @DefaultOrderBy(value = "p.price", orderType = OrderType.desc)
    }
)
CollectionResponse<ProductDto> findAll();
```

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — | JPQL or SQL sort expression (e.g., `"p.name"`, `"price"`) |
| `orderType` | `OrderType` | `OrderType.asc` | Sort direction (`asc` or `desc`) |

---

## Hook interfaces

### BeforeFind

```java
public interface BeforeFind<E, ID> {
    void before(BaseQueryParameter<E, ID> parameters, Object... args);
}
```

Implement this interface to run logic before the query. The `parameters` object is mutable — you can add named parameters, set pagination, or inject security constraints.

**Real example — injecting IDs from a security-resolved lookup:**

```java
@Component
public class GrantHandlerFindRequest implements BeforeFind<ExApplication, Integer> {

    @Autowired
    private ExProjectService exProjectService;

    @Override
    public void before(BaseQueryParameter<ExApplication, Integer> parameters, Object... args) {
        // args[0] is the @AuthenticationPrincipal passed to the controller method
        ExUserSecurity exUserSecurity = (ExUserSecurity) args[0];
        parameters.addParameter(
            ExApplicationQueryJpql.idProjectGrant,
            exProjectService.findIdProjectByEmail(exUserSecurity.getUsername())
        );
    }
}
```

The `args` array contains the method arguments **in declaration order**, excluding parameters annotated with `@IgnoreMapping` (which are stripped before building the query parameter map) — they are still passed to hooks.

### AfterFind

```java
public interface AfterFind<T> {
    T after(T result, Object... args);
}
```

Implement this interface to post-process results. The return value replaces the original result.

**Real example — enriching a single-result response with additional data:**

```java
@Component
public class UserHandlerFindRequest implements AfterFind<ObjectResponse<UserModel>> {

    @Autowired
    private ExAssUserRoleService exAssUserRoleService;

    @Autowired
    private ExProjectService exProjectService;

    @Override
    public ObjectResponse<UserModel> after(ObjectResponse<UserModel> response, Object... args) {
        IdUserFilter filter = (IdUserFilter) args[0];

        // Load role from a separate query
        QueryParameter<ExAssUserRole, ExAssUserRolePK> queryUserRole = new QueryParameter<>();
        queryUserRole.addParameter(ExAssUserRoleQueryJpql.idUser, response.getData().getId());
        ExAssUserRole userRole = exAssUserRoleService.singleResultByFilter(queryUserRole);

        response.getData().setIdRole(userRole.getId().getIdRole());
        response.getData().setPriority(userRole.getExRole().getPriority());

        // Load project associations
        List<Integer> idProjects = exProjectService.findIdProjectByIdUser(filter.getIdUser());
        if (CollectionUtils.isEmpty(idProjects))
            idProjects.add(-1);
        response.getData().setIdProject(idProjects);

        return response;
    }
}
```

---

## Method parameter binding

The interceptor extracts values from Spring MVC method parameters based on their annotations:

| Spring annotation | Binding |
|-------------------|---------|
| `@RequestBody` | Treated as a `BaseParameter` filter; all non-null fields become query parameters |
| `@RequestParam` | Added as a named parameter |
| `@PathVariable` | Added as a named parameter |
| `@AuthenticationPrincipal` | Passed to hooks as an element of `args` — not added to the query parameter map |
| `@RequestAttribute` | Added as a named parameter |
| `@IgnoreMapping` | Prevents the parameter from being added to the query map, but still forwarded to hooks |
| None (no annotation) | Ignored |

### @IgnoreMapping on method parameters

`@IgnoreMapping` can be placed on a method parameter to prevent the framework from treating it as a query filter, while still making it available to `BeforeFind`/`AfterFind` hooks via `args`. This is the standard pattern for `@AuthenticationPrincipal`:

```java
@PostMapping("/search")
CollectionResponse<ExceptionAuditModel> findByFilter(
    @RequestBody ExceptionAuditFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);
    //                        ↑ passed to hooks but never added to the WHERE clause
```

### @ConditionsZones on method parameters

When a method parameter (e.g., `@AuthenticationPrincipal`) should contribute to a **zone-based native SQL condition**, annotate it with `@ConditionsZones` to route its value to the correct zone:

```java
@PostMapping("/grant/all/names")
@ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
CollectionResponse<ApplicationModel> searchName(
    @AuthenticationPrincipal
    @Param("email")
    @ConditionsZones(@ConditionsZone(key = "appCondition"))
    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    ExUserSecurity exUserSecurity);
```

Here `exUserSecurity` is bound as the `email` parameter (via `@Param`) and routed to the `appCondition` zone in the native SQL template.

---

## Response containers

The interceptor determines how to wrap the result based on the method's declared return type:

| Return type | Behaviour |
|-------------|-----------|
| `List<T>` / `Collection<T>` | Returns the mapped entity list |
| `T` (single object) | Returns the single result (calls `singleResultByFilter`) |
| `long` / `Long` / `Number` | Executes `countByFilter` and returns the count |
| `CollectionResponse<T>` | Returns a wrapper with `items`, `totalCount`, `pageNumber`, `pageSize` |
| `ObjectResponse<T>` | Returns a wrapper with `item` (single result) |

`CollectionResponse` is the standard choice for paginated lists. `ObjectResponse` is used for single-item lookups where you need a consistent JSON wrapper:

```java
// Returns { "data": {...}, "status": ... }
@PostMapping("/search/id")
ObjectResponse<ApplicationModel> singleResultFindByFilter(
    @RequestBody @Valid IdApplicationFilter baseParameter);
```

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
  │   - @IgnoreMapping params skipped for query map, kept for hooks
  │   - @ConditionsZones params routed to named zones
  │
  ├─ Invoke @ApiBeforeFind hook (if present)
  │   - passes BaseQueryParameter + all method args
  │
  ├─ Build QueryParameter or NativeQueryParameter
  │
  ├─ Resolve JpaService<Entity, ID> from Spring context
  │   using ResolvableType with generic bounds
  │
  ├─ Execute query based on return type:
  │   - CollectionResponse / List  → findByFilter
  │   - ObjectResponse / T         → singleResultByFilter
  │   - long / Long                → countByFilter
  │
  ├─ Invoke @ApiAfterFind hook (if present)
  │   - passes result + all method args
  │
  ├─ Map results via @ApiMapper (if present)
  │
  └─ Wrap in response container and return
```

---

## Internal components

Understanding the internal classes is useful when extending or debugging the framework.

| Class | Role |
|-------|------|
| `EnableProxyApiController` | Entry-point annotation; imports `ProxyApiFindConfig` and `ApiFindRegistrar` |
| `ApiFindRegistrar` | `ImportBeanDefinitionRegistrar` that scans the classpath for `@ApiFindController` interfaces and registers each as a Spring bean via `ProxyConfig.newProxyInstance()` |
| `ProxyApiFindConfig` | Spring `@Configuration` that activates component scanning for the `com.bld.proxy.api.find` package and enables common utilities |
| `ProxyConfig` | Spring `@Component` factory that creates JDK dynamic proxy instances for `@ApiFindController` interfaces using `ApiFindInterceptor` as the `InvocationHandler` |
| `ApiFindInterceptor` | Singleton `InvocationHandler` registered on every proxy; retrieves a fresh `FindInterceptor` prototype bean per invocation and delegates to it |
| `FindInterceptor` | Prototype-scoped component that performs the actual query: parameter extraction → `BeforeFind` hook → query execution → mapper invocation → `AfterFind` hook |
| `ParameterDetails` | Internal value object capturing a method parameter's `java.lang.reflect.Parameter`, its runtime value, and its position index |
| `ApiFindException` | Unchecked runtime exception thrown when proxy invocation or mapper resolution fails |

### Registration flow (startup)

```
@EnableProxyApiController
  └─ imports ApiFindRegistrar
       └─ scans packages for @ApiFindController interfaces
            └─ for each interface:
                 BeanDefinitionBuilder.setFactoryMethodOnBean("newProxyInstance", "proxyConfig")
                 → ProxyConfig.newProxyInstance(interfaceClass)
                 → Proxy.newProxyInstance(..., ApiFindInterceptor)
                 → bean registered in Spring context as the interface type
```

### Request handling flow (runtime)

```
HTTP request
  → proxy method call
  → ApiFindInterceptor.invoke()                      [singleton]
      → applicationContext.getBean(FindInterceptor)  [new prototype per request]
      → FindInterceptor.find()
          → extract @RequestBody / @RequestParam / @PathVariable parameters
          → resolve @ApiFind entity + id types
          → invoke @ApiBeforeFind hook (if present)
          → build QueryParameter or NativeQueryParameter
          → resolve JpaService<E,ID> via ResolvableType
          → execute: findByFilter / countByFilter / singleResultByFilter
          → apply @ApiMapper (entity → DTO via mapper bean)
          → invoke @ApiAfterFind hook (if present)
          → return wrapped response
```

---

## Error handling

The framework throws `ApiFindException` (unchecked) in the following situations:

| Scenario | Message |
|----------|---------|
| `@ApiFind` annotation missing on interface and method | `NullPointerException` on `apiFind.entity()` — ensure `@ApiFind` is present |
| `@ApiMapper` missing when result needs mapping | `"The class to convert the entity to output is not declared"` |
| Mapper method not found for the entity/model pair | `"Method mapper is not found"` |
| Multiple compatible mapper methods found | `"More compatible methods were found in the mapping class, use @ApiMethodMapper or @ApiMapper to select the method name"` |
| `@ApiQuery` native SQL with blank `value` | `"For native query the field 'value' can not be blank into ApiQuery"` |

**Standard exception handling:** `ApiFindException` extends `RuntimeException`. Configure a Spring `@ControllerAdvice` / `@RestControllerAdvice` to map it to an HTTP error response:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiFindException.class)
    public ResponseEntity<ErrorResponse> handleApiFindException(ApiFindException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
```

---

## Real-world examples

### Basic controller with security and caching

A controller exposing two search endpoints for the same entity. Standard Spring annotations (`@PreAuthorize`, `@Cacheable`, `@Transactional`) work directly on the interface methods.

```java
@ApiFindController
@RequestMapping("/api-exception/application")
@Transactional(rollbackFor = Exception.class)
@ApiFind(entity = ExApplication.class, id = Integer.class)
@ApiMapper(ExApplicationMapper.class)
public interface ApplicationProxyController {

    @PostMapping(path = "/search",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Cacheable("listApplication")
    @PreAuthorize("hasAuthority('OWNER')")
    CollectionResponse<ApplicationModel> findByFilter(@RequestBody ApplicationFilter filter);

    @PostMapping(path = "/search/id",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ObjectResponse<ApplicationModel> singleResultFindByFilter(
        @RequestBody @Valid IdApplicationFilter baseParameter);
}
```

### Method-level annotation overrides

`@ApiFind` and `@ApiMapper` can be overridden at method level. Here the type-level binding targets `ExExceptionAudit`, but one method queries `ExUserAssigned` and uses a specific mapper method:

```java
@ApiFindController
@ApiFind(entity = ExExceptionAudit.class, id = Integer.class)
@ApiMapper(ExExceptionAuditMapper.class)
@RequestMapping("/api-exception/exception-audit")
public interface ExceptionAuditProxyController {

    // Uses type-level entity and mapper
    @PostMapping(path = "/search",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CollectionResponse<ExceptionAuditModel> findByFilter(
        @RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

    // Overrides entity → queries ExUserAssigned
    // Overrides mapper method → calls convertToModel instead of auto-resolved method
    @PostMapping(path = "/search/by-user",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ApiFind(entity = ExUserAssigned.class, id = Integer.class)
    @ApiMapper(value = ExExceptionAuditMapper.class, method = "convertToModel")
    CollectionResponse<ExceptionAuditModel> findByUser(
        @RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal
        @Param("email")
        @LikeString(likeType = LikeType.NONE, upperLowerType = UpperLowerType.UPPER)
        ExUserSecurity exUserSecurity);

    // Single result by ID
    @PostMapping(path = "/search/id",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ObjectResponse<ExceptionAuditModel> searchById(@RequestBody IdExceptionAuditModel baseParameter);
}
```

### Named query with @ApiQuery

When the standard filter-based path is insufficient, `@ApiQuery` executes a fixed JPQL string defined as a constant on the service interface. `@DefaultOrderBy` sets the sort when the client provides none.

```java
// On the service interface:
public interface ExApplicationService extends JpaService<ExApplication, Integer> {
    String NAME_APPLICATION = "ExApplication.findNamesByUser";
    // ...
}

// In the controller:
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@ApiMapper(ExApplicationMapper.class)
@RequestMapping("/api-exception/application")
public interface ApplicationProxyController {

    // No @RequestBody — method takes no filter input
    @PostMapping(path = "/all/names",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
    @ResponseBody
    CollectionResponse<ApplicationModel> searchName();

    // Same query but restricted to the authenticated user's applications via @ConditionsZone
    @PostMapping(path = "/grant/all/names",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable("allNames-user")
    @ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
    @ResponseBody
    CollectionResponse<ApplicationModel> searchName(
        @AuthenticationPrincipal
        @Param("email")
        @ConditionsZones(@ConditionsZone(key = "appCondition"))
        @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
        ExUserSecurity exUserSecurity);
}
```

### BeforeFind hook with @AuthenticationPrincipal

A `BeforeFind` hook is the standard way to inject security-derived parameters (IDs, tenant context, etc.) into the query **before** it runs. The authenticated principal is passed via `args[0]`:

```java
// Controller method — hook declared, principal marked @IgnoreMapping
@PostMapping(path = "/grant/search",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@ApiBeforeFind(GrantHandlerFindRequest.class)
CollectionResponse<ApplicationModel> findByFilter(
    @RequestBody ApplicationFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

// Hook implementation — resolves allowed IDs from the authenticated user's email
@Component
public class GrantHandlerFindRequest implements BeforeFind<ExApplication, Integer> {

    @Autowired
    private ExProjectService exProjectService;

    @Override
    public void before(BaseQueryParameter<ExApplication, Integer> parameters, Object... args) {
        ExUserSecurity exUserSecurity = (ExUserSecurity) args[0];
        parameters.addParameter(
            ExApplicationQueryJpql.idProjectGrant,
            exProjectService.findIdProjectByEmail(exUserSecurity.getUsername())
        );
    }
}
```

The `GrantHandlerFindRequest` bean injects a list of project IDs the authenticated user has access to. The `@QueryBuilder` on the service declares a matching `@ConditionBuilder` that activates this parameter in the WHERE clause.

### AfterFind hook to enrich results

`AfterFind` runs after the query completes and can call other services to enrich the response. The request body (`args[0]`) and other method arguments are available:

```java
// Controller method — hook declared on the method
@PostMapping(path = "/search/id",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@ApiAfterFind(UserHandlerFindRequest.class)
ObjectResponse<UserModel> findById(@RequestBody @Valid IdUserFilter idUserFilter);

// Hook implementation — loads role and projects and enriches the response
@Component
public class UserHandlerFindRequest implements AfterFind<ObjectResponse<UserModel>> {

    @Autowired
    private ExAssUserRoleService exAssUserRoleService;

    @Autowired
    private ExProjectService exProjectService;

    @Override
    public ObjectResponse<UserModel> after(ObjectResponse<UserModel> response, Object... args) {
        IdUserFilter filter = (IdUserFilter) args[0];

        QueryParameter<ExAssUserRole, ExAssUserRolePK> queryUserRole = new QueryParameter<>();
        queryUserRole.addParameter(ExAssUserRoleQueryJpql.idUser, response.getData().getId());
        ExAssUserRole userRole = exAssUserRoleService.singleResultByFilter(queryUserRole);

        response.getData().setIdRole(userRole.getId().getIdRole());
        response.getData().setPriority(userRole.getExRole().getPriority());

        List<Integer> idProjects = exProjectService.findIdProjectByIdUser(filter.getIdUser());
        if (CollectionUtils.isEmpty(idProjects))
            idProjects.add(-1);
        response.getData().setIdProject(idProjects);

        return response;
    }
}
```

### Filter class with IDFilterParameter

`IDFilterParameter<ID>` is a convenience base class that adds an `id` list field (bound to an `IN (...)` condition). Override the JSON property name to give it a domain-specific name:

```java
public class ApplicationFilter extends IDFilterParameter<Integer> {

    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    private String name;

    private List<Integer> idEnvironment;

    private List<Integer> idProject;

    // This field is populated by GrantHandlerFindRequest — hidden from JSON deserialization
    @JsonIgnoreProperties("idProjectGrant")
    private List<Integer> idProjectGrant;

    @LikeString
    private String version;

    // Rename the inherited 'id' field in JSON to the domain name
    @Override
    @JsonProperty("idApplication")
    public List<Integer> getId() { return super.getId(); }

    @Override
    @JsonProperty("idApplication")
    public void setId(List<Integer> id) { super.setId(id); }
}
```

---

## Complete end-to-end example

This example shows the full stack: entity, service, filter, mapper, and controller.

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
    // getters / setters
}
```

**Service interface and implementation**

The `@QueryBuilder` annotation on the implementation drives compile-time JPQL generation. Field paths follow JPQL dot-notation and can traverse multiple relationships.

```java
public interface ProductService extends JpaService<Product, Long> { }

@Service
@Transactional
@QueryBuilder(
    distinct = true,
    conditions = {
        @ConditionBuilder(field = "product.name",   operation = OperationType.LIKE,
                          parameter = "name",       upperLower = UpperLowerType.LOWER),
        @ConditionBuilder(field = "product.active", operation = OperationType.EQUAL,
                          parameter = "active"),
        @ConditionBuilder(field = "product.category.idCategory", operation = OperationType.IN,
                          parameter = "idCategory")
    },
    jpaOrder = {
        @JpqlOrderBuilder(key = "name",     order = "product.name"),
        @JpqlOrderBuilder(key = "category", order = "product.category.name")
    }
)
public class ProductServiceImpl
        extends JpaServiceImpl<Product, Long>
        implements ProductService {

    @Autowired private ProductRepository productRepository;
    @PersistenceContext private EntityManager entityManager;

    @Override
    protected JpaRepository<Product, Long> getJpaRepository() { return productRepository; }
    @Override
    protected EntityManager getEntityManager() { return entityManager; }
}
```

**Filter**

```java
public class ProductFilter extends BaseParameter {

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;

    private Boolean active;

    @ListFilter
    private List<Long> idCategory;

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

    @PostMapping(path = "/search",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CollectionResponse<ProductDto> search(@RequestBody ProductFilter filter);

    @PostMapping(path = "/search/id",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ObjectResponse<ProductDto> findById(@RequestBody @Valid IdProductFilter filter);

    @GetMapping("/count")
    long count(@RequestBody ProductFilter filter);
}
```

That is the entire controller implementation. No `@RestController` class needed.
