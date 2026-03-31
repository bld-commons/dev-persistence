# dev-persistence

**Versione:** 3.0.16 | **Java:** 17+ | **Spring Boot:** 3.x

Un framework multi-modulo che elimina il boilerplate JPA attraverso la generazione di codice a compile time e la costruzione dinamica delle query a runtime.

> Documentazione disponibile anche in: [English](README.md)

---

## Moduli

| Modulo | Descrizione |
|--------|-------------|
| [common-jpa-service](common-jpa-service/README.it.md) | Motore runtime principale: esecuzione dinamica di JPQL/SQL, binding dei filtri via reflection, astrazioni di servizio |
| [processor-jpa-service](processor-jpa-service/README.it.md) | Annotation processor a compile time che genera le classi `QueryJpqlImpl` a partire da `@QueryBuilder` |
| [proxy-api-controller](proxy-api-controller/README.it.md) | Framework per controller REST dinamici: genera endpoint API tramite annotazioni, senza scrivere codice controller |
| [jpa-service-plugin-generator](jpa-service-plugin-generator/README.it.md) | Plugin Maven che genera automaticamente le classi service, service-impl e repository a partire dalle entità JPA |
| [project-jpa-persistence](project-jpa-persistence/README.it.md) | Applicazione Spring Boot di esempio che mostra il framework completo in azione |

---

## Architettura

```
┌──────────────────────────────────────────────────────────┐
│                 La tua applicazione Spring Boot          │
│                                                          │
│  ┌───────────────────┐   ┌──────────────────────────┐   │
│  │  REST Controller  │   │   proxy-api-controller   │   │
│  │     (manuale)     │   │    (@ApiFindController)  │   │
│  └────────┬──────────┘   └────────────┬─────────────┘   │
│           │                           │                  │
│           └─────────────┬─────────────┘                  │
│                         ▼                                │
│              ┌─────────────────────┐                     │
│              │  JpaService<T, ID>  │  ← common-jpa-service│
│              │   (JpaServiceImpl)  │                     │
│              └─────────┬───────────┘                     │
│                        │                                 │
│        ┌───────────────┴───────────────┐                 │
│        ▼                               ▼                 │
│  ┌──────────────┐            ┌──────────────────┐        │
│  │ JpaRepository│            │  QueryJpqlImpl   │        │
│  │ (Spring Data)│            │ (generata dal    │        │
│  └──────────────┘            │  processor)      │        │
│                              └──────────────────┘        │
└──────────────────────────────────────────────────────────┘

Compile time:
  processor-jpa-service        → genera QueryJpqlImpl
  jpa-service-plugin-generator → genera le classi Service/Repository
```

---

## Come funziona

**A compile time**

1. `jpa-service-plugin-generator` (plugin Maven, fase `generate-sources`) analizza il package delle entità configurato e genera gli scheletri di `*Service`, `*ServiceImpl` e `*Repository`.
2. `processor-jpa-service` (annotation processor) legge ogni interfaccia di servizio annotata con `@QueryBuilder` e genera la corrispondente classe `*QueryJpqlImpl`, contenente le stringhe JPQL pre-costruite e le mappe delle condizioni.

**A runtime**

1. Spring inietta automaticamente i servizi e i repository generati.
2. Un'interfaccia `@ApiFindController` viene registrata come `@RestController` Spring tramite un dynamic proxy Java.
3. Ogni richiesta HTTP viene gestita da `FindInterceptor`, che costruisce un `QueryParameter` dai dati della richiesta, risolve il `JpaService` corretto ed esegue la query.
4. `JpaServiceImpl` delega l'esecuzione a `BaseJpaService`, che assembla dinamicamente la clausola WHERE, i JOIN FETCH, ORDER BY e la paginazione.
5. I risultati vengono mappati in DTO tramite `@ApiMapper` e restituiti al client.

---

## Quick Start

Aggiungi le dipendenze runtime e configura l'annotation processor nel tuo progetto:

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
</dependencies>

<build>
    <plugins>
        <!-- Generatore di scaffold (eseguire una sola volta) -->
        <plugin>
            <groupId>com.bld.commons</groupId>
            <artifactId>jpa-service-plugin-generator</artifactId>
            <version>3.0.16</version>
            <configuration>
                <persistencePackage>com.example.domain</persistencePackage>
                <servicePackage>com.example.service</servicePackage>
                <basePackage>com.example</basePackage>
            </configuration>
        </plugin>

        <!-- Annotation processor (genera QueryJpqlImpl a compile time) -->
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
    </plugins>
</build>
```

Vedi [project-jpa-persistence](project-jpa-persistence/README.it.md) per un esempio completo e funzionante.

---

## Documentazione completa

La documentazione dettagliata di ogni modulo si trova nel proprio README (link nella tabella sopra). La cartella `docs/` contiene un riferimento esteso che copre l'intera API.
