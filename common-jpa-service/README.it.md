# common-jpa-service

Modulo runtime principale del framework dev-persistence. Fornisce le astrazioni di servizio, il motore di costruzione dinamica delle query e il binding dei filtri via reflection su cui si basano tutti gli altri moduli.

> Documentazione disponibile anche in: [English](README.md)

---

## Indice

1. [Panoramica](#panoramica)
2. [Configurazione](#configurazione)
3. [Layer di servizio](#layer-di-servizio)
   - [Interfaccia JpaService](#interfaccia-jpaservice)
   - [Estendere JpaServiceImpl](#estendere-jpaserviceimpl)
4. [Oggetti filtro (BaseParameter)](#oggetti-filtro-baseparameter)
   - [Annotazioni di filtro](#annotazioni-di-filtro)
5. [QueryParameter](#queryparameter)
6. [Query native SQL](#query-native-sql)
   - [NativeQueryParameter](#nativequeryparameter)
   - [Condizioni a zone](#condizioni-a-zone)
7. [QueryJpql](#queryjpql)
8. [Mapping personalizzato delle righe](#mapping-personalizzato-delle-righe)
9. [JdbcTemplateService](#jdbctemplateservice)

---

## Panoramica

`common-jpa-service` sostituisce il classico pattern di scrittura manuale dei metodi di query per ogni entità. Al suo posto, si definisce un oggetto filtro tipizzato i cui campi non-null vengono automaticamente tradotti in condizioni JPQL WHERE a runtime. Le stringhe JPQL stesse sono pre-costruite a compile time dall'annotation processor `processor-jpa-service` e memorizzate in una classe `QueryJpqlImpl` generata.

A runtime la catena di chiamata è:

```
YourService.findByFilter(queryParameter)
  → JpaServiceImpl.findByFilter()
  → BaseJpaService (costruisce JPQL / native SQL dinamicamente)
  → QueryJpqlImpl (fornisce stringhe e mappe pre-costruite)
  → EntityManager / JpaRepository
```

---

## Configurazione

Abilita il framework nell'applicazione Spring Boot:

```java
@SpringBootApplication
@EnableJpaService
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

`@EnableJpaService` importa `EnableJpaServiceConfiguration`, che registra il bean `ReflectionCommons` usato internamente per l'introspezione dei parametri.

---

## Layer di servizio

### Interfaccia JpaService

`JpaService<T, ID>` è l'interfaccia principale che le tue interfacce di servizio estendono. Fornisce:

**CRUD**

| Metodo | Descrizione |
|--------|-------------|
| `save(T entity)` | Inserimento o aggiornamento |
| `update(T entity)` | Aggiornamento di un'entità esistente |
| `delete(T entity)` | Eliminazione di una singola entità |
| `deleteAll(Collection<T>)` | Eliminazione massiva |
| `findById(ID id)` | Ricerca per chiave primaria |
| `findAll()` | Restituisce tutte le righe |
| `count()` | Conteggio totale delle righe |
| `existsById(ID id)` | Verifica dell'esistenza |

**Query basate su filtro**

| Metodo | Descrizione |
|--------|-------------|
| `findByFilter(QueryParameter<T,ID>)` | Lista di entità che corrispondono al filtro |
| `countByFilter(QueryParameter<T,ID>)` | Conteggio delle entità corrispondenti |
| `deleteByFilter(QueryParameter<T,ID>)` | Eliminazione delle entità corrispondenti |
| `singleResultByFilter(QueryParameter<T,ID>)` | Singola entità (eccezione se ne vengono trovate più di una) |
| `mapFindByFilter(QueryParameter<T,ID>)` | Risultati come `PersistenceMap<K,V>` |
| `mapKeyFindByFilter(QueryParameter<T,ID>)` | Risultati indicizzati per un campo chiave |
| `mapKeyListFindByFilter(QueryParameter<T,ID>)` | Risultati raggruppati per un campo chiave |

**Query native SQL**

| Metodo | Descrizione |
|--------|-------------|
| `findNativeByFilter(NativeQueryParameter<K,ID>)` | Lista mappata da risultati Tuple nativi |
| `findNativeByFilter(NativeQueryParameter<K,ID>, JpaRowMapper<K>)` | Lista tramite mapper personalizzato |

### Estendere JpaServiceImpl

Le classi di servizio concrete estendono `JpaServiceImpl<T, ID>` e implementano due metodi astratti:

```java
@Service
@Transactional
@QueryBuilder(
    distinct = true,
    conditions = {
        @ConditionBuilder(field = "product.name", operation = OperationType.LIKE, parameter = "name"),
        @ConditionBuilder(field = "product.active", operation = OperationType.EQUALS, parameter = "active")
    },
    jpaOrder = {
        @JpqlOrderBuilder(sortKey = "name", field = "product.name")
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

L'annotation processor legge `@QueryBuilder` e genera `ProductQueryJpqlImpl` a compile time. Spring lo inietta in `JpaServiceImpl` come bean `QueryJpql<Product>`.

---

## Oggetti filtro (BaseParameter)

Crea un DTO filtro estendendo `BaseParameter`. Qualsiasi campo non-null viene aggiunto automaticamente alla clausola WHERE a runtime.

```java
public class ProductFilter extends BaseParameter {

    private String name;

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String description;

    @DateFilter(addDay = 1)
    private Date expiresAfter;

    @ListFilter
    private List<Long> categoryIds;

    // getter / setter
}
```

Utilizzo:

```java
ProductFilter filter = new ProductFilter();
filter.setName("Widget");
filter.addOrderBy("name", OrderType.ASC);
filter.setPageSize(20);
filter.setPageNumber(0);

QueryParameter<Product, Long> qp = new QueryParameter<>(filter);
List<Product> results = productService.findByFilter(qp);
```

Campi predefiniti (annotati con `@IgnoreMapping`, non generano condizioni WHERE):

| Campo | Descrizione |
|-------|-------------|
| `pageSize` | Numero di risultati per pagina |
| `pageNumber` | Numero di pagina (base zero) |
| `orderBy` | Lista di `OrderBy(sortKey, OrderType)` |

### Annotazioni di filtro

#### @DateFilter

Sposta un valore `Date` (o `Calendar`) di un offset configurabile prima di passarlo come parametro.

```java
@DateFilter(addDay = 7)
private Date expiresBy = new Date();
// viene passato come: now + 7 giorni
```

Parametri: `addYear`, `addMonth`, `addWeek`, `addDay`, `addHour`, `addMinute`, `addSecond` (tutti default 0; valori negativi sottraggono).

#### @LikeString

Aggiunge i wildcard SQL `LIKE` attorno al valore di una stringa.

```java
@LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
private String lastName;
// genera: AND LOWER(e.lastName) LIKE LOWER(:lastName)  →  '%smith%'
```

Valori `LikeType`: `LEFT` (`%valore`), `RIGHT` (`valore%`), `LEFT_RIGHT` (`%valore%`).
Valori `UpperLowerType`: `NONE`, `UPPER`, `LOWER`.

#### @ListFilter

Mappa una `Collection` in una clausola SQL `IN (…)`.

```java
@ListFilter
private List<String> statuses;
// genera: AND o.status IN (:statuses)
```

#### @FieldMapping

Sovrascrive il nome del parametro JPQL (che di default è il nome del campo Java).

```java
@FieldMapping("firstName")
private String userFirstName;
// JPQL usa :firstName invece di :userFirstName
```

#### @IgnoreMapping

Impedisce che un campo venga aggiunto alla mappa dei parametri. Utile per campi che non devono generare condizioni WHERE (campi di visualizzazione, flag, ecc.).

#### @FilterNullValue

Forza l'inclusione di un campo nella clausola WHERE anche quando il suo valore è `null` (genera una condizione IS NULL).

---

## QueryParameter

`QueryParameter<T, ID>` è l'oggetto principale di input per le query basate su filtro. Può essere popolato in due modi:

**Tramite un oggetto filtro tipizzato:**

```java
QueryParameter<Product, Long> qp = new QueryParameter<>(filter);
```

Il framework analizza la sottoclasse di `BaseParameter` via reflection ed estrae tutti i campi non-null nella mappa dei parametri interna.

**Manualmente:**

```java
QueryParameter<Product, Long> qp = new QueryParameter<>();
qp.addParameter("categoryId", 5L);
qp.addNullable("deletedAt");  // aggiunge sempre: AND e.deletedAt IS NULL
```

`addNullable(name)` aggiunge sempre la condizione indipendentemente dal valore — utile per verifiche IS NULL.

---

## Query native SQL

Per query non esprimibili in JPQL (sintassi vendor-specifica, join complessi, confronti di tuple), usa SQL nativo con `NativeQueryParameter`.

Definisci la query native in un file `.sql` (o come stringa inline) usando i placeholder `${zoneName}` per i blocchi WHERE dinamici:

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

| Metodo | Descrizione |
|--------|-------------|
| `addCondition(zona, param, condizione, valore)` | Aggiunge una condizione a una zona nominata |
| `addParameter(nome, valore)` | Aggiunge un parametro named direttamente |
| `setPageSize(int)` | Paginazione |
| `setPageNumber(int)` | Paginazione |
| `addOrderBy(sortKey, OrderType)` | Ordinamento |

### Condizioni a zone

Le zone consentono allo stesso template SQL di avere più blocchi WHERE attivabili indipendentemente. Ogni zona corrisponde a un placeholder `${nomeZona}` nel template SQL.

Usa `@ConditionsZone` per creare annotazioni di zona personalizzate da usare con `@QueryBuilder#customNativeConditions()`:

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

`initWhere = true` (default) indica al framework di anteporre automaticamente `WHERE` o `AND` prima della prima condizione della zona.

---

## QueryJpql

`QueryJpql<T>` è il contenitore astratto di tutte le stringhe JPQL/SQL pre-costruite per una specifica entità. Le sottoclassi non si scrivono mai manualmente — vengono generate da `processor-jpa-service`.

La classe `*QueryJpqlImpl` generata fornisce:

- Stringhe JPQL statiche: SELECT, COUNT, DELETE, SELECT-ID
- Mappa delle condizioni: `Map<String, String>` — nome parametro → frammento di condizione JPQL (es. `"name" → "AND e.name = :name"`)
- Mappa degli ordini: `Map<String, String>` — chiave di ordinamento → espressione campo JPQL
- Mappa delle condizioni native: `Map<String, Map<String, String>>` — nome zona → mappa condizioni
- `mapOneToMany()` — LEFT JOIN FETCH condizionale, aggiunto solo quando il parametro filtro corrispondente è non-null

`QueryJpql` viene iniettato in `JpaServiceImpl` come bean Spring generico e non è usato direttamente dal codice applicativo.

---

## Mapping personalizzato delle righe

Per query native che richiedono un mapping Tuple→oggetto personalizzato, implementa `JpaRowMapper<K>`:

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

Per l'accesso JDBC puro (bypassando JPA), estendi `JdbcTemplateServiceImpl`:

```java
@Service
public class ReportServiceImpl extends JdbcTemplateServiceImpl<ReportDto, Long>
        implements ReportService {
    // eredita: findByFilter, countByFilter tramite NamedParameterJdbcTemplate
}
```

`JdbcTemplateService<T, ID>` replica le API di `JpaService` ma esegue le query tramite `NamedParameterJdbcTemplate`.
