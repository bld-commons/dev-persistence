# Changelog

All notable changes to **dev-persistence** are documented in this file.

---

## [3.0.19] - 2026-05-06

### Changed
- Renamed annotation `@ListFilter` → `@ConditionTrigger` (`common-jpa-service` — `com.bld.commons.reflection.annotations.ConditionTrigger`). The annotation is used to mark a `Boolean` field that, when `true`, activates a value-less JPQL condition (`IS NULL` / `IS NOT NULL`) via `QueryParameter.addNullable(fieldName)`. Old name was misleading because it suggested a `Collection`/`IN (…)` mapping, while the actual runtime behavior is a Boolean condition trigger. Updated references in `ReflectionCommons`, `FindInterceptor`, `BaseParameter` (Javadoc), and documentation. **Breaking change**: callers using `@ListFilter` must rename the import and annotation to `@ConditionTrigger`.

---

## [3.0.18] - 2026-05-04

### Added
- `@DateFilter` now supports `java.time.Instant`, `java.time.LocalDate`, `java.time.LocalDateTime`, and `java.time.OffsetDateTime` in addition to the existing `Date`, `Calendar`, and `Timestamp` types (`common-jpa-service` — `ReflectionCommons.value()`)
- `processor-jpa-service` (`ClassBuilding`) generates range conditions (`fieldFrom <=`, `<= fieldTo`, `= field`) for `Instant`, `LocalDate`, `LocalDateTime`, and `OffsetDateTime` entity fields instead of falling back to the generic `IN` clause
- Hibernate type binding entries added for `Instant` (`INSTANT`), `LocalDate` (`LOCAL_DATE`), `LocalDateTime` (`LOCAL_DATE_TIME`), and `OffsetDateTime` (`OFFSET_DATE_TIME`) in `ReflectionCommons.mapType`

---

## [3.0.17] - 2026-04-14

### Fixed
- `JpaServiceImpl`: eliminated duplicate key lookup in `mapKeyListFindByFilter` — result value was retrieved twice using the raw value instead of the cached local variable

### Changed
- `PersistenceMap`: complete Javadoc rewrite documenting JPA-aware key comparison semantics, usage examples, and difference from `java.util.Map`

### Added
- `ProxyControllerSwaggerConfig`: new `@Configuration` that fixes Swagger/SpringDoc tag names for `@ApiFindController` JDK proxy beans — replaces auto-generated proxy names (e.g. `$-proxy-265`) with the kebab-case interface name (e.g. `service-type-find-controller`); activated only when SpringDoc is on the classpath

---

## [3.0.16] - 2026-03-30

### Changed
- `FindInterceptor`: major rework — improved parameter resolution, request handling, and `@DateFilter` / `@LikeString` binding logic
- `ApiFindInterceptor`: extended proxy interception logic
- `ParameterDetails`: enriched with additional resolution fields
- Comprehensive Javadoc added to all public classes and methods across all modules
- Italian README translations added for all modules (`README.it.md`)

### Added
- `ApiFindException`: new dedicated exception for proxy API controller errors

---

## [3.0.15] - 2026-02-16

### Changed
- `JdbcTemplateServiceImpl`: refactored internal result mapping and zone handling
- `JdbcTemplateService`: interface cleanup and method signature improvements
- `JdbcRowMapper` / `JpaRowMapper`: added missing null-safety and result mapping improvements

---

## [3.0.14] - 2026-01-18

### Added
- `JpaService`: new interface methods for additional query patterns
- `JpaServiceImpl`: extended implementation to support new service methods

### Changed
- `GenereMapper` and `GenereController` in the example app updated to reflect new service capabilities

---

## [3.0.13] - 2026-01-16

### Added
- `BaseQueryParameter`: new fields and helpers for zone-aware native query building
- `NativeQueryParameter`: support for additional zone routing patterns

### Fixed
- `ReflectionCommons`: minor correction in parameter mapping logic
- `JdbcTemplateServiceImpl`: reworked zone-based condition assembly to avoid incorrect SQL generation

---

## [3.0.12] - 2026-01-14

### Added
- `JdbcTemplateService` interface and `JdbcTemplateServiceImpl`: new service abstraction for executing native SQL via `JdbcTemplate` with the same zone-based condition map approach used by JPQL queries
- `NativeQueryParameter`: extended with additional fields to support the new `JdbcTemplateService`

### Changed
- `BaseJpaService`: refactored internal JPQL assembly to reduce duplication and improve zone handling

---

## [3.0.11] - 2026-01-12

### Added
- `BaseQueryParameter`: new convenience methods for building query parameter maps

### Changed
- `BaseJpaService`: improved handling of `ONE_TO_MANY` join injection and nullable condition resolution
- `ReflectionCommons`: minor cleanup in field reflection logic

---

## [3.0.10] - 2026-01-12

### Fixed
- `ReflectionCommons`: corrected edge case in `dataToMap` when processing fields with `@FilterNullValue`

---

## [3.0.9] - 2026-01-09

### Changed
- `ApiFindRegistrar`: improved bean registration logic for proxy controllers
- Removed stale `MavenWrapperDownloader.java` files from `processor-jpa-service` and `proxy-api-controller`
- `JpaProcessor`: minor cleanup of annotation processor entry point

### Added *(retroactively included — committed 2025-04-15)*
- `BeforeFind`: new hook interface for pre-execution logic in proxy API controllers
- `ApiBeforeFind` / `ApiAfterFind`: renamed from `ApiBeforeRequest` / `ApiAfterRequest` for naming consistency
- `FindInterceptor`: updated to invoke the new `BeforeFind` hook and handle renamed interfaces

---

## [3.0.8] - 2025-03-08

### Fixed
- `JpaProcessor`: corrected annotation processor incremental compilation issue causing missed `@QueryBuilder` types in multi-round processing

---

## [3.0.7] - 2025-02-19

### Changed
- `ClassBuilding`: improved handling of `@Enumerated` fields in generated `QueryJpqlImpl` — enum comparisons now produce correct `IN` conditions consistently

---

## [3.0.6] - 2024-10-15

### Added
- `@TupleComparison` annotation and `TupleParameter` model: support for tuple-based WHERE conditions (multi-column equality in a single predicate)
- `NativeGenereParameter` in the example app demonstrating native SQL zone parameters
- `GenereTuple` example showing `@TupleComparison` usage

### Changed
- `FindInterceptor`: extended to resolve `@TupleComparison`-annotated parameters and wrap them in `TupleParameter` before query execution

---

## [3.0.5] - 2024-10-12

### Changed
- Full package rename across `common-jpa-service` and `proxy-api-controller`: base package changed from `bld.commons.*` to `com.bld.commons.*` and `com.bld.proxy.*` (146 files changed)
- `FindInterceptor`: cleaned up dead code and aligned with new package structure
- Example app updated to use new package names

---

## [3.0.4] - 2024-10-11

### Fixed
- `BaseJpaService`: corrected `ONE_TO_MANY` join injection when no join conditions are present
- `JpaProcessor` / `ClassBuilding`: fixed annotation processor crash on certain entity hierarchies with mixed `@Id` / `@EmbeddedId` declarations

---

## [3.0.3] - 2024-04-10

### Changed
- `FindInterceptor`: major refactor — simplified request dispatch and reduced coupling between HTTP parameter resolution and JPQL execution
- Removed `StaticApplicationContext` helper (context lookup replaced by standard Spring autowiring)
- Removed `ApiMethod` data class (functionality inlined into interceptor)

---

## [3.0.2] - 2024-04-05

### Added
- `StaticApplicationContext`: helper for non-Spring-managed lookup of application context beans
- `ApiMethod`: data class encapsulating resolved HTTP method metadata for proxy interceptors
- `ApiFindInterceptor`: extended to support additional HTTP methods via `ApiMethod`

### Changed
- `FindInterceptor`: reworked request routing to use the new `ApiMethod` abstraction

---

## [3.0.1] - 2024-03-30

### Fixed
- `ClassBuilding`: removed stale import and unused variable that caused a compile error in certain processor configurations

---

## [3.0.0] - 2024-02-12

### Changed
- **Jakarta EE migration**: all `javax.*` imports replaced with `jakarta.*` throughout the framework — requires Spring Boot 3.x / Jakarta EE 10
- `FindInterceptor`: major refactor for Spring Boot 3 compatibility — updated `HttpServletRequest` handling, parameter binding, and service lookup
- `ApiMapper`: extended with additional mapping strategies to support complex DTO transformations
- `QueryParameter`: internal field renamed from `baseParameter` to `filterParameter`

---

## [2.0.9] - 2023-09-28

### Changed
- `ClassBuilding`: improved generated `QueryJpqlImpl` — better handling of inherited entity fields and multi-level `@ManyToOne` joins
- `FindInterceptor`: minor cleanup, removed `javax.validation` dependency
- `PerformanceSearchController`: removed `@Valid` import no longer needed after validation refactor

---

## [2.0.8] - 2023-04-18

### Changed
- Internal snapshot stabilisation; no API-level changes

---

## [2.0.7] - 2023-02-07

### Changed
- Internal development snapshot

---

## [2.0.6] - 2023-01-31

### Changed
- Dependency and build alignment fixes

---

## [2.0.5] - 2023-01-28

### Changed
- Minor service layer adjustments

---

## [2.0.4] - 2023-01-27

### Changed
- Internal refactor pass

---

## [2.0.3] - 2023-01-13

### Added
- **`proxy-api-controller` module** (new): declarative REST controller generation via JDK dynamic proxies — annotate an interface with `@ApiFindController` and `@ApiFindSearch` to expose fully functional search endpoints without writing controller code
- `ApiFindInterceptor`: core invocation handler that intercepts proxy method calls and delegates to the correct `JpaService`
- `FindInterceptor`: resolves HTTP request parameters, applies `@DateFilter` / `@LikeString` transformations, builds `QueryParameter`, and executes the query
- `ApiFindException`: dedicated exception type for proxy controller errors
- `@EnableApiFindController` and `ApiFindRegistrar`: Spring `ImportBeanDefinitionRegistrar` that auto-registers all `@ApiFindController` interfaces as Spring beans

---

## [2.0.2] - 2022-10-22

### Added
- `BaseCliente` and `BaseEntity` base classes in the example app for shared entity fields
- Extended reflection support for complex entity hierarchies in `ReflectionCommons`

### Changed
- `ClienteServiceImpl`: updated query logic to leverage new base entity structure

---

## [2.0.1] - 2022-08-01

### Changed
- Service layer fixes: corrected `TipoUtenteServiceImpl` and `UtenteServiceImpl` after base class restructuring
- `ConfiguraMenuFilter`: parameter binding correction

---

## [2.0.0] - 2022-07-01

### Changed
- Spring Boot 2.7 compatibility fixes
- `BaseJpaService`: aligned Hibernate 5 / Spring Data JPA API calls
- `JpaServiceImpl`: minor signature alignment
- `ReflectionCommons`: removed deprecated utility call

---

## [1.1.4] - 2022-06-30

### Changed
- Reflection layer improvements for field/method discovery in deep class hierarchies

---

## [1.1.3] - 2022-06-24

### Changed
- `ServiceJpaGeneratorPlugin`: improved entity scanning, better handling of abstract base entities
- `ClassBuilding` (plugin generator): improved service/repository class generation templates
- `ClassBuilding` (processor): improved handling of `@OneToMany` / `@ManyToMany` condition generation

---

## [1.1.2] - 2022-03-31

### Changed
- `ReflectionUtils`: refactored field discovery and getter/setter resolution
- `ServiceJpaGeneratorPlugin`: updated scaffolding templates
- `ClassBuilding` (processor): major rewrite of JPQL condition generation for join paths and `@ConditionBuilder`

---

## [1.1.1] - 2022-03-08

### Fixed
- Persistence layer stability fixes

---

## [1.1.0] - 2021-12-19

### Changed
- `processor-jpa-service` (`ClassBuilding`): improved generation of `mapOneToMany`, `mapConditions`, and order maps — better handling of deep join paths and nullable associations
- `jpa-service-plugin-generator` (`ClassBuilding`): template alignment with updated service API
- `JpaProcessor`: removed redundant rounds configuration

---

## [1.0.5] - 2021-09-22

### Added
- Major expansion of the example application (`project-jpa-persistence`) with realistic multi-entity scenarios
- `UtenteServiceImpl`: new service demonstrating cross-entity query patterns

---

## [1.0.4] - 2021-07-31

### Changed
- `DateUtils`: corrected week/day offset arithmetic in `sumDate` for `Calendar` and `Date`
- `ReflectionUtils`: improved handling of overridden getter methods
- `BaseJpaService`: refactored pageable query assembly and count query generation

---

## [1.0.3] - 2021-07-23

### Added
- `JpaService`: new `deleteByFilter`, `countByFilter`, and `singleResultByFilter` methods
- `JpaServiceImpl`: implemented new interface methods with JPQL query delegation

---

## [1.0.2] - 2021-07-20

### Added
- `JpaService` / `JpaServiceImpl`: `mapKeyFindByFilter` and `mapKeyListFindByFilter` — result grouping by a dot-notation entity field path using `PersistenceMap`
- Jackson-aware result handling in `JdbcServiceImpl`

---

## [1.0.1] - 2021-07-15

### Fixed
- `EnableJpaServiceConfiguration`: removed incorrect extra `@ComponentScan` path that caused double bean registration on application startup

---

## [1.0.0] - 2021-05-20

### Added
- Initial public release
- `common-jpa-service`: dynamic JPQL/native SQL query execution engine, `@DateFilter`, `@LikeString`, `@ListFilter`, `@FieldMapping`, `@IgnoreMapping`, `@FilterNullValue` annotations, `JpaService` / `JpaServiceImpl` / `BaseJpaService`, `QueryParameter`, `NativeQueryParameter`, `PersistenceMap`, `BaseSearchController`, `PerformanceSearchController` (note: `@ListFilter` later renamed to `@ConditionTrigger` in 3.0.19)
- `processor-jpa-service`: annotation processor that reads `@QueryBuilder`-annotated service interfaces and generates `*QueryJpqlImpl` classes with pre-built JPQL strings and condition maps
- `jpa-service-plugin-generator`: Maven plugin that scaffolds `*Service`, `*ServiceImpl`, and `*Repository` for each JPA entity — `ServiceJpaGeneratorPlugin` (renamed from `ServiceJpaPlugin`)
- `project-jpa-persistence`: example Spring Boot application demonstrating the complete framework
