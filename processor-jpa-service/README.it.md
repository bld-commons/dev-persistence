# processor-jpa-service

Annotation processor a tempo di compilazione che legge le interfacce di servizio annotate con `@QueryBuilder` e genera le corrispondenti classi `*QueryJpqlImpl` contenenti stringhe JPQL pre-costruite, mappe di condizioni e mappe di ordinamento.

---

## Indice dei contenuti

1. [Panoramica](#panoramica)
2. [Configurazione](#configurazione)
3. [@QueryBuilder](#querybuilder)
4. [@ConditionBuilder](#conditionbuilder)
   - [Riferimento OperationType](#riferimento-operationtype)
5. [@CustomConditionBuilder](#customconditionbuilder)
6. [@JpqlOrderBuilder e @NativeOrderBuilder](#jpqlorderbuilder-e-nativeorderbuilder)
7. [Codice generato](#codice-generato)
   - [Esempio di input](#esempio-di-input)
   - [Esempio di output](#esempio-di-output)
8. [Come funziona il processor](#come-funziona-il-processor)

---

## Panoramica

Il processor viene eseguito durante la fase `compile` di Maven (o Gradle). Individua ogni tipo annotato con `@QueryBuilder`, estrae la classe dell'entità JPA dal parametro generico del servizio, legge gli attributi dell'annotazione e genera una classe `*QueryJpqlImpl`.

La classe generata estende `QueryJpql<T>` e viene acquisita da Spring come bean. `JpaServiceImpl` la inietta automaticamente per costruire ed eseguire query dinamiche a runtime.

**Non è mai necessario scrivere o mantenere manualmente la classe generata.** Eliminala e verrà rigenerata al prossimo build.

---

## Configurazione

Aggiungi il processor come annotation processor path nel plugin Maven compiler. Poiché il processor include tutte le sue dipendenze in un unico fat JAR (tramite Maven Shade), non sono necessarie ulteriori voci di dipendenza.

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

Posiziona `@QueryBuilder` sull'interfaccia (o classe) di servizio che gestisce un'entità JPA. Il processor lo utilizza per generare il `QueryJpqlImpl` per quell'entità.

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

### Attributi

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `distinct` | `boolean` | `true` | Aggiunge `DISTINCT` alla SELECT generata |
| `joins` | `String[]` | `{}` | Percorsi di relazione in notazione dot per `JOIN FETCH` incondizionali |
| `conditions` | `@ConditionBuilder[]` | `{}` | Condizioni JPQL standard (campo + operazione + parametro) |
| `customConditions` | `@CustomConditionBuilder[]` | `{}` | Frammenti JPQL scritti manualmente |
| `customNativeConditions` | `@CustomConditionBuilder[]` | `{}` | Frammenti SQL nativo scritti manualmente, raggruppati in zone con nome |
| `jpaOrder` | `@JpqlOrderBuilder[]` | `{}` | Mappature chiave di ordinamento → campo JPQL |
| `nativeOrder` | `@NativeOrderBuilder[]` | `{}` | Mappature chiave di ordinamento → colonna SQL nativo |

### joins — percorsi di relazione in notazione dot

Ogni voce in `joins` è una catena di nomi di campo di relazione JPA che inizia dall'alias dell'entità radice. Il processor percorre il percorso e genera una clausola `JOIN FETCH` per ogni salto.

```java
joins = { "genere.postazioneCucina.ristorante" }
```

Genera:

```jpql
join fetch genere.postazioneCucina postazioneCucina
join fetch postazioneCucina.ristorante ristorante
```

Questi join sono **sempre** presenti indipendentemente dai parametri di filtro attivi. Per i join condizionali (aggiunti solo quando uno specifico parametro di filtro è non-null), il processor utilizza i metadati delle relazioni `@OneToMany` / `@ManyToMany` per generare una voce `mapOneToMany()` tramite `LEFT JOIN FETCH`.

---

## @ConditionBuilder

Definisce una singola condizione WHERE in JPQL. Utilizzato all'interno di `@QueryBuilder#conditions()`.

```java
@ConditionBuilder(
    field      = "product.name",
    operation  = OperationType.LIKE,
    parameter  = "name",
    upperLower = UpperLowerType.LOWER,
    nullable   = false
)
```

### Attributi

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `field` | `String` | — | Percorso campo JPQL (es. `"e.name"`, `"e.address.city"`) |
| `operation` | `OperationType` | — | Tipo di confronto |
| `parameter` | `String` | — | Nome del parametro named (mappato al campo del filtro o alla voce di `QueryParameter`) |
| `upperLower` | `UpperLowerType` | `NONE` | Trasformazione maiuscolo/minuscolo applicata sia al campo che al parametro |
| `nullable` | `boolean` | `false` | Se `true`, viene sempre aggiunto anche quando il parametro è null (condizione IS NULL) |

### Riferimento OperationType

| Valore | JPQL generato |
|--------|--------------|
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
| `RANGE` | Confronto per intervallo con due parametri |

---

## @CustomConditionBuilder

Permette di scrivere direttamente frammenti di condizione JPQL o SQL nativo, per i casi in cui le operazioni standard di `@ConditionBuilder` non sono sufficientemente espressive (subquery, confronti tuple, sintassi vendor-specifiche).

Utilizzato in due attributi distinti di `@QueryBuilder`:

- `customConditions` — condizioni JPQL, inserite in `MAP_CONDITIONS`
- `customNativeConditions` — condizioni SQL nativo, raggruppate in zone con nome in `MAP_NATIVE_CONDITIONS`

### Attributi

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `parameter` | `String` | — | Parametro named che attiva questa condizione quando non-null |
| `condition` | `String` | — | Frammento JPQL o SQL grezzo, inclusa la parola chiave iniziale `AND`/`OR` |
| `type` | `ConditionType` | `SELECT` | `SELECT` → `MAP_CONDITIONS`, `DELETE` → `MAP_DELETE_CONDITIONS` |
| `keys` | `String[]` | `{"default"}` | Nomi delle zone (per condizioni native); corrisponde ai placeholder `${zoneName}` nel template SQL |

### Condizione JPQL personalizzata

Usa l'alias dell'entità e i nomi dei campi JPA (non i nomi delle colonne):

```java
customConditions = {
    @CustomConditionBuilder(
        condition = "and genere.idGenere in (:genereId)",
        parameter = "genereId"
        // type defaults to SELECT → MAP_CONDITIONS
    )
}
```

### Condizione SQL nativo personalizzata con zone

Usa l'alias di tabella e i nomi delle colonne. Specifica le chiavi di zona che corrispondono ai placeholder `${zoneName}` nel template SQL:

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
// Template SQL: SELECT g.* FROM genere g ${zone1}
```

---

## @JpqlOrderBuilder e @NativeOrderBuilder

Mappano le chiavi di ordinamento logiche (usate negli oggetti `OrderBy`) a espressioni di campo JPQL o riferimenti a colonne SQL nativo.

```java
jpaOrder = {
    @JpqlOrderBuilder(sortKey = "name",  field = "product.name"),
    @JpqlOrderBuilder(sortKey = "price", field = "product.unitPrice")
}

nativeOrder = {
    @NativeOrderBuilder(sortKey = "name", field = "p.product_name")
}
```

A runtime, `OrderBy("name", OrderType.ASC)` nel filtro viene risolto in `ORDER BY product.name ASC`.

---

## Codice generato

Per ogni classe annotata con `@QueryBuilder` il processor genera **due file** in `target/generated-sources/annotations`:

| File generato | Scopo |
|---|---|
| `*QueryJpql.java` | Interfaccia con costanti `public static final String` per ogni nome di parametro e chiave di ordinamento |
| `*QueryJpqlImpl.java` | `@Component` che estende `QueryJpql<T>`, implementa l'interfaccia e contiene tutte le stringhe e le mappe pre-costruite |

---

### Esempio di input

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

### Esempio di output

#### `ExApplicationQueryJpql.java` — interfaccia delle costanti

Una costante `String` per ogni nome di parametro e per ogni chiave di ordinamento. Referenziata nelle implementazioni degli hook e nelle chiamate manuali a `QueryParameter` per evitare stringhe hardcoded.

```java
public interface ExApplicationQueryJpql {

    // Costanti dei nomi parametro — una per @ConditionBuilder.parameter + campi dell'entità base
    String applicationsName    = "applicationsName";
    String idProjectGrant      = "idProjectGrant";
    String idProject           = "idProject";
    String idEnvironment       = "idEnvironment";
    String notInName           = "notInName";
    String email               = "email";       // da customNativeConditions
    String name                = "name";        // dai campi dell'entità base
    String id                  = "id";
    String idApplication       = "idApplication";
    String updateTime          = "updateTime";
    String updateTimeFrom      = "updateTimeFrom";
    String updateTimeTo        = "updateTimeTo";
    String createTime          = "createTime";
    String createTimeFrom      = "createTimeFrom";
    String createTimeTo        = "createTimeTo";
    // ...

    // Costanti delle chiavi di ordinamento (con prefisso ord_)
    String ord_desApplicationType = "desApplicationType";  // da @JpqlOrderBuilder
    String ord_name               = "name";                // da @NativeOrderBuilder
}
```

#### `ExApplicationQueryJpqlImpl.java` — classe generata completa

```java
@Component
public class ExApplicationQueryJpqlImpl
        extends QueryJpql<ExApplication>
        implements ExApplicationQueryJpql {

    // ── Mappe statiche (inizializzate una volta al caricamento della classe) ────
    private final static Map<String,String>              MAP_CONDITIONS        = getMapConditions();
    private final static Map<String,String>              MAP_DELETE_CONDITIONS = getMapDeleteConditions();
    private final static Map<String,Map<String,String>>  MAP_NATIVE_CONDITIONS = getMapNativeConditions();
    private final static Map<String,String>              MAP_NATIVE_ORDERS     = getMapNativeOrders();
    private final static Map<String,String>              MAP_JPA_ORDERS        = getMapJpaOrders();

    // ── Stringhe JPQL di base ────────────────────────────────────────────────
    // FROM include tutte le clausole JOIN FETCH obbligatorie derivate dai campi @ManyToOne dell'entità.
    // Il processor percorre il grafo dell'entità e genera un JOIN FETCH per ogni relazione
    // che non è @OneToMany / @ManyToMany (quelle vanno in mapOneToMany()).

    private final static String FROM_BY_FILTER =
        " From ExApplication exApplication " +
        " join fetch exApplication.exProject exProject " +
        " join fetch exApplication.exEnvironment exEnvironment " +
        " join fetch exApplication.exApplicationType exApplicationType ";

    private final static String SELECT_BY_FILTER    = "select distinct exApplication" + FROM_BY_FILTER;
    private final static String COUNT_BY_FILTER     = "select distinct count(exApplication)" + FROM_BY_FILTER;
    private final static String SELECT_ID_BY_FILTER = "select distinct exApplication.idApplication " + FROM_BY_FILTER;
    private final static String DELETE_BY_FILTER    = "delete from ExApplication exApplication ";
    // Nota: DELETE non ha JOIN FETCH — le relazioni vengono navigate inline in MAP_DELETE_CONDITIONS.

    // ── MAP_CONDITIONS — attiva durante le query SELECT / COUNT ──────────────
    // Chiave   = nome parametro
    // Valore   = frammento JPQL aggiunto alla WHERE quando il parametro è non-null
    //
    // Due sotto-mappe vengono unite:
    //   getMapBaseConditions() — auto-generata dai campi propri dell'entità
    //   voci esplicite        — generate da ogni @ConditionBuilder
    //
    // Nelle query SELECT, gli alias delle relazioni da FROM_BY_FILTER sono riutilizzati direttamente
    // (es. "exProject" invece di "exApplication.exProject").

    private static Map<String,String> getMapConditions() {
        Map<String,String> map = getMapBaseConditions();
        map.put(applicationsName, " and ((exApplication.name)  in (:applicationsName) )");
        map.put(notInName,        " and ((exApplication.name)  not in (:notInName) )");
        map.put(idProject,        " and (exProject.idProject  in (:idProject) )");
        map.put(idEnvironment,    " and (exEnvironment.idEnvironment  in (:idEnvironment) )");
        map.put(idProjectGrant,   " and (exProject.idProject  in (:idProjectGrant) )");
        return map;
    }

    // Condizioni base auto-generate — campi a livello entità sempre disponibili
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

    // ── MAP_DELETE_CONDITIONS — attiva durante le query DELETE ───────────────
    // Struttura identica a MAP_CONDITIONS ma senza alias join-fetched.
    // Il JPQL DELETE non può usare JOIN FETCH, quindi ogni relazione viene navigata
    // dall'alias dell'entità radice usando il percorso completo in notazione dot.

    private static Map<String,String> getMapDeleteConditions() {
        Map<String,String> map = getMapBaseConditions();
        // Confronto con MAP_CONDITIONS:
        //   SELECT: "exProject.idProject in (:idProject)"       ← usa l'alias fetched
        //   DELETE: "exApplication.exProject.idProject in (:idProject)"  ← percorso completo
        map.put(idProject,      " and (exApplication.exProject.idProject  in (:idProject) )");
        map.put(idEnvironment,  " and (exApplication.exEnvironment.idEnvironment  in (:idEnvironment) )");
        map.put(idProjectGrant, " and (exApplication.exProject.idProject  in (:idProjectGrant) )");
        map.put(applicationsName," and ((exApplication.name)  in (:applicationsName) ) ");
        map.put(notInName,       " and ((exApplication.name)  not in (:notInName) ) ");
        return map;
    }

    // ── MAP_NATIVE_CONDITIONS — condizioni SQL nativo per zona ───────────────
    // Chiave esterna = nome zona (deve corrispondere a ${zoneName} nella stringa template SQL)
    // Mappa interna  = nome parametro → frammento SQL iniettato in quella zona quando non-null
    // Generata da @QueryBuilder#customNativeConditions con le chiavi corrispondenti.

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

    // ── MAP_NATIVE_ORDERS — chiavi di ordinamento SQL nativo ─────────────────
    // Chiave   = chiave di ordinamento logica (da @NativeOrderBuilder.key)
    // Valore   = espressione colonna SQL usata in ORDER BY

    private static Map<String,String> getMapNativeOrders() {
        Map<String,String> map = new HashMap<>();
        map.put(ord_name, " ea.name ");
        return map;
    }

    // ── MAP_JPA_ORDERS — chiavi di ordinamento JPQL ──────────────────────────
    // Auto-generate per ogni campo raggiungibile tramite alias join-fetched + voci @JpqlOrderBuilder esplicite.
    // Chiave   = stringa chiave di ordinamento (da OrderBy.sortKey nel filtro)
    // Valore   = espressione campo JPQL per ORDER BY

    private static Map<String,String> getMapJpaOrders() {
        Map<String,String> map = new HashMap<>();
        // Auto-generate dai percorsi join-fetched:
        map.put("exApplication.name",          "exApplication.name");
        map.put("exProject.idProject",         "exProject.idProject");
        map.put("exEnvironment.idEnvironment", "exEnvironment.idEnvironment");
        map.put("exEnvironment.envName",       "exEnvironment.envName");
        map.put("exProject.prjName",           "exProject.prjName");
        // Voce esplicita da @JpqlOrderBuilder:
        map.put(ord_desApplicationType,        " exApplicationType.desApplicationType ");
        return map;
    }

    // ── mapOneToMany() — LEFT JOIN FETCH condizionale ─────────────────────────
    // Invocato pigriamente la prima volta che getMapOneToMany() viene acceduta.
    // Ogni voce registra un LEFT JOIN FETCH che viene aggiunto alla query SELECT/COUNT
    // SOLO quando il parametro corrispondente è non-null nel filtro.
    // Questo previene prodotti cartesiani per collezioni su cui non si sta filtrando.

    @Override
    public void mapOneToMany() {
        addJoinOneToMany(idApplicationServer,
            " left join fetch exApplication.exApplicationServers exApplicationServers ");
        addJoinOneToMany(idServiceRest,
            " left join fetch exApplication.exServiceRests exServiceRests ");
        // Se il filtro ha idApplicationServer → il JOIN FETCH viene aggiunto automaticamente.
        // Se è null → nessun join, nessun prodotto cartesiano.
    }

    // ── Metodi bridge ─────────────────────────────────────────────────────────

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

### Differenze chiave tra MAP_CONDITIONS e MAP_DELETE_CONDITIONS

Lo stesso `@ConditionBuilder` genera **due stringhe di condizione diverse** — una per SELECT e una per DELETE:

| Tipo query | Percorso usato | Esempio |
|---|---|---|
| SELECT / COUNT | Alias breve da JOIN FETCH | `exProject.idProject in (:idProject)` |
| DELETE | Percorso completo dall'alias radice | `exApplication.exProject.idProject in (:idProject)` |

Questo perché il JPQL DELETE non può contenere clausole JOIN FETCH, quindi il processor riscrive automaticamente ogni condizione per usare il percorso completo in notazione dot a partire dall'alias dell'entità radice.

---

## Come funziona il processor

1. Il processor viene attivato dalla presenza di `@QueryBuilder` su qualsiasi tipo.
2. Estrae il primo argomento di tipo generico del servizio (la classe entità).
3. Percorre i campi della classe entità usando i mirror delle annotazioni Java per trovare:
   - `@Id` / `@EmbeddedId` → tipo della chiave primaria
   - `@OneToMany` / `@ManyToMany` → candidati per `LEFT JOIN FETCH` condizionale
   - `@JoinColumn` → metadati della relazione
4. Elabora ogni voce `@ConditionBuilder` e `@CustomConditionBuilder` per popolare le mappe di condizioni.
5. Elabora i percorsi `joins[]` per generare la catena statica di JOIN FETCH.
6. Usando i template FreeMarker, scrive il file sorgente completo `*QueryJpqlImpl.java` nella directory generated-sources.
7. Il compiler Maven lo compila insieme al resto del progetto.

La directory di output del processor è `target/generated-sources/annotations`. La classe generata viene compilata ed è disponibile a runtime come qualsiasi altra classe dell'applicazione.
