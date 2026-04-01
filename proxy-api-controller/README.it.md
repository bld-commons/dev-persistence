# proxy-api-controller

Framework REST controller a runtime che genera endpoint Spring MVC completamente funzionali da interfacce annotate, senza alcuna classe di implementazione del controller.

---

## Indice dei contenuti

1. [Panoramica](#panoramica)
2. [Configurazione](#configurazione)
3. [Annotazioni](#annotazioni)
   - [@ApiFindController](#apifindcontroller)
   - [@ApiFind](#apifind)
   - [@ApiMapper](#apimapper)
   - [@ApiBeforeFind](#apibeforefind)
   - [@ApiAfterFind](#apiafterfind)
   - [@ApiQuery](#apiquery)
   - [@DefaultOrderBy](#defaultorderby)
4. [Interfacce hook](#interfacce-hook)
   - [BeforeFind](#beforefind)
   - [AfterFind](#afterfind)
5. [Binding dei parametri dei metodi](#binding-dei-parametri-dei-metodi)
6. [Contenitori di risposta](#contenitori-di-risposta)
7. [Flusso di esecuzione](#flusso-di-esecuzione)
8. [Componenti interni](#componenti-interni)
9. [Gestione degli errori](#gestione-degli-errori)
10. [Esempi reali](#esempi-reali)
    - [Controller base con sicurezza e caching](#controller-base-con-sicurezza-e-caching)
    - [Override di annotazioni a livello di metodo](#override-di-annotazioni-a-livello-di-metodo)
    - [Query con nome tramite @ApiQuery](#query-con-nome-tramite-apiquery)
    - [Hook BeforeFind con @AuthenticationPrincipal](#hook-beforefind-con-authenticationprincipal)
    - [Hook AfterFind per arricchire i risultati](#hook-afterfind-per-arricchire-i-risultati)
    - [Classe filtro con IDFilterParameter](#classe-filtro-con-idfilterparameter)
11. [Esempio end-to-end completo](#esempio-end-to-end-completo)

---

## Panoramica

`proxy-api-controller` implementa il pattern [dynamic proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html) sopra `common-jpa-service`. Dichiari un'interfaccia Java, annotala con `@ApiFindController` e `@ApiFind`, e il framework:

1. Registra l'interfaccia come bean Spring `@RestController` supportato da un `java.lang.reflect.Proxy`.
2. Intercetta ogni richiesta HTTP, estrae i parametri dalla richiesta, costruisce un `QueryParameter` e delega al `JpaService` corretto.
3. Opzionalmente mappa i risultati tramite un mapper MapStruct (o qualsiasi bean) prima di restituirli.

Non è necessario scrivere o mantenere codice del controller.

---

## Configurazione

Aggiungi `@EnableProxyApiController` alla classe della tua applicazione Spring Boot e specifica i package da scansionare:

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

Attributi di `@EnableProxyApiController`:

| Attributo | Descrizione |
|-----------|-------------|
| `value` | Package base (abbreviazione) |
| `basePackages` | Package da scansionare per le interfacce `@ApiFindController` |
| `basePackageClasses` | Alternativa type-safe a `basePackages` |
| `clients` | Classi interfaccia specifiche da registrare |
| `defaultConfiguration` | Classi di configurazione applicate globalmente |

---

## Annotazioni

### @ApiFindController

Contrassegna un'interfaccia come controller REST dinamico. Combina con le annotazioni Spring MVC (`@RequestMapping`, `@PostMapping`, ecc.) sull'interfaccia e i suoi metodi.

```java
@ApiFindController
@RequestMapping("/api/products")
public interface ProductController {
    // metodi qui
}
```

L'interfaccia viene registrata come bean `@RestController` tramite `ApiFindRegistrar`. Il proxy intercetta tutte le chiamate ai metodi e le instrada attraverso `FindInterceptor`.

Qualsiasi annotazione Spring che funziona su una classe `@RestController` può essere posizionata sull'interfaccia o sui suoi metodi: `@Transactional`, `@PreAuthorize`, `@Cacheable`, ecc.

### @ApiFind

Lega un'interfaccia controller o un metodo a una specifica entità JPA e al suo tipo di chiave primaria.

```java
@ApiFind(entity = Product.class, id = Long.class)
```

Può essere posizionata a **livello di tipo** (ereditata da tutti i metodi) o a **livello di metodo** (sovrascrive il binding a livello di tipo per quel metodo):

```java
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@RequestMapping("/api-exception/exception-audit")
public interface ExceptionAuditProxyController {

    // Usa il binding a livello di tipo → interroga ExExceptionAudit
    @PostMapping("/search")
    CollectionResponse<ExceptionAuditModel> findByFilter(@RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

    // Sovrascrive l'entità a livello di metodo → interroga ExUserAssigned
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

| Attributo | Tipo | Descrizione |
|-----------|------|-------------|
| `entity` | `Class<?>` | La classe entità JPA da interrogare |
| `id` | `Class<?>` | Il tipo della chiave primaria dell'entità |

### @ApiMapper

Specifica la classe mapper (e opzionalmente un metodo specifico) da usare per convertire i risultati della query prima di restituirli.

```java
@ApiMapper(value = ProductMapper.class, method = "toDto")
```

Se `method` è vuoto, il framework risolve automaticamente il primo metodo con un singolo argomento sulla classe mapper il cui tipo di parametro corrisponde all'entità. Il bean mapper viene recuperato dal contesto Spring.

Può essere posizionata a **livello di tipo** (si applica a tutti i metodi) o a **livello di metodo** (sovrascrive il mapper a livello di tipo per quel metodo):

```java
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@ApiMapper(ExApplicationMapper.class)               // mapper default per tutti i metodi
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

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `value` | `Class<?>` | — | La classe mapper (recuperata dal contesto Spring) |
| `method` | `String` | `""` | Nome del metodo (risolto automaticamente se vuoto) |

### @ApiBeforeFind

Registra un hook `BeforeFind` che viene eseguito **prima** dell'esecuzione della query.

```java
@PostMapping("/grant/search")
@ApiBeforeFind(GrantHandlerFindRequest.class)
CollectionResponse<ApplicationModel> findByFilter(
    @RequestBody ApplicationFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);
```

L'implementazione `BeforeFind` riceve il `BaseQueryParameter` corrente (permettendo di aggiungere parametri, modificare il filtro, applicare vincoli di sicurezza, ecc.) e gli argomenti grezzi del metodo. Gli argomenti extra del metodo (es. `@AuthenticationPrincipal`) vengono passati come `args` in ordine.

| Attributo | Tipo | Descrizione |
|-----------|------|-------------|
| `value` | `Class<? extends BeforeFind>` | L'implementazione `BeforeFind` (bean Spring o costruttore senza argomenti) |

### @ApiAfterFind

Registra un hook `AfterFind` che viene eseguito **dopo** che i risultati della query vengono restituiti.

```java
@PostMapping("/search/id")
@ApiAfterFind(UserHandlerFindRequest.class)
ObjectResponse<UserModel> findById(@RequestBody @Valid IdUserFilter idUserFilter);
```

L'implementazione `AfterFind` riceve l'oggetto risultato e gli argomenti grezzi del metodo, e può modificare o sostituire il risultato.

| Attributo | Tipo | Descrizione |
|-----------|------|-------------|
| `value` | `Class<? extends AfterFind>` | L'implementazione `AfterFind` (bean Spring o costruttore senza argomenti) |

### @ApiQuery

Posizionata opzionalmente su un metodo per eseguire una query JPQL con nome invece del percorso standard `findByFilter`. La stringa della query con nome è tipicamente definita come costante sull'interfaccia del servizio.

```java
@PostMapping("/all/names")
@PreAuthorize("hasAuthority('OWNER')")
@ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
CollectionResponse<ApplicationModel> searchName();
```

Quando `@ApiQuery` è presente con un valore, il framework esegue direttamente quella stringa JPQL. L'attributo `orderBy` inietta un ordinamento predefinito quando la richiesta non contiene parametri `OrderBy`.

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | La stringa della query da eseguire (SQL nativo o JPQL in base a `jpql`) |
| `jpql` | `boolean` | `false` | `false` (default) = SQL nativo; `true` = modalità JPQL dinamica (auto-costruisce JPQL da `value`) |
| `orderBy` | `@DefaultOrderBy[]` | `{}` | Ordinamenti predefiniti applicati quando non c'è `OrderBy` nel filtro |

> **Importante:** `jpql = false` (default) esegue `value` come stringa SQL nativo tramite `NativeQueryParameter`. `jpql = true` esegue la pipeline JPQL auto-generata; in questo caso `value` è la stringa JPQL, oppure lasciala vuota per far generare automaticamente la clausola WHERE al framework.

### @DefaultOrderBy

Definisce un singolo elemento di clausola `ORDER BY` predefinita usato dentro `@ApiQuery#orderBy()`. Voci multiple producono un ordinamento multi-colonna applicato quando il chiamante non fornisce alcuna chiave di ordinamento esplicita.

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

| Attributo | Tipo | Default | Descrizione |
|-----------|------|---------|-------------|
| `value` | `String` | — | Espressione di ordinamento JPQL o SQL (es. `"p.name"`, `"price"`) |
| `orderType` | `OrderType` | `OrderType.asc` | Direzione di ordinamento (`asc` o `desc`) |

---

## Interfacce hook

### BeforeFind

```java
public interface BeforeFind<E, ID> {
    void before(BaseQueryParameter<E, ID> parameters, Object... args);
}
```

Implementa questa interfaccia per eseguire logica prima della query. L'oggetto `parameters` è mutabile — puoi aggiungere parametri named, impostare la paginazione o iniettare vincoli di sicurezza.

**Esempio reale — iniezione di ID da una lookup risolta tramite sicurezza:**

```java
@Component
public class GrantHandlerFindRequest implements BeforeFind<ExApplication, Integer> {

    @Autowired
    private ExProjectService exProjectService;

    @Override
    public void before(BaseQueryParameter<ExApplication, Integer> parameters, Object... args) {
        // args[0] è l'@AuthenticationPrincipal passato al metodo del controller
        ExUserSecurity exUserSecurity = (ExUserSecurity) args[0];
        parameters.addParameter(
            ExApplicationQueryJpql.idProjectGrant,
            exProjectService.findIdProjectByEmail(exUserSecurity.getUsername())
        );
    }
}
```

L'array `args` contiene gli argomenti del metodo **nell'ordine di dichiarazione**, escludendo i parametri annotati con `@IgnoreMapping` (che vengono rimossi prima di costruire la mappa dei parametri della query) — vengono comunque passati agli hook.

### AfterFind

```java
public interface AfterFind<T> {
    T after(T result, Object... args);
}
```

Implementa questa interfaccia per post-elaborare i risultati. Il valore restituito sostituisce il risultato originale.

**Esempio reale — arricchimento di una risposta a risultato singolo con dati aggiuntivi:**

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

        // Carica il ruolo da una query separata
        QueryParameter<ExAssUserRole, ExAssUserRolePK> queryUserRole = new QueryParameter<>();
        queryUserRole.addParameter(ExAssUserRoleQueryJpql.idUser, response.getData().getId());
        ExAssUserRole userRole = exAssUserRoleService.singleResultByFilter(queryUserRole);

        response.getData().setIdRole(userRole.getId().getIdRole());
        response.getData().setPriority(userRole.getExRole().getPriority());

        // Carica le associazioni ai progetti
        List<Integer> idProjects = exProjectService.findIdProjectByIdUser(filter.getIdUser());
        if (CollectionUtils.isEmpty(idProjects))
            idProjects.add(-1);
        response.getData().setIdProject(idProjects);

        return response;
    }
}
```

---

## Binding dei parametri dei metodi

L'interceptor estrae i valori dai parametri dei metodi Spring MVC in base alle loro annotazioni:

| Annotazione Spring | Binding |
|-------------------|---------|
| `@RequestBody` | Trattato come filtro `BaseParameter`; tutti i campi non-null diventano parametri della query |
| `@RequestParam` | Aggiunto come parametro named |
| `@PathVariable` | Aggiunto come parametro named |
| `@AuthenticationPrincipal` | Passato agli hook come elemento di `args` — non aggiunto alla mappa dei parametri della query |
| `@RequestAttribute` | Aggiunto come parametro named |
| `@IgnoreMapping` | Impedisce che il parametro venga aggiunto alla mappa della query, ma viene comunque inoltrato agli hook |
| Nessuna (nessuna annotazione) | Ignorato |

### @IgnoreMapping sui parametri del metodo

`@IgnoreMapping` può essere posizionato su un parametro del metodo per impedire al framework di trattarlo come filtro di query, pur rendendolo disponibile agli hook `BeforeFind`/`AfterFind` tramite `args`. Questo è il pattern standard per `@AuthenticationPrincipal`:

```java
@PostMapping("/search")
CollectionResponse<ExceptionAuditModel> findByFilter(
    @RequestBody ExceptionAuditFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);
    //                        ↑ passato agli hook ma mai aggiunto alla clausola WHERE
```

### @ConditionsZones sui parametri del metodo

Quando un parametro del metodo (es. `@AuthenticationPrincipal`) dovrebbe contribuire a una **condizione SQL nativo basata su zone**, annotalo con `@ConditionsZones` per instradare il suo valore alla zona corretta:

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

Qui `exUserSecurity` è legato come parametro `email` (tramite `@Param`) e instradato alla zona `appCondition` nel template SQL nativo.

---

## Contenitori di risposta

L'interceptor determina come wrappare il risultato in base al tipo di ritorno dichiarato del metodo:

| Tipo di ritorno | Comportamento |
|----------------|--------------|
| `List<T>` / `Collection<T>` | Restituisce la lista di entità mappate |
| `T` (oggetto singolo) | Restituisce il risultato singolo (chiama `singleResultByFilter`) |
| `long` / `Long` / `Number` | Esegue `countByFilter` e restituisce il conteggio |
| `CollectionResponse<T>` | Restituisce un wrapper con `items`, `totalCount`, `pageNumber`, `pageSize` |
| `ObjectResponse<T>` | Restituisce un wrapper con `item` (risultato singolo) |

`CollectionResponse` è la scelta standard per le liste paginate. `ObjectResponse` viene usato per le lookup a elemento singolo dove hai bisogno di un wrapper JSON coerente:

```java
// Restituisce { "data": {...}, "status": ... }
@PostMapping("/search/id")
ObjectResponse<ApplicationModel> singleResultFindByFilter(
    @RequestBody @Valid IdApplicationFilter baseParameter);
```

---

## Flusso di esecuzione

```
Richiesta HTTP
  │
  ▼
ApiFindInterceptor.invoke()
  │  recupera FindInterceptor dal contesto Spring
  ▼
FindInterceptor.find()
  │
  ├─ Estrai parametri (@RequestBody, @RequestParam, @PathVariable, ...)
  │   - parametri @IgnoreMapping saltati per la mappa query, mantenuti per gli hook
  │   - parametri @ConditionsZones instradati alle zone con nome
  │
  ├─ Invoca hook @ApiBeforeFind (se presente)
  │   - passa BaseQueryParameter + tutti gli argomenti del metodo
  │
  ├─ Costruisce QueryParameter o NativeQueryParameter
  │
  ├─ Risolve JpaService<Entity, ID> dal contesto Spring
  │   usando ResolvableType con limiti generici
  │
  ├─ Esegue la query in base al tipo di ritorno:
  │   - CollectionResponse / List  → findByFilter
  │   - ObjectResponse / T         → singleResultByFilter
  │   - long / Long                → countByFilter
  │
  ├─ Invoca hook @ApiAfterFind (se presente)
  │   - passa il risultato + tutti gli argomenti del metodo
  │
  ├─ Mappa i risultati tramite @ApiMapper (se presente)
  │
  └─ Wrappa nel contenitore di risposta e restituisce
```

---

## Componenti interni

Capire le classi interne è utile quando si estende o si fa debug del framework.

| Classe | Ruolo |
|--------|-------|
| `EnableProxyApiController` | Annotazione punto di ingresso; importa `ProxyApiFindConfig` e `ApiFindRegistrar` |
| `ApiFindRegistrar` | `ImportBeanDefinitionRegistrar` che scansiona il classpath per le interfacce `@ApiFindController` e registra ognuna come bean Spring tramite `ProxyConfig.newProxyInstance()` |
| `ProxyApiFindConfig` | Spring `@Configuration` che attiva la scansione dei componenti per il package `com.bld.proxy.api.find` e abilita le utility comuni |
| `ProxyConfig` | Spring `@Component` factory che crea istanze di proxy JDK dinamico per le interfacce `@ApiFindController` usando `ApiFindInterceptor` come `InvocationHandler` |
| `ApiFindInterceptor` | `InvocationHandler` singleton registrato su ogni proxy; recupera un bean prototipo `FindInterceptor` fresco per ogni invocazione e gli delega |
| `FindInterceptor` | Componente con scope prototipo che esegue la query reale: estrazione parametri → hook `BeforeFind` → esecuzione query → invocazione mapper → hook `AfterFind` |
| `ParameterDetails` | Value object interno che cattura il `java.lang.reflect.Parameter` di un parametro del metodo, il suo valore a runtime e il suo indice di posizione |
| `ApiFindException` | Eccezione runtime non controllata lanciata quando l'invocazione del proxy o la risoluzione del mapper fallisce |

### Flusso di registrazione (avvio)

```
@EnableProxyApiController
  └─ importa ApiFindRegistrar
       └─ scansiona i package per le interfacce @ApiFindController
            └─ per ogni interfaccia:
                 BeanDefinitionBuilder.setFactoryMethodOnBean("newProxyInstance", "proxyConfig")
                 → ProxyConfig.newProxyInstance(interfaceClass)
                 → Proxy.newProxyInstance(..., ApiFindInterceptor)
                 → bean registrato nel contesto Spring come tipo interfaccia
```

### Flusso di gestione delle richieste (runtime)

```
Richiesta HTTP
  → chiamata metodo proxy
  → ApiFindInterceptor.invoke()                      [singleton]
      → applicationContext.getBean(FindInterceptor)  [nuovo prototipo per richiesta]
      → FindInterceptor.find()
          → estrai parametri @RequestBody / @RequestParam / @PathVariable
          → risolvi tipi entità + id di @ApiFind
          → invoca hook @ApiBeforeFind (se presente)
          → costruisce QueryParameter o NativeQueryParameter
          → risolve JpaService<E,ID> tramite ResolvableType
          → esegue: findByFilter / countByFilter / singleResultByFilter
          → applica @ApiMapper (entità → DTO tramite bean mapper)
          → invoca hook @ApiAfterFind (se presente)
          → restituisce risposta wrappata
```

---

## Gestione degli errori

Il framework lancia `ApiFindException` (non controllata) nelle seguenti situazioni:

| Scenario | Messaggio |
|----------|-----------|
| Annotazione `@ApiFind` mancante sull'interfaccia e sul metodo | `NullPointerException` su `apiFind.entity()` — assicurarsi che `@ApiFind` sia presente |
| `@ApiMapper` mancante quando il risultato necessita di mappatura | `"The class to convert the entity to output is not declared"` |
| Metodo mapper non trovato per la coppia entità/modello | `"Method mapper is not found"` |
| Trovati più metodi mapper compatibili | `"More compatible methods were found in the mapping class, use @ApiMethodMapper or @ApiMapper to select the method name"` |
| `@ApiQuery` SQL nativo con `value` vuoto | `"For native query the field 'value' can not be blank into ApiQuery"` |

**Gestione standard delle eccezioni:** `ApiFindException` estende `RuntimeException`. Configura uno Spring `@ControllerAdvice` / `@RestControllerAdvice` per mapparla a una risposta HTTP di errore:

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

## Esempi reali

### Controller base con sicurezza e caching

Un controller che espone due endpoint di ricerca per la stessa entità. Le annotazioni Spring standard (`@PreAuthorize`, `@Cacheable`, `@Transactional`) funzionano direttamente sui metodi dell'interfaccia.

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

### Override di annotazioni a livello di metodo

`@ApiFind` e `@ApiMapper` possono essere sovrascritte a livello di metodo. Qui il binding a livello di tipo punta a `ExExceptionAudit`, ma un metodo interroga `ExUserAssigned` e usa un metodo mapper specifico:

```java
@ApiFindController
@ApiFind(entity = ExExceptionAudit.class, id = Integer.class)
@ApiMapper(ExExceptionAuditMapper.class)
@RequestMapping("/api-exception/exception-audit")
public interface ExceptionAuditProxyController {

    // Usa entità e mapper a livello di tipo
    @PostMapping(path = "/search",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    CollectionResponse<ExceptionAuditModel> findByFilter(
        @RequestBody ExceptionAuditFilter filter,
        @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

    // Sovrascrive entità → interroga ExUserAssigned
    // Sovrascrive metodo mapper → chiama convertToModel invece del metodo auto-risolto
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

    // Risultato singolo per ID
    @PostMapping(path = "/search/id",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ObjectResponse<ExceptionAuditModel> searchById(@RequestBody IdExceptionAuditModel baseParameter);
}
```

### Query con nome tramite @ApiQuery

Quando il percorso standard basato su filtro è insufficiente, `@ApiQuery` esegue una stringa JPQL fissa definita come costante sull'interfaccia del servizio. `@DefaultOrderBy` imposta l'ordinamento quando il client non ne fornisce uno.

```java
// Sull'interfaccia del servizio:
public interface ExApplicationService extends JpaService<ExApplication, Integer> {
    String NAME_APPLICATION = "ExApplication.findNamesByUser";
    // ...
}

// Nel controller:
@ApiFindController
@ApiFind(entity = ExApplication.class, id = Integer.class)
@ApiMapper(ExApplicationMapper.class)
@RequestMapping("/api-exception/application")
public interface ApplicationProxyController {

    // Nessun @RequestBody — il metodo non accetta input filtro
    @PostMapping(path = "/all/names",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('OWNER')")
    @ApiQuery(value = ExApplicationService.NAME_APPLICATION, orderBy = @DefaultOrderBy("name"))
    @ResponseBody
    CollectionResponse<ApplicationModel> searchName();

    // Stessa query ma ristretta alle applicazioni dell'utente autenticato tramite @ConditionsZone
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

### Hook BeforeFind con @AuthenticationPrincipal

Un hook `BeforeFind` è il modo standard per iniettare parametri derivati dalla sicurezza (ID, contesto tenant, ecc.) nella query **prima** che venga eseguita. Il principal autenticato viene passato tramite `args[0]`:

```java
// Metodo controller — hook dichiarato, principal contrassegnato @IgnoreMapping
@PostMapping(path = "/grant/search",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@ApiBeforeFind(GrantHandlerFindRequest.class)
CollectionResponse<ApplicationModel> findByFilter(
    @RequestBody ApplicationFilter filter,
    @AuthenticationPrincipal @IgnoreMapping ExUserSecurity exUserSecurity);

// Implementazione hook — risolve gli ID consentiti dall'email dell'utente autenticato
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

Il bean `GrantHandlerFindRequest` inietta una lista di ID progetto a cui l'utente autenticato ha accesso. Il `@QueryBuilder` sul servizio dichiara un `@ConditionBuilder` corrispondente che attiva questo parametro nella clausola WHERE.

### Hook AfterFind per arricchire i risultati

`AfterFind` viene eseguito dopo che la query completa e può chiamare altri servizi per arricchire la risposta. Il corpo della richiesta (`args[0]`) e gli altri argomenti del metodo sono disponibili:

```java
// Metodo controller — hook dichiarato sul metodo
@PostMapping(path = "/search/id",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@ApiAfterFind(UserHandlerFindRequest.class)
ObjectResponse<UserModel> findById(@RequestBody @Valid IdUserFilter idUserFilter);

// Implementazione hook — carica ruolo e progetti e arricchisce la risposta
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

### Classe filtro con IDFilterParameter

`IDFilterParameter<ID>` è una classe base di convenienza che aggiunge un campo lista `id` (legato a una condizione `IN (...)`). Sovrascrive il nome della proprietà JSON per dargli un nome specifico del dominio:

```java
public class ApplicationFilter extends IDFilterParameter<Integer> {

    @LikeString(upperLowerType = UpperLowerType.UPPER, likeType = LikeType.NONE)
    private String name;

    private List<Integer> idEnvironment;

    private List<Integer> idProject;

    // Questo campo è popolato da GrantHandlerFindRequest — nascosto dalla deserializzazione JSON
    @JsonIgnoreProperties("idProjectGrant")
    private List<Integer> idProjectGrant;

    @LikeString
    private String version;

    // Rinomina il campo 'id' ereditato in JSON con il nome del dominio
    @Override
    @JsonProperty("idApplication")
    public List<Integer> getId() { return super.getId(); }

    @Override
    @JsonProperty("idApplication")
    public void setId(List<Integer> id) { super.setId(id); }
}
```

---

## Esempio end-to-end completo

Questo esempio mostra lo stack completo: entità, servizio, filtro, mapper e controller.

**Entità**

```java
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Boolean active;
    // getter / setter
}
```

**Interfaccia e implementazione del servizio**

L'annotazione `@QueryBuilder` sull'implementazione guida la generazione JPQL a tempo di compilazione. I percorsi dei campi seguono la notazione dot JPQL e possono attraversare più relazioni.

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

**Filtro**

```java
public class ProductFilter extends BaseParameter {

    @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
    private String name;

    private Boolean active;

    @ListFilter
    private List<Long> idCategory;

    // getter / setter
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

Questa è l'intera implementazione del controller. Non è necessaria alcuna classe `@RestController`.
