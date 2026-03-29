# project-jpa-persistence

Example Spring Boot application demonstrating the complete `dev-persistence` framework: entity definition, service generation, annotation-processor-based query building, and declarative REST controllers via dynamic proxies.

---

## Table of Contents

1. [Purpose](#purpose)
2. [Prerequisites](#prerequisites)
3. [Project structure](#project-structure)
4. [Step-by-step walkthrough](#step-by-step-walkthrough)
   - [1. Define a JPA entity](#1-define-a-jpa-entity)
   - [2. Generate the service layer](#2-generate-the-service-layer)
   - [3. Add @QueryBuilder attributes](#3-add-querybuilder-attributes)
   - [4. Define the filter object](#4-define-the-filter-object)
   - [5. Create the REST controller](#5-create-the-rest-controller)
5. [Configuration](#configuration)
6. [Building and running](#building-and-running)

---

## Purpose

This module is not a library — it is a working example and integration test for the other four modules. It shows:

- How to configure `jpa-service-plugin-generator` to scaffold service/repository classes.
- How to configure `processor-jpa-service` as an annotation processor so `@QueryBuilder` generates `*QueryJpqlImpl` at compile time.
- How to use `@ApiFindController` to expose REST endpoints without writing controller logic.
- How to use `@ApiMapper` with MapStruct to convert entities to DTOs.

---

## Prerequisites

- Java 17+
- Maven 3.6+
- A running PostgreSQL instance (configure in `application.yml`)
- The four framework modules installed locally (`mvn install` from the root)

---

## Project structure

```
project-jpa-persistence/
├── pom.xml
└── src/
    └── main/
        ├── java/com/bld/persistence/
        │   ├── core/
        │   │   ├── domain/          ← JPA entities
        │   │   ├── repository/      ← generated repositories (committed)
        │   │   └── service/         ← generated services (committed, then customised)
        │   ├── controller/          ← @ApiFindController interfaces
        │   └── mapper/              ← MapStruct mappers
        └── resources/
            ├── application.yml
            └── sql/                 ← native SQL templates (optional)
```

---

## Step-by-step walkthrough

### 1. Define a JPA entity

```java
// src/main/java/com/bld/persistence/core/domain/Product.java

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private Boolean active;
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // getters / setters
}
```

### 2. Generate the service layer

Run the plugin once to scaffold the service and repository classes:

```bash
mvn generate-sources
```

The plugin reads `pom.xml` configuration and writes into `src/main/java`:

```
core/repository/ProductRepository.java
core/service/ProductService.java
core/service/ProductServiceImpl.java
```

These files are only created if they do not already exist.

### 3. Add @QueryBuilder attributes

Open the generated `ProductServiceImpl.java` and fill in the `@QueryBuilder` annotation:

```java
@Service
@Transactional
@QueryBuilder(
    distinct = true,
    joins    = { "product.category" },  // always JOIN FETCH category
    conditions = {
        @ConditionBuilder(field = "product.name",     operation = OperationType.LIKE,
                          parameter = "name",          upperLower = UpperLowerType.LOWER),
        @ConditionBuilder(field = "product.active",   operation = OperationType.EQUALS,
                          parameter = "active"),
        @ConditionBuilder(field = "product.category.id", operation = OperationType.IN,
                          parameter = "categoryIds")
    },
    customConditions = {
        @CustomConditionBuilder(
            condition = "and product.price >= :minPrice",
            parameter = "minPrice"
        )
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name",  field = "product.name"),
        @JpqlOrderBuilder(sortKey = "price", field = "product.price")
    }
)
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

On the next `mvn compile`, the annotation processor generates `ProductQueryJpqlImpl.java` in `target/generated-sources/annotations`.

### 4. Define the filter object

```java
public class ProductFilter extends BaseParameter {

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;

    private Boolean active;

    @ListFilter
    private List<Long> categoryIds;

    private BigDecimal minPrice;

    // getters / setters
}
```

### 5. Create the REST controller

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

    @GetMapping("/{id}")
    ProductDto findById(@PathVariable Long id);
}
```

No implementation class is needed. The proxy intercepts every call at runtime.

---

## Configuration

`pom.xml` excerpt showing the plugin and annotation processor setup:

```xml
<dependencies>
    <dependency>
        <groupId>com.bld.commons</groupId>
        <artifactId>common-jpa-service</artifactId>
        <version>3.0.16</version>
    </dependency>
    <dependency>
        <groupId>com.bld.commons</groupId>
        <artifactId>proxy-api-controller</artifactId>
        <version>3.0.16</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <!-- MapStruct -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                    <!-- Lombok (if used) -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                    <!-- QueryJpqlImpl generator -->
                    <path>
                        <groupId>com.bld.commons</groupId>
                        <artifactId>processor-jpa-service</artifactId>
                        <version>3.0.16</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <plugin>
            <groupId>com.bld.commons</groupId>
            <artifactId>jpa-service-plugin-generator</artifactId>
            <version>3.0.16</version>
            <executions>
                <execution>
                    <goals>
                        <goal>jpa-service-generator</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <persistencePackage>com.bld.persistence.core.domain</persistencePackage>
                <repositoryPackage>com.bld.persistence.core.repository</repositoryPackage>
                <servicePackage>com.bld.persistence.core.service</servicePackage>
                <basePackage>com.bld.persistence</basePackage>
            </configuration>
        </plugin>
    </plugins>
</build>
```

`application.yml` minimum configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: myuser
    password: mypassword
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
```

---

## Building and running

```bash
# Install framework modules (from repository root)
mvn install -DskipTests

# Build and run the example project
cd project-jpa-persistence
mvn spring-boot:run
```

The application starts on port `8080`. Test the generated endpoint:

```bash
curl -X POST http://localhost:8080/api/products/search \
     -H "Content-Type: application/json" \
     -d '{"name": "widget", "active": true, "pageSize": 10, "pageNumber": 0}'
```
