# jpa-service-plugin-generator

Plugin Maven che scansiona un package di classi entità JPA e genera la corrispondente interfaccia `*Service`, la classe `*ServiceImpl` e l'interfaccia `*Repository` per ogni entità. Eseguilo una volta per fare lo scaffolding del service layer — i file generati finiscono in `src/main/java` e sono tuoi da modificare.

---

## Indice dei contenuti

1. [Panoramica](#panoramica)
2. [Cosa viene generato](#cosa-viene-generato)
3. [Configurazione del plugin](#configurazione-del-plugin)
4. [Riferimento parametri](#riferimento-parametri)
5. [Esecuzione del plugin](#esecuzione-del-plugin)
6. [Struttura dei file generati](#struttura-dei-file-generati)
7. [Dopo la generazione](#dopo-la-generazione)

---

## Panoramica

Il plugin viene eseguito nella fase `generate-sources` del ciclo di vita Maven. Legge i file sorgente nel package delle entità (usando il parsing AST — senza compilazione), estrae il nome della classe entità e il tipo della chiave primaria, e scrive tre file sorgente Java per entità usando template FreeMarker.

Poiché l'output va in `src/main/java` (o in una directory configurata), i file vengono committati nel controllo di versione e modificati liberamente. Il plugin non sovrascriverà i file già esistenti — i build successivi sono sicuri.

---

## Cosa viene generato

Per ogni classe entità trovata in `persistencePackage`, il plugin produce:

### `*Repository` — interfaccia repository Spring Data

```java
@Repository
public interface ProductRepository extends BaseJpaRepository<Product, Long> {
}
```

`BaseJpaRepository<T, ID>` estende `JpaRepository<T, ID>` ed è il tipo di repository standard usato dal framework.

### `*Service` — interfaccia del servizio

```java
public interface ProductService extends JpaService<Product, Long> {
}
```

Estendi questa interfaccia per aggiungere metodi di servizio personalizzati oltre a quelli standard CRUD / basati su filtro.

### `*ServiceImpl` — implementazione del servizio

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

L'annotazione `@QueryBuilder` viene inserita senza attributi — aggiungi condizioni, join e definizioni di ordinamento dopo che il file è stato generato (vedi la documentazione di `processor-jpa-service`).

---

## Configurazione del plugin

Aggiungi il plugin alla sezione `<build><plugins>` del tuo `pom.xml`:

```xml
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
        <persistencePackage>com.example.domain</persistencePackage>
        <servicePackage>com.example.service</servicePackage>
        <basePackage>com.example</basePackage>
        <!-- opzionale -->
        <repositoryPackage>com.example.repository</repositoryPackage>
        <outputDirectory>src_main_java</outputDirectory>
    </configuration>
</plugin>
```

---

## Riferimento parametri

| Parametro | Obbligatorio | Default | Descrizione |
|-----------|-------------|---------|-------------|
| `persistencePackage` | Sì | — | Package pienamente qualificato contenente le classi entità JPA da scansionare |
| `servicePackage` | Sì | — | Package di destinazione dove vengono scritti i file `*Service` e `*ServiceImpl` |
| `basePackage` | Sì | — | Package radice dell'applicazione (usato per la risoluzione del percorso di output) |
| `repositoryPackage` | No | Derivato dal package entità | Package di destinazione dove vengono scritti i file `*Repository`. Default al package della classe entità con il segmento del nome classe rimosso |
| `outputDirectory` | No | `src_main_java` | Tipo di directory di output. `src_main_java` scrive direttamente in `src/main/java` |
| `resourceTemplateDirectory` | No | `/template` | Posizione dei template FreeMarker all'interno del JAR del plugin |

### Comportamento default di repositoryPackage

Se `repositoryPackage` non è impostato, il repository viene scritto nello stesso package dell'entità. Per esempio, se un'entità è `com.example.domain.Product`, il repository sarà `com.example.domain.ProductRepository`.

Per mantenere i repository in un package separato, imposta `repositoryPackage` esplicitamente:

```xml
<repositoryPackage>com.example.repository</repositoryPackage>
```

---

## Esecuzione del plugin

Il plugin è vincolato alla fase `generate-sources` e viene eseguito automaticamente durante un build normale. Per avviarlo in isolamento:

```bash
mvn com.bld.commons:jpa-service-plugin-generator:jpa-service-generator
```

O, se il plugin è nel tuo POM sotto `<pluginManagement>`:

```bash
mvn generate-sources
```

I file vengono scritti solo se non esistono già. Aggiungi le directory di output a `.gitignore` se preferisci rigenararle, oppure committale nel controllo di versione per tracciare le tue personalizzazioni.

---

## Struttura dei file generati

Dato `persistencePackage = com.example.domain`, `servicePackage = com.example.service`, `repositoryPackage = com.example.repository`, e le entità `Product`, `Order`, `Customer`:

```
src/main/java/
  com/example/
    domain/
      Product.java          ← la tua entità (non modificata)
      Order.java
      Customer.java
    repository/
      ProductRepository.java   ← generato
      OrderRepository.java     ← generato
      CustomerRepository.java  ← generato
    service/
      ProductService.java      ← generato
      ProductServiceImpl.java  ← generato
      OrderService.java        ← generato
      OrderServiceImpl.java    ← generato
      CustomerService.java     ← generato
      CustomerServiceImpl.java ← generato
```

---

## Dopo la generazione

Una volta generati i file:

1. **Aggiungi gli attributi `@QueryBuilder`** a ogni `*ServiceImpl` per definire condizioni, join e chiavi di ordinamento. L'annotation processor (`processor-jpa-service`) li legge per generare la classe `*QueryJpqlImpl`.

2. **Aggiungi metodi personalizzati** all'interfaccia `*Service` e implementali in `*ServiceImpl` secondo necessità.

3. **Configura l'annotation processor** nel plugin Maven compiler affinché l'annotazione `@QueryBuilder` su ogni `*ServiceImpl` venga rilevata a tempo di compilazione:

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

Consulta la [documentazione di processor-jpa-service](../processor-jpa-service/README.it.md) per il riferimento completo degli attributi di `@QueryBuilder`.
