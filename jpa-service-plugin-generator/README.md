# jpa-service-plugin-generator

Maven plugin that scans a package of JPA entity classes and generates the corresponding `*Service` interface, `*ServiceImpl` class, and `*Repository` interface for each entity. Run it once to bootstrap the service layer — the generated files land in `src/main/java` and are then yours to modify.

---

## Table of Contents

1. [Overview](#overview)
2. [What gets generated](#what-gets-generated)
3. [Plugin configuration](#plugin-configuration)
4. [Parameters reference](#parameters-reference)
5. [Running the plugin](#running-the-plugin)
6. [Generated file structure](#generated-file-structure)
7. [After generation](#after-generation)

---

## Overview

The plugin runs in the `generate-sources` Maven lifecycle phase. It reads the source files in the entity package (using AST parsing — no compilation required), extracts the entity class name and primary-key type, and writes three Java source files per entity using FreeMarker templates.

Because the output goes to `src/main/java` (or a configured directory), the files are committed to version control and edited freely. The plugin will not overwrite files that already exist — subsequent builds are safe.

---

## What gets generated

For each entity class found in `persistencePackage`, the plugin produces:

### `*Repository` — Spring Data repository interface

```java
@Repository
public interface ProductRepository extends BaseJpaRepository<Product, Long> {
}
```

`BaseJpaRepository<T, ID>` extends `JpaRepository<T, ID>` and is the standard repository type used by the framework.

### `*Service` — Service interface

```java
public interface ProductService extends JpaService<Product, Long> {
}
```

Extend this interface to add custom service methods beyond the standard CRUD / filter-based ones.

### `*ServiceImpl` — Service implementation

```java
@Service
@Transactional
@QueryBuilder
public class ProductServiceImpl extends JpaServiceImpl<Product, Long>
        implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected JpaRepository<Product, Long> getJpaRepository() {
        return this.productRepository;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }
}
```

The `@QueryBuilder` annotation is placed with no attributes — add conditions, joins, and order definitions to it after the file is generated (see `processor-jpa-service` documentation).

---

## Plugin configuration

Add the plugin to the `<build><plugins>` section of your `pom.xml`:

```xml
<plugin>
    <groupId>com.bld.commons</groupId>
    <artifactId>jpa-service-plugin-generator</artifactId>
    <version>3.0.18</version>
    <executions>
        <execution>
            <goals>
                <goal>jpa-service-generator</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <persistencePackage>com.example.domain</persistencePackage>
        <servicePackage>com.example.service</servicePackage>
        <basePackage>com.example</basePackage>
        <!-- optional -->
        <repositoryPackage>com.example.repository</repositoryPackage>
        <outputDirectory>src_main_java</outputDirectory>
    </configuration>
</plugin>
```

---

## Parameters reference

| Parameter | Required | Default | Description |
|-----------|----------|---------|-------------|
| `persistencePackage` | Yes | — | Fully qualified package containing the JPA entity classes to scan |
| `servicePackage` | Yes | — | Target package where `*Service` and `*ServiceImpl` files are written |
| `basePackage` | Yes | — | Root package of your application (used for output path resolution) |
| `repositoryPackage` | No | Derived from entity package | Target package where `*Repository` files are written. Defaults to the entity class package with the class name segment removed |
| `outputDirectory` | No | `src_main_java` | Output directory type. `src_main_java` writes directly into `src/main/java` |
| `resourceTemplateDirectory` | No | `/template` | Location of the FreeMarker templates inside the plugin JAR |

### repositoryPackage default behaviour

If `repositoryPackage` is not set, the repository is written into the same package as the entity. For example, if an entity is `com.example.domain.Product`, the repository will be `com.example.domain.ProductRepository`.

To keep repositories in a separate package, set `repositoryPackage` explicitly:

```xml
<repositoryPackage>com.example.repository</repositoryPackage>
```

---

## Running the plugin

The plugin is bound to the `generate-sources` phase and runs automatically during a normal build. To trigger it in isolation:

```bash
mvn com.bld.commons:jpa-service-plugin-generator:jpa-service-generator
```

Or, if the plugin is in your POM under `<pluginManagement>`:

```bash
mvn generate-sources
```

Files are written only if they do not already exist. Add the output directories to `.gitignore` if you prefer to regenerate them, or commit them to version control to track your customisations.

---

## Generated file structure

Given `persistencePackage = com.example.domain`, `servicePackage = com.example.service`, `repositoryPackage = com.example.repository`, and entities `Product`, `Order`, `Customer`:

```
src/main/java/
  com/example/
    domain/
      Product.java          ← your entity (untouched)
      Order.java
      Customer.java
    repository/
      ProductRepository.java   ← generated
      OrderRepository.java     ← generated
      CustomerRepository.java  ← generated
    service/
      ProductService.java      ← generated
      ProductServiceImpl.java  ← generated
      OrderService.java        ← generated
      OrderServiceImpl.java    ← generated
      CustomerService.java     ← generated
      CustomerServiceImpl.java ← generated
```

---

## After generation

Once the files are generated:

1. **Add `@QueryBuilder` attributes** to each `*ServiceImpl` to define conditions, joins, and sort keys. The annotation processor (`processor-jpa-service`) reads these to generate the `*QueryJpqlImpl` class.

2. **Add custom methods** to the `*Service` interface and implement them in `*ServiceImpl` as needed.

3. **Configure the annotation processor** in the Maven compiler plugin so the `@QueryBuilder` annotation on each `*ServiceImpl` is picked up at compile time:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>com.bld.commons</groupId>
                <artifactId>processor-jpa-service</artifactId>
                <version>3.0.18</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

See the [processor-jpa-service documentation](../processor-jpa-service/README.md) for the full `@QueryBuilder` attribute reference.
