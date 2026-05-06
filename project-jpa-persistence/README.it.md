# project-jpa-persistence

Applicazione Spring Boot di esempio che dimostra il framework `dev-persistence` completo: definizione delle entità, generazione del service layer, costruzione delle query basata su annotation processor, e controller REST dichiarativi tramite proxy dinamici.

---

## Indice dei contenuti

1. [Scopo](#scopo)
2. [Prerequisiti](#prerequisiti)
3. [Struttura del progetto](#struttura-del-progetto)
4. [Guida passo dopo passo](#guida-passo-dopo-passo)
   - [1. Definire un'entità JPA](#1-definire-unentità-jpa)
   - [2. Generare il service layer](#2-generare-il-service-layer)
   - [3. Aggiungere gli attributi @QueryBuilder](#3-aggiungere-gli-attributi-querybuilder)
   - [4. Definire l'oggetto filtro](#4-definire-loggetto-filtro)
   - [5. Creare il controller REST](#5-creare-il-controller-rest)
5. [Configurazione](#configurazione)
6. [Build ed esecuzione](#build-ed-esecuzione)

---

## Scopo

Questo modulo non è una libreria — è un esempio funzionante e un test di integrazione per gli altri quattro moduli. Mostra:

- Come configurare `jpa-service-plugin-generator` per fare lo scaffolding delle classi service/repository.
- Come configurare `processor-jpa-service` come annotation processor affinché `@QueryBuilder` generi `*QueryJpqlImpl` a tempo di compilazione.
- Come usare `@ApiFindController` per esporre endpoint REST senza scrivere logica nei controller.
- Come usare `@ApiMapper` con MapStruct per convertire entità in DTO.

---

## Prerequisiti

- Java 17+
- Maven 3.6+
- Un'istanza PostgreSQL in esecuzione (da configurare in `application.yml`)
- I quattro moduli del framework installati localmente (`mvn install` dalla root)

---

## Struttura del progetto

```
project-jpa-persistence/
├── pom.xml
└── src/
    └── main/
        ├── java/com/bld/persistence/
        │   ├── core/
        │   │   ├── domain/          ← entità JPA
        │   │   ├── repository/      ← repository generati (committati)
        │   │   └── service/         ← servizi generati (committati, poi personalizzati)
        │   ├── controller/          ← interfacce @ApiFindController
        │   └── mapper/              ← mapper MapStruct
        └── resources/
            ├── application.yml
            └── sql/                 ← template SQL nativo (opzionale)
```

---

## Guida passo dopo passo

### 1. Definire un'entità JPA

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

    // getter / setter
}
```

### 2. Generare il service layer

Esegui il plugin una volta per fare lo scaffolding delle classi service e repository:

```bash
mvn generate-sources
```

Il plugin legge la configurazione del `pom.xml` e scrive in `src/main/java`:

```
core/repository/ProductRepository.java
core/service/ProductService.java
core/service/ProductServiceImpl.java
```

Questi file vengono creati solo se non esistono già.

### 3. Aggiungere gli attributi @QueryBuilder

Apri il `ProductServiceImpl.java` generato e compila l'annotazione `@QueryBuilder`:

```java
@Service
@Transactional
@QueryBuilder(
    distinct = true,
    joins    = { "product.category" },  // sempre JOIN FETCH category
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

Al prossimo `mvn compile`, l'annotation processor genera `ProductQueryJpqlImpl.java` in `target/generated-sources/annotations`.

### 4. Definire l'oggetto filtro

```java
public class ProductFilter extends BaseParameter {

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;

    private Boolean active;

    private List<Long> categoryIds;

    @ConditionTrigger
    private Boolean deletedAtIsNull;

    private BigDecimal minPrice;

    // getter / setter
}
```

### 5. Creare il controller REST

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

Non è necessaria alcuna classe di implementazione. Il proxy intercetta ogni chiamata a runtime.

---

## Configurazione

Estratto del `pom.xml` che mostra la configurazione del plugin e dell'annotation processor:

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
                    <!-- Lombok (se usato) -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                    <!-- Generatore QueryJpqlImpl -->
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

Configurazione minima di `application.yml`:

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

## Build ed esecuzione

```bash
# Installa i moduli del framework (dalla root del repository)
mvn install -DskipTests

# Build ed esecuzione del progetto di esempio
cd project-jpa-persistence
mvn spring-boot:run
```

L'applicazione si avvia sulla porta `8080`. Testa l'endpoint generato:

```bash
curl -X POST http://localhost:8080/api/products/search \
     -H "Content-Type: application/json" \
     -d '{"name": "widget", "active": true, "pageSize": 10, "pageNumber": 0}'
```
