# dev-persistence — Documentazione per Sviluppatori

**Versione:** 3.0.16 | **Java:** 17+ | **Spring Boot:** 3.x | **Licenza:** MIT

Un framework multi-modulo che elimina il boilerplate JPA tramite generazione di codice a tempo di compilazione e costruzione dinamica di query a runtime.

---

## Indice dei contenuti

1. [Panoramica dell'architettura](#panoramica-dellarchitettura)
2. [Moduli](#moduli)
3. [common-jpa-service](#common-jpa-service)
   - [Per iniziare](#per-iniziare)
   - [JpaService API](#jpaservice-api)
   - [Costruzione dinamica delle query](#costruzione-dinamica-delle-query)
   - [BaseParameter e annotazioni di filtro](#baseparameter-e-annotazioni-di-filtro)
   - [QueryParameter](#queryparameter)
   - [Query SQL nativo](#query-sql-nativo)
4. [processor-jpa-service](#processor-jpa-service)
   - [@QueryBuilder](#querybuilder)
   - [@ConditionBuilder](#conditionbuilder)
   - [Codice generato](#codice-generato)
5. [jpa-service-plugin-generator](#jpa-service-plugin-generator)
6. [proxy-api-controller](#proxy-api-controller)
   - [Configurazione](#configurazione)
   - [Annotazioni](#annotazioni)
   - [Hook](#hook)
7. [Configurazione applicazione](#configurazione-applicazione)
8. [Esempio completo](#esempio-completo)

---

## Panoramica dell'architettura

```
┌─────────────────────────────────────────────────────────┐
│                  La tua App Spring Boot                 │
│                                                         │
│   ┌──────────────────┐   ┌──────────────────────────┐  │
│   │  REST Controllers│   │  proxy-api-controller    │  │
│   │  (manuali)       │   │  (proxy dinamici)        │  │
│   └────────┬─────────┘   └────────────┬─────────────┘  │
│            │                          │                 │
│            └──────────┬───────────────┘                 │
│                       ▼                                 │
│            ┌──────────────────────┐                     │
│            │  JpaService<T, ID>   │  ◄── common-jpa-service
│            │  (JpaServiceImpl)    │                     │
│            └──────────┬───────────┘                     │
│                       │                                 │
│          ┌────────────┴───────────────┐                 │
│          ▼                            ▼                 │
│  ┌───────────────┐          ┌──────────────────────┐   │
│  │ JpaRepository │          │ QueryJpql<T>         │   │
│  │ (Spring Data) │          │ (generato da         │   │
│  └───────────────┘          │  processor/plugin)   │   │
│                             └──────────────────────┘   │
└─────────────────────────────────────────────────────────┘

Strumenti di build
  ├── processor-jpa-service   → genera implementazioni QueryJpql
  └── jpa-service-plugin-generator → genera classi Service / Repository / Mapper
```

Il framework separa le responsabilità in tre layer:

| Layer | Responsabilità |
|---|---|
| **Generazione codice** (build time) | Produce stringhe JPQL type-safe, mappe di condizioni e mappe di ordinamento dalle annotazioni sui servizi delle entità |
| **Esecuzione query** (runtime) | `BaseJpaService` costruisce il JPQL finale aggiungendo i frammenti WHERE/ORDER BY dalle mappe generate |
| **API dinamica** (runtime) | `proxy-api-controller` crea controller Spring MVC da semplici interfacce Java senza alcuna classe di implementazione |

---

## Moduli

| Artifact ID | Tipo | Scopo |
|---|---|---|
| `common-jpa-service` | JAR | Core runtime: service layer, reflection engine, classi modello |
| `processor-jpa-service` | Annotation Processor JAR | Genera `*QueryJpqlImpl` a tempo di compilazione |
| `jpa-service-plugin-generator` | Maven Plugin | Genera file sorgente `Service`, `Repository` e `Mapper` |
| `proxy-api-controller` | JAR | Controller REST dinamici tramite proxy Java |

---

## common-jpa-service

### Per iniziare

Aggiungi la dipendenza:

```xml
<dependency>
    <groupId>com.github.bld-commons</groupId>
    <artifactId>common-jpa-service</artifactId>
    <version>3.0.15</version>
</dependency>
```

Abilita il framework nella tua applicazione Spring Boot:

```java
@SpringBootApplication
@EnableJpaService
public class MyApplication { }
```

`@EnableJpaService` attiva la scansione dei componenti Spring per tutti i bean del framework
(`ReflectionCommons`, `JdbcTemplateService`, ecc.).

---

### JpaService API

`JpaService<T, ID>` è l'interfaccia centrale. Fornisce:

#### CRUD

| Metodo | Descrizione |
|---|---|
| `save(T)` | Persiste una nuova entità |
| `update(T)` | Effettua il merge di un'entità esistente e restituisce l'istanza managed |
| `saveAndFlush(T)` | Persiste e scarica immediatamente sul DB |
| `updateAndFlush(T)` | Fa il merge e scarica immediatamente sul DB |
| `delete(T)` | Elimina una singola entità |
| `deleteAll(Collection<T>)` | Elimina tutte le entità nella collection |
| `deleteById(ID)` | Elimina per chiave primaria |
| `deleteAndFlush(T)` | Elimina e scarica immediatamente sul DB |
| `saveAll(Collection<T>)` | Persiste tutte le entità nella collection |
| `flush()` | Scarica il contesto di persistenza |
| `findById(ID)` | Restituisce l'entità o `null` |
| `findAll()` | Restituisce tutte le entità |
| `count()` | Restituisce il conteggio totale delle entità |

#### Query dinamiche

| Metodo | Descrizione |
|---|---|
| `findByFilter(QueryParameter)` | Query lista con la SELECT generata |
| `findByFilter(QueryParameter, String sql)` | Query lista con una SELECT personalizzata |
| `countByFilter(QueryParameter)` | Query di conteggio |
| `singleResultByFilter(QueryParameter)` | Singola entità o `null` |
| `deleteByFilter(QueryParameter)` | Eliminazione massiva delle entità corrispondenti |
| `mapFindByFilter(QueryParameter)` | Risultati come `Map<ID, T>` |
| `mapKeyFindByFilter(QueryParameter, Class<J>, String key)` | Risultati come `Map<J, T>` indicizzati per un campo personalizzato |
| `mapKeyListFindByFilter(QueryParameter, Class<J>, String key)` | Risultati come `Map<J, List<T>>` |
| `findByFilter(NativeQueryParameter, String sql)` | Query SQL nativo |
| `countByFilter(NativeQueryParameter, String)` | Conteggio SQL nativo |
| `singleResultByFilter(NativeQueryParameter, String)` | Risultato singolo SQL nativo |

> **La paginazione** è supportata su tutte le query lista: imposta `pageSize` e `pageNumber`
> sul `BaseParameter` o direttamente su `QueryParameter`.

---

### Costruzione dinamica delle query

Il framework costruisce le query JPQL dinamicamente a runtime:

1. L'**annotation processor** genera una classe `*QueryJpqlImpl` per ogni entità
   contenente stringhe JPQL statiche e mappe condizioni/ordinamento.
2. `JpaServiceImpl` chiama `ReflectionCommons.dataToMap()` per estrarre i campi non-null
   dal `BaseParameter` in una `Map<String, Object>`.
3. `BaseJpaService` aggiunge solo i frammenti di condizione le cui chiavi sono presenti nella
   mappa dei parametri — così solo i filtri attivi appaiono nella clausola WHERE.
4. Le clausole JOIN FETCH uno-a-molti vengono iniettate solo quando il corrispondente parametro
   di filtro è presente, prevenendo join non necessari e problemi N+1.

**Esempio di flusso:**

```
ProductFilter { name = "Widget", active = true }
    ↓ ReflectionCommons
Map { "name" → "Widget", "active" → true }
    ↓ ricerca mappa condizioni
WHERE  AND e.name LIKE :name
  AND  AND e.active = :active
    ↓ binding parametri
SELECT DISTINCT product FROM Product product WHERE AND product.name LIKE :name AND product.active = :active
```

---

### BaseParameter e annotazioni di filtro

Estendi `BaseParameter` per creare un oggetto filtro tipizzato:

```java
public class ProductFilter extends BaseParameter {

    // Uguaglianza semplice: AND product.name = :name
    private String name;

    // LIKE con wildcard su entrambi i lati, case-insensitive
    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String description;

    // Data spostata di +1 giorno prima del binding: AND p.expiresAt < :expiresAt
    @DateFilter(addDay = 1)
    private Date expiresAt;

    // Se true → attiva condizione senza valore: AND p.deletedAt IS NULL
    @ConditionTrigger
    private Boolean deletedAtIsNull;

    // Sovrascrive il nome parametro JPQL
    @FieldMapping("createdBy")
    private String authorName;

    // Questo campo NON viene mai aggiunto alla clausola WHERE
    @IgnoreMapping
    private String internalNote;
}
```

#### Riferimento annotazioni di filtro

| Annotazione | Target | Effetto |
|---|---|---|
| `@LikeString` | Campo `String` | Avvolge il valore in `%…%` (configurabile), trasformazione case opzionale |
| `@DateFilter` | Campo `Date`/`Calendar` | Sposta la data di un offset configurabile prima del binding |
| `@ConditionTrigger` | Campo `Boolean` | Quando `true`, attiva condizione senza valore (`IS NULL` / `IS NOT NULL`) tramite `addNullable(fieldName)` |
| `@FieldMapping(name)` | Qualsiasi campo | Usa `name` come nome parametro JPQL invece del nome del campo |
| `@IgnoreMapping` | Qualsiasi campo | Esclude completamente il campo dalla mappa dei parametri |
| `@FilterNullValue` | Qualsiasi campo | Controlla se i valori `null` sono inclusi |
| `@ConditionsZones` | Campo (tramite annotazione personalizzata) | Raggruppa le condizioni in zone SQL con nome (query native) |

#### Ordinamento e paginazione

```java
ProductFilter filter = new ProductFilter();
filter.setName("Widget");
filter.addOrderBy("name", OrderType.ASC);   // la chiave di ordinamento deve essere nella mappa di ordinamento generata
filter.setPageSize(20);
filter.setPageNumber(0);
```

---

### QueryParameter

`QueryParameter<T, ID>` avvolge il filtro e trasporta stato runtime aggiuntivo:

```java
// Da un oggetto filtro tipizzato
QueryParameter<Product, Long> qp = new QueryParameter<>(filter);

// Parametri manuali (senza oggetto filtro)
QueryParameter<Product, Long> qp = new QueryParameter<>();
qp.addParameter("active", true);
qp.addNullable("deletedAt");   // aggiunge sempre la condizione IS-NULL per deletedAt

// Parametri tuple (per confronti multi-colonna)
TupleParameter tp = new TupleParameter(...);
qp.addParameter("price", tp);
```

---

### Query con mappa

`mapFindByFilter`, `mapKeyFindByFilter` e `mapKeyListFindByFilter` restituiscono i risultati
come `PersistenceMap` invece di una lista. Il parametro `key` è un **percorso campo in
dot-notation** risolto a runtime via reflection per estrarre la chiave della mappa da ciascuna entità.

```java
QueryParameter<Product, Long> qp = new QueryParameter<>();
qp.addParameter("active", true);

// Indicizzata per chiave primaria dell'entità
PersistenceMap<Long, Product> byId = productService.mapFindByFilter(qp);
// { 1L → Product(id=1), 2L → Product(id=2), ... }

// Indicizzata per un campo di entità correlata: Product → category → categoryId
PersistenceMap<Long, Product> byCategory =
    productService.mapKeyFindByFilter(qp, Long.class, "category.categoryId");
// { 10L → Product(categoryId=10), 20L → Product(categoryId=20) }
// Se più prodotti condividono la stessa chiave, viene mantenuto l'ultimo.

// Raggruppata per lo stesso campo — tutti i prodotti per categoria
PersistenceMap<Long, List<Product>> grouped =
    productService.mapKeyListFindByFilter(qp, Long.class, "category.categoryId");
// { 10L → [Product(...), Product(...)], 20L → [Product(...)] }
```

---

### Query SQL nativo

Per query complesse che non possono essere espresse in JPQL, usa `NativeQueryParameter`:

```java
NativeQueryParameter<ReportRow, Long> nqp = new NativeQueryParameter<>(ReportRow.class);
nqp.addZoneParameter("filters", "status", "ACTIVE");

List<ReportRow> rows = myService.findByFilter(nqp,
    "SELECT r.id, r.amount FROM report r ${filters}");
```

Gli alias di colonna nel risultato SQL vengono mappati ai campi di `ReportRow` per nome tramite
reflection. Usa `@ResultMapping` o un `JpaRowMapper<K>` personalizzato per mappature non standard.

---

## processor-jpa-service

L'annotation processor genera classi `*QueryJpqlImpl` dalle annotazioni sui servizi.

Aggiungilo al progetto come dipendenza dell'annotation processor:

```xml
<dependency>
    <groupId>com.github.bld-commons</groupId>
    <artifactId>processor-jpa-service</artifactId>
    <version>3.0.15</version>
    <scope>provided</scope>
</dependency>
```

### @QueryBuilder

Posizionalo sul servizio (interfaccia o classe) per attivare la generazione del codice:

```java
@QueryBuilder(
    distinct = true,

    // percorso relazione in notazione dot — genera sempre:
    //   join fetch product.category category
    // Per percorsi multi-hop, es. "product.category.department":
    //   join fetch product.category category
    //   join fetch category.department department
    joins = { "product.category" },

    conditions = {
        @ConditionBuilder(
            field      = "product.name",
            operation  = OperationType.LIKE,
            parameter  = "name",
            upperLower = UpperLowerType.LOWER
        ),
        @ConditionBuilder(
            field     = "product.active",
            operation = OperationType.EQUALS,
            parameter = "active"
        ),
        @ConditionBuilder(
            field     = "product.deletedAt",
            operation = OperationType.EQUALS,
            parameter = "deletedAt",
            nullable  = true           // sempre aggiunto anche quando il valore è null
        )
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name",      field = "product.name"),
        @JpqlOrderBuilder(sortKey = "createdAt", field = "product.createdAt")
    }
)
public interface ProductService extends JpaService<Product, Long> { }
```

### @ConditionBuilder

Ogni `@ConditionBuilder` definisce una condizione WHERE dove il processor ricava
automaticamente il frammento JPQL da un percorso campo e un tipo di operazione:

| Attributo | Tipo | Descrizione |
|---|---|---|
| `field` | `String` | Percorso campo JPQL, es. `"product.name"` o `"product.address.city"` |
| `operation` | `OperationType` | `EQUALS`, `LIKE`, `IN`, `RANGE`, `NOT_EQUALS`, `IS_NULL`, … |
| `parameter` | `String` | Chiave del parametro named, es. `"name"` → `:name` |
| `upperLower` | `UpperLowerType` | `NONE`, `UPPER`, `LOWER` — trasformazione case |
| `nullable` | `boolean` | Se `true`, sempre incluso anche quando il parametro è `null` |

---

### @CustomConditionBuilder

Usato all'interno di `customConditions` (JPQL) o `customNativeConditions` (SQL nativo) quando
i tipi di operazione standard di `@ConditionBuilder` non sono sufficientemente espressivi.
Si scrive direttamente il frammento di condizione grezzo.

| Attributo | Tipo | Descrizione |
|---|---|---|
| `condition` | `String` | Il frammento JPQL o SQL grezzo, incluso il `AND`/`OR` iniziale |
| `parameter` | `String` | Chiave del parametro named che attiva questa condizione quando non-null |
| `type` | `ConditionType` | `SELECT` (default) → `MAP_CONDITIONS`; `DELETE` → `MAP_DELETE_CONDITIONS` |
| `keys` | `String[]` | Nomi delle zone per query native (vedi sotto); default `{"default"}` |

#### customConditions — JPQL grezzo

La condizione viene aggiunta a `MAP_CONDITIONS` (JPQL) e aggiunta alla clausola WHERE
quando il parametro named è presente. Usa l'alias dell'entità e i nomi dei campi JPA
(non i nomi delle colonne), esattamente come li scriveresti in una query JPQL.

```java
@QueryBuilder(
    customConditions = {
        @CustomConditionBuilder(
            condition = "and genere.idGenere in (:genereId)",
            parameter = "genereId"
            // type default a ConditionType.SELECT → va in MAP_CONDITIONS
            // keys non necessario per condizioni JPQL
        )
    }
)
```

Il processor aggiunge questa voce a `MAP_CONDITIONS`:

```java
map.put("genereId", " and genere.idGenere in (:genereId) ");
```

A runtime, la condizione viene aggiunta alla clausola WHERE solo quando
`genereId` è non-null in `QueryParameter`.

#### customNativeConditions — SQL grezzo con zone

La condizione viene aggiunta a una o più sotto-mappe di zona dentro `MAP_NATIVE_CONDITIONS`.
A runtime ogni zona viene sostituita nel template SQL tramite il placeholder `${zoneName}`.

```java
@QueryBuilder(
    customNativeConditions = {
        // Confronto tuple — impossibile esprimere con @ConditionBuilder
        @CustomConditionBuilder(
            condition = "and (g.id_genere, pc.id_postazione_cucina) in (:genereTuple)",
            parameter = "genereTuple",
            keys      = {"zone1", "zone2"}   // registrato in entrambe le zone
        ),
        @CustomConditionBuilder(
            condition = "and g.id_genere in (:idGenere)",
            parameter = "idGenere",
            keys      = {"zone1", "zone2"}
        )
    }
)
```

Mappe generate (dal vero `GenereQueryJpqlImpl`):

```java
private static Map<String, String> getZone1() {
    Map<String, String> map = new HashMap<>();
    map.put("genereTuple", " and (g.id_genere, pc.id_postazione_cucina) in (:genereTuple) ");
    map.put("idGenere",    " and g.id_genere in (:idGenere) ");
    return map;
}

private static Map<String, String> getZone2() { /* identico */ }

private static Map<String, Map<String, String>> getMapNativeConditions() {
    Map<String, Map<String, String>> map = new HashMap<>();
    map.put("zone1", getZone1());
    map.put("zone2", getZone2());
    return map;
}
```

Utilizzo nel template SQL:

```sql
SELECT g.*, pc.id_postazione_cucina
FROM   genere g
JOIN   postazione_cucina pc ON pc.id_postazione_cucina = g.id_postazione_cucina
${zone1}
```

A runtime, se `genereTuple` è non-null, la zona si espande in:

```sql
and (g.id_genere, pc.id_postazione_cucina) in (:genereTuple)
```

---

### Codice generato

Il processor produce una classe come (basato sul vero output generato di `GenereQueryJpqlImpl`):

```java
@Component
public class ProductQueryJpqlImpl extends QueryJpql<Product> {

    // Clausola FROM: il processor percorre "product.category" e genera un
    // JOIN FETCH per ogni salto, usando il nome del campo come alias
    private static final String FROM_BY_FILTER =
        " From Product product " +
        " join fetch product.category category ";

    private static final String SELECT_BY_FILTER =
        "select distinct product" + FROM_BY_FILTER;

    private static final String COUNT_BY_FILTER =
        "select distinct count(product)" + FROM_BY_FILTER;

    private static final String SELECT_ID_BY_FILTER =
        "select distinct product.id " + FROM_BY_FILTER;

    private static final String DELETE_BY_FILTER =
        "delete from Product product ";

    // Condizioni aggiunte solo quando il parametro è non-null (o nullable = true)
    private static Map<String, String> getMapConditions() {
        Map<String, String> map = new HashMap<>();
        map.put("name",   " and upper(product.name) like :name ");
        map.put("active", " and product.active = :active ");
        return map;
    }

    // LEFT JOIN FETCH condizionale — aggiunto alla query solo quando il
    // parametro di filtro correlato (es. "tagIds") è presente
    @Override
    public void mapOneToMany() {
        addJoinOneToMany("tagIds", " left join fetch product.tags tags ");
    }

    @Override public String selectByFilter()              { return SELECT_BY_FILTER; }
    @Override public String countByFilter()               { return COUNT_BY_FILTER; }
    @Override public Map<String, String> mapConditions()  { return MAP_CONDITIONS; }
    // ...
}
```

> **Distinzione chiave:**
> - `joins` → `JOIN FETCH` **sempre presente** (eager, incondizionale) — il percorso traversale genera una clausola per ogni salto
> - `mapOneToMany()` → `LEFT JOIN FETCH` **condizionale** — aggiunto solo quando il parametro di filtro named è non-null, prevenendo join non necessari e problemi N+1

---

## jpa-service-plugin-generator

Il plugin Maven genera file sorgente `Service`, `Repository` e `Mapper` MapStruct
dalle classi entità.

```xml
<plugin>
    <groupId>com.github.bld-commons</groupId>
    <artifactId>jpa-service-plugin-generator</artifactId>
    <version>3.0.15</version>
    <executions>
        <execution>
            <goals><goal>jpa-service-generator</goal></goals>
        </execution>
    </executions>
    <configuration>
        <persistencePackage>com.example.domain</persistencePackage>
        <repositoryPackage>com.example.repository</repositoryPackage>
        <servicePackage>com.example.service</servicePackage>
        <basePackage>com.example</basePackage>
    </configuration>
</plugin>
```

#### Riferimento parametri di configurazione

| Parametro | Obbligatorio | Default | Descrizione |
|---|---|---|---|
| `persistencePackage` | sì | — | Package contenente le classi `@Entity` |
| `servicePackage` | sì | — | Package di output per i servizi generati |
| `repositoryPackage` | no | uguale al service | Package di output per i repository generati |
| `basePackage` | sì | — | Package radice per gli import |
| `outputDirectory` | no | `src_main_java` | Dove vengono scritti i sorgenti generati |

> **Nota:** Considera di puntare `outputDirectory` a `target/generated-sources/java` per
> evitare di committare il codice generato nel controllo di versione. Aggiungi quella directory
> ai source root di compilazione nel tuo `pom.xml`.

---

## proxy-api-controller

Genera controller REST Spring MVC a runtime da interfacce annotate —
nessuna classe di implementazione richiesta.

```xml
<dependency>
    <groupId>com.github.bld-commons</groupId>
    <artifactId>proxy-api-controller</artifactId>
    <version>3.0.15</version>
</dependency>
```

### Configurazione

```java
@SpringBootApplication
@EnableProxyApiController
public class MyApplication { }
```

### Annotazioni

#### @ApiFindController

Contrassegna un'interfaccia come controller REST dinamico. Il framework crea un
proxy dinamico Java che intercetta tutte le chiamate ai metodi.

```java
@ApiFindController
@RequestMapping("/api/products")
@ApiFind(entity = Product.class, id = Long.class)
@ApiMapper(value = ProductMapper.class, method = "toDto")
public interface ProductController {

    @PostMapping("/search")
    List<ProductDto> search(@RequestBody ProductFilter filter);

    @PostMapping("/search/page")
    List<ProductDto> searchPaged(@RequestBody ProductFilter filter,
                                  @RequestParam int page,
                                  @RequestParam int size);
}
```

#### @ApiFind

Lega il controller (o un singolo metodo) a un'entità JPA e al suo tipo di chiave primaria.
Può essere posizionata a livello di classe o di metodo; il livello metodo ha la precedenza.

```java
@ApiFind(entity = Product.class, id = Long.class)
```

#### @ApiMapper

Specifica il mapper usato per convertire i risultati della query prima che la risposta venga restituita.

```java
@ApiMapper(value = ProductMapper.class, method = "toDto")
```

Se `method` è vuoto, il framework risolve il metodo abbinando il tipo dell'entità
al tipo del parametro del metodo del mapper.

#### @ApiBeforeFind

Registra un hook invocato **prima** dell'esecuzione della query.

```java
@ApiBeforeFind(TenantContextInjector.class)
```

```java
@Component
public class TenantContextInjector implements BeforeFind<ProductFilter, Long> {
    @Override
    public void before(QueryParameter<Product, Long> qp, ProductFilter filter) {
        qp.addParameter("tenantId", SecurityContext.getTenantId());
    }
}
```

#### @ApiAfterFind

Registra un hook invocato **dopo** che la query restituisce i risultati.

```java
@ApiAfterFind(ProductEnricher.class)
```

```java
@Component
public class ProductEnricher implements AfterFind<ProductDto> {
    @Override
    public void after(List<ProductDto> results) {
        results.forEach(dto -> dto.setDisplayLabel(dto.getName() + " — " + dto.getSku()));
    }
}
```

### Hook

| Interfaccia | Quando | Firma |
|---|---|---|
| `BeforeFind<F, ID>` | Prima della query | `void before(QueryParameter<T,ID> qp, F filter)` |
| `AfterFind<R>` | Dopo la query, prima della risposta | `void after(List<R> results)` |

Entrambe le classi hook devono essere bean Spring (`@Component` o `@Service`) affinché
il framework possa recuperarle dal contesto applicativo.

---

## Configurazione applicazione

### @EnableJpaService

Abilita tutti i bean di `common-jpa-service`. Posizionala su una classe `@Configuration` o
direttamente sulla classe principale dell'applicazione:

```java
@EnableJpaService
@SpringBootApplication
public class MyApplication { }
```

### @EnableProxyApiController

Abilita la scansione e la registrazione di tutte le interfacce `@ApiFindController`:

```java
@EnableProxyApiController
@SpringBootApplication
public class MyApplication { }
```

---

## Esempio completo

### 1. Entità

```java
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    // ...
}
```

### 2. Interfaccia del servizio (con @QueryBuilder)

```java
@QueryBuilder(
    distinct = true,
    // percorso notazione dot → genera sempre: join fetch product.category category
    joins = { "product.category" },
    conditions = {
        @ConditionBuilder(field = "product.name",   operation = OperationType.LIKE,   parameter = "name"),
        @ConditionBuilder(field = "product.active", operation = OperationType.EQUALS, parameter = "active")
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name", field = "product.name")
    }
)
public interface ProductService extends JpaService<Product, Long> { }
```

### 3. Implementazione del servizio (generata o manuale)

```java
@Service
public class ProductServiceImpl extends JpaServiceImpl<Product, Long>
        implements ProductService {

    @Autowired private ProductRepository repository;
    @PersistenceContext private EntityManager em;

    @Override protected JpaRepository<Product, Long> getJpaRepository() { return repository; }
    @Override protected EntityManager getEntityManager() { return em; }
}
```

### 4. Oggetto filtro

```java
public class ProductFilter extends BaseParameter {

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;

    private Boolean active;

    // getter / setter
}
```

### 5. Controller REST dinamico (proxy)

```java
@ApiFindController
@RequestMapping("/api/products")
@ApiFind(entity = Product.class, id = Long.class)
@ApiMapper(value = ProductMapper.class, method = "toDto")
public interface ProductController {

    @PostMapping("/search")
    List<ProductDto> search(@RequestBody ProductFilter filter);
}
```

### 6. Utilizzo manuale del servizio

```java
@RestController
@RequestMapping("/api/products/manual")
public class ManualProductController {

    @Autowired private ProductService productService;

    @PostMapping("/search")
    public List<Product> search(@RequestBody ProductFilter filter) {
        filter.addOrderBy("name", OrderType.ASC);
        filter.setPageSize(50);
        filter.setPageNumber(0);
        return productService.findByFilter(new QueryParameter<>(filter));
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.findById(id);
    }
}
```

---

## Grafo delle dipendenze

```
common-jpa-service
  └── Spring Data JPA, Hibernate, Commons utilities, MapStruct, Jackson

processor-jpa-service        (annotation processor, solo compilazione)
  └── common-jpa-service, FreeMarker, class-generator

jpa-service-plugin-generator (Maven plugin, solo build)
  └── common-jpa-service, FreeMarker, Maven Plugin API

proxy-api-controller
  └── common-jpa-service, Spring Security, Spring AOP
```

---

## Controller di ricerca astratti

`common-jpa-service` include due controller base astratti che espongono endpoint REST standard già collegati a un `JpaService`.

### BaseSearchController

Fornisce `findByFilter`, `countByFilter` e `singleResultFindByFilter` come metodi protected. Le sottoclassi li dichiarano come endpoint HTTP e forniscono il `ModelMapper`:

```java
public abstract class BaseSearchController<E, ID, M extends BaseModel<ID>,
        P extends BaseParameter, MM extends ModelMapper<E, M>> {

    @Autowired
    protected JpaService<E, ID> jpaService;

    protected CollectionResponse<M> findByFilter(P baseParameter) throws Exception { ... }
    protected ObjectResponse<Long>  countByFilter(P baseParameter) throws Exception { ... }
    protected ObjectResponse<M>     singleResultFindByFilter(P baseParameter) throws Exception { ... }
    protected abstract MM modelMapper();
}
```

### PerformanceSearchController

Estende `BaseSearchController` e aggiunge un endpoint `POST /performance/search` che restituisce un modello `PM` più leggero insieme all'endpoint standard `POST /search`:

```java
public abstract class PerformanceSearchController<E, ID,
        M extends BaseModel<ID>, PM extends BaseModel<ID>,
        P extends BaseParameter>
        extends BaseSearchController<E, ID, M, P, PerformanceModelMapper<E, M, PM>> {

    // POST /search             → CollectionResponse<M>
    // POST /performance/search → CollectionResponse<PM>
    // POST /count              → ObjectResponse<Long>
    // POST /search/single-result → ObjectResponse<M>
}
```

---

## Mappatura personalizzata delle righe

### JpaRowMapper / JdbcRowMapper

Interfacce funzionali per la mappatura manuale delle righe nelle query native:

```java
// Basata su JPA Tuple
JpaRowMapper<ProductDto> jpaMapper = (list, row, i) -> {
    list.add(new ProductDto(row.get("id", Long.class), row.get("name", String.class)));
};

// Basata su JDBC ResultSet
JdbcRowMapper<ReportRow> jdbcMapper = (list, rs, i) -> {
    list.add(new ReportRow(rs.getLong("id"), rs.getBigDecimal("amount")));
};
```

### @ResultMapping / @IgnoreResultSet

Controllano come le colonne del risultato di una query nativa vengono mappate ai campi del modello:

| Annotazione | Effetto |
|------------|--------|
| `@ResultMapping(MyMapper.class)` | Delega la conversione del campo a un `ResultMapper<T>` personalizzato |
| `@IgnoreResultSet` | Salta completamente il campo durante la mappatura automatica del result-set |

```java
public class OrderSummary {

    private Long id;

    @ResultMapping(StatusMapper.class)   // conversione personalizzata
    private StatusEnum status;

    @IgnoreResultSet                     // popolato dopo la query
    private String displayLabel;
}
```

---

## TupleParameter e @TupleComparison

Per confronti `IN` multi-colonna (semantica row-value):

```java
// Programmatico
TupleParameter tp = new TupleParameter(new String[]{"productId", "warehouseId"});
tp.setObjects(new Object[]{1L, 10L}, new Object[]{2L, 10L});
qp.addParameter("productWarehouse", tp);

// Dichiarativo (in una classe filtro)
@TupleComparison({"productId", "warehouseId"})
private TupleParameter productWarehouse;
```

---

## proxy-api-controller — Componenti interni

| Componente | Descrizione |
|-----------|-------------|
| `ApiFindRegistrar` | Spring `ImportBeanDefinitionRegistrar`; individua le interfacce `@ApiFindController` e le registra come bean proxy |
| `ProxyConfig` | Bean Spring factory; avvolge `Proxy.newProxyInstance()` con `ApiFindInterceptor` |
| `ApiFindInterceptor` | `InvocationHandler` singleton; instrada ogni chiamata a un nuovo prototipo `FindInterceptor` |
| `FindInterceptor` | Componente prototipo; pipeline completa: estrazione parametri → `BeforeFind` → query → mappatura → `AfterFind` |
| `ParameterDetails` | Value object interno: `Parameter` + valore runtime + indice posizionale |
| `ApiFindException` | Eccezione non controllata per i fallimenti della pipeline del proxy |

---

*Documentazione generata per dev-persistence v3.0.16 — Francesco Baldi*
