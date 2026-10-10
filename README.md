# BGG Client

A lightweight Java 21 client for the [BoardGameGeek XML API v2](https://boardgamegeek.com/wiki/page/BGG_XML_API2).

It wraps every public read endpoint (`/thing`, `/family`, `/forumlist`, `/forum`, `/thread`, `/user`, `/guild`, `/plays`, `/collection`, `/hot`, `/search`) behind a small, thread-safe Java API with immutable configuration. Responses are parsed into typed model classes; paginated endpoints offer single-page fetches, lazy `Stream`s and eager load-all variants. Retries for the typical BGG transient responses (`202 Accepted` on `/collection`, `429 Too Many Requests`, `503 Service Unavailable`) are built in. An optional website login enables access to private collection fields.

## Minimal dependencies

The library is deliberately small. The only runtime dependency is Jackson XML for parsing; HTTP is handled by the JDK's built-in `java.net.http.HttpClient`.

| Dependency                                   | Scope     | Purpose                          |
|----------------------------------------------|-----------|----------------------------------|
| `tools.jackson.dataformat:jackson-dataformat-xml` | runtime   | XML deserialisation              |
| `org.projectlombok:lombok`                   | provided  | compile-time boilerplate removal |

No HTTP client, no logging framework, no Spring, no Guava.

## Logging

The client uses **`java.util.logging` (JUL)** — no SLF4J, no Logback, no Log4j. Configure verbosity through the standard JDK mechanisms, e.g. a `logging.properties` file passed via `-Djava.util.logging.config.file=...`, or programmatically:

```java
Logger.getLogger("de.agiehl.bgg").setLevel(Level.FINE);
```

If you already use SLF4J in your application, bridge JUL with `jul-to-slf4j`.

## Getting a BoardGameGeek API key

Since October 2025, BoardGameGeek requires every XML API caller to register and use an API token. Registration opened on June 10 2025; unauthenticated traffic has been progressively cut off ever since. Without a valid token the BGG server will return an HTTP error before reaching the XML layer.

To obtain a token:

1. Create a free BGG account at <https://boardgamegeek.com/register> (or sign in to an existing one).
2. Open the applications page: <https://boardgamegeek.com/applications>.
3. Register a new application — give it a name and a short description.
4. Once the registration is accepted, copy the API key shown for your application.

Then hand the token to the client:

```java
BggClient client = BggClient.of("your-bgg-api-token");
```

The client sends the token as an `Authorization: Bearer ...` header on every API request.

A few endpoints (`/user`, `/plays`, `/collection`, `/guild`) additionally operate on data tied to a specific BGG account. For those you pass the **username** (not credentials) as part of the request — separately from the API token used for authentication.

## Installation

The library requires Java 21 or newer at runtime. Building requires JDK 21 or newer and Maven 3.9.0 or newer. Maven compiles with `--release 21`, targeting Java 21 bytecode and APIs even when building with a newer JDK. Source files, reports and XML responses use UTF-8. From the project root:

```bash
mvn install
```

Then declare the dependency:

```xml
<dependency>
    <groupId>de.agiehl</groupId>
    <artifactId>bgg-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Dependency maintenance

Dependencies were checked against Maven Central on 2026-10-06. Stable releases are used; milestone, release-candidate and snapshot versions are excluded.

| Dependency | Version |
|------------|---------|
| Jackson XML | 3.2.3 |
| Lombok | 1.18.48 |
| JUnit Jupiter (tests only) | 6.1.3 |
| Woodstox (transitive XML parser) | 7.3.0 |
| Stax2 API (transitive) | 4.3.1 |
| JSpecify (transitive, tests only) | 1.0.1 |

Maven lifecycle and reporting plugins are pinned in `pom.xml`. GitHub Actions use checkout 7.0.1, setup-java 6.0.1 and upload-artifact 7.0.1; all workflows run on Java 21.

The migration to [Jackson 3](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md) changes mapper, XML annotation and deserializer packages to `tools.jackson`. Core annotations retain `com.fasterxml.jackson.annotation`. Custom mappers passed to `HttpExecutor` must use the Jackson 3 `XmlMapper`. `lombok.config` configures `@Jacksonized` to generate Jackson 3 builder annotations. Parse failures remain wrapped in `BggParseException`.

Run the complete build, including unit tests, sources and Javadoc artifacts:

```bash
mvn clean verify
```

The live API smoke tests additionally require `BGG_API_KEY`; without it, JUnit skips them.

## Automatische GitHub-Releases

Der Workflow `.github/workflows/release.yml` startet bei einem Push auf `main` oder `master` und verwendet den aktuellen Stand des jeweiligen Branches. Vor dem Build erhöht das Maven Versions Plugin die Patch-Version in `pom.xml`, beispielsweise von `1.0.4` auf `1.0.5`. Die Version muss das Format `MAJOR.MINOR.PATCH` haben.

Nach einem erfolgreichen Build committet `github-actions[bot]` die geänderte `pom.xml` und pusht sie zurück auf denselben Branch. Anschließend erstellt der Workflow den GitHub-Release mit dem Tag `v<VERSION>-<LAUFNUMMER>`, beispielsweise `v1.0.5-42`. Der Tag zeigt auf den neuen Versions-Commit; die angehängten JARs einschließlich Sources und Javadoc verwenden ebenfalls die erhöhte Version.

Release-Läufe desselben Branches laufen nacheinander. Wenn während des Builds weitere Commits auf den Branch gepusht werden, schlägt der Versions-Push fehl und dieser Lauf erstellt keinen Release. Der wartende Lauf verwendet anschließend den aktuellen Branch-Stand.

Der Workflow benötigt `contents: write`; Branch-Schutzregeln müssen den Push durch GitHub Actions erlauben. Der integrierte `GITHUB_TOKEN` verhindert, dass der automatische Versions-Push erneut Push-Workflows auslöst ([GitHub-Dokumentation](https://docs.github.com/en/actions/how-tos/write-workflows/choose-when-workflows-run/trigger-a-workflow#triggering-a-workflow-from-a-workflow)). Nach einem Release sollte lokal zunächst `git pull` ausgeführt werden, um den Versions-Commit zu übernehmen. Ein vollständig erneut gestarteter Workflow-Lauf erhöht die Patch-Version noch einmal.

## Creating a client

Default configuration:

```java
BggClient client = BggClient.of("your-bgg-api-token");
```

Custom configuration:

```java
BggClient client = BggClient.builder()
    .config(BggClientConfig.builder()
        .apiKey("your-bgg-api-token")
        .connectTimeout(Duration.ofSeconds(15))
        .requestTimeout(Duration.ofSeconds(60))
        .maxRetries(5)
        .retryBackoff(Duration.ofSeconds(10))
        .userAgent("my-app/1.0")
        .build())
    .build();
```

A single `BggClient` is thread-safe and has immutable configuration. Complete an optional login before sharing the instance across the application. Use a separate client for each account.

## Optionaler Login für private Felder

Der API-Token bleibt erforderlich. Zusätzlich kannst du dich mit Benutzername und Passwort bei BoardGameGeek anmelden:

```java
BggClient client = BggClient.of(System.getenv("BGG_API_KEY"));
client.login(System.getenv("BGG_LOGIN_USERNAME"), System.getenv("BGG_LOGIN_PASSWORD"));

CollectionResponse collection = client.collections().fetch(
    CollectionRequest.builder()
        .username(System.getenv("BGG_LOGIN_USERNAME"))
        .showPrivate(true)
        .build());

collection.getItems().stream()
    .map(CollectionItem::getPrivateinfo)
    .filter(Objects::nonNull)
    .forEach(info -> System.out.println(info.getInventorylocation()));
```

Ohne den Aufruf von `login(...)` funktioniert der Client wie bisher mit dem API-Token. Laut [BGG-Dokumentation](https://boardgamegeek.com/wiki/page/BGG_XML_API2) liefert `showprivate=1` private Daten nur für die Sammlung des angemeldeten Benutzers. Verwende die vollständige Collection-Abfrage ohne `brief(true)`.

Der Login sendet die Zugangsdaten als UTF-8-JSON an `/login/api/v1` auf dem Host der konfigurierten `baseUri`. Beim Standard-Host ist das `https://boardgamegeek.com/login/api/v1`. Das Passwort wird nicht im Client oder seiner Konfiguration gespeichert. Ein `CookieManager` merkt sich die vom Server gesetzten Cookies pro Client im Arbeitsspeicher und sendet alle passenden Cookies automatisch bei sämtlichen API-Anfragen, Folgeseiten und Wiederholungsversuchen mit. Domain, Pfad, `Secure` und Ablaufzeit werden berücksichtigt; Cookie-Änderungen aus API-Antworten werden ebenfalls übernommen. Die Cookies werden nicht auf Festplatte gespeichert. Nach einem Neustart oder dem Ablauf der Sitzung ist ein erneuter Login nötig.

Ein erfolgreicher Login benötigt einen HTTP-Erfolgsstatus und gültige `bggusername`- und `bggpassword`-Cookies für die API. Bei einer abgelehnten Anmeldung oder fehlenden Authentifizierungs-Cookies wird `BggAuthenticationException` geworfen. Login-Antworttexte werden nicht in diese Exception übernommen. Jeder Login ersetzt die bisherige Sitzung; schlägt der Login-Request fehl, werden die Cookies gelöscht. Login-Weiterleitungen werden nicht verfolgt. Führe einen erneuten Login aus, wenn keine API-Anfragen mehr laufen.

`CollectionItem.getPrivateinfo()` liefert ein `CollectionPrivateInfo` mit Kaufpreis und Währung (`pricepaid`, `ppCurrency`), aktuellem Wert und Währung (`currvalue`, `cvCurrency`), Anzahl (`quantity`), Erwerbsdatum (`acquisitiondate`), Bezugsquelle (`acquiredfrom`), Lagerort (`inventorylocation`) und privatem Kommentar (`privatecomment`). Geldbeträge verwenden `BigDecimal`. Nicht gelieferte private Daten bleiben `null`; leere Zahlenattribute werden ebenfalls als `null` gelesen.

Wenn du über den erweiterten `HttpExecutor`-Konstruktor einen eigenen `HttpClient` verwendest, braucht dieser für den Login einen eigenen `CookieManager` und `HttpClient.Redirect.NEVER`. Teile diesen CookieManager nicht zwischen verschiedenen Konten.

## Endpoint examples

### `/thing` — games, expansions, accessories

```java
// Convenience: fetch Catan (id 13) with default options.
ThingResponse catan = client.things().fetchById(13);

// Full request with stats and videos for multiple ids.
ThingResponse response = client.things().fetch(
    ThingRequest.builder()
        .ids(List.of(13, 9209))
        .stats(true)
        .videos(true)
        .build());

// Paginated comments as a lazy stream.
client.things()
    .comments(ThingRequest.builder().id(13).comments(true).build())
    .limit(500)
    .forEach(comment -> System.out.println(comment.getValue()));
```

### `/family` — family groupings

```java
FamilyResponse family = client.families().fetchById(5666);

FamilyResponse multiple = client.families().fetch(
    FamilyRequest.builder()
        .ids(List.of(5666, 24281))
        .build());
```

### `/forumlist` — forums attached to a Thing or Family

```java
ForumListResponse forums = client.forumLists().fetch(
    ForumListRequest.builder()
        .id(13)
        .type(ForumListType.THING)
        .build());
```

### `/forum` — threads of a forum

```java
// First page only.
ForumResponse page1 = client.forums().fetchById(26);

// Lazy stream of thread summaries across all pages.
client.forums()
    .threads(ForumRequest.builder().id(26).build())
    .limit(100)
    .forEach(thread -> System.out.println(thread.getSubject()));

// Eager load-all (use sparingly for large forums).
List<ThreadSummary> all = client.forums().allThreads(
    ForumRequest.builder().id(26).build());
```

### `/thread` — articles of a thread

```java
ThreadResponse thread = client.threads().fetchById(1234567);

ThreadResponse filtered = client.threads().fetch(
    ThreadRequest.builder()
        .id(1234567)
        .count(10)
        .minArticleId(50000000)
        .build());
```

### `/user` — user profile, buddies, guilds

```java
// Basic profile.
UserResponse user = client.users().fetchByName("Aldie");

// Stream paginated buddies.
client.users()
    .buddies(UserRequest.builder().name("Aldie").buddies(true).build())
    .forEach(buddy -> System.out.println(buddy.getName()));

// Eagerly load every guild for a user.
List<UserGuild> guilds = client.users().allGuilds(
    UserRequest.builder().name("Aldie").guilds(true).build());
```

### `/guild` — guild metadata and members

```java
GuildResponse guild = client.guilds().fetchById(1234);

// Lazy stream of members (requires members(true)).
client.guilds()
    .members(GuildRequest.builder().id(1234).members(true).build())
    .forEach(member -> System.out.println(member.getName()));
```

### `/plays` — logged plays

```java
PlaysResponse first = client.plays().fetchByUsername("Aldie");

// Filter plays in a date range and stream them.
client.plays()
    .items(PlaysRequest.builder()
        .username("Aldie")
        .minDate("2026-01-01")
        .maxDate("2026-06-01")
        .build())
    .forEach(play -> System.out.println(play.getDate()));
```

### `/collection` — a user's collection

```java
// Convenience: full collection. May transparently retry on 202 Accepted
// while BGG builds the response on the server.
CollectionResponse owned = client.collections().fetchByUsername("Aldie");

// Filtered: only games rated >= 8 that the user owns.
CollectionResponse top = client.collections().fetch(
    CollectionRequest.builder()
        .username("Aldie")
        .owned(true)
        .minRating(8.0)
        .build());
```

### `/hot` — current hotness list

```java
// Convenience: hottest board games right now.
HotResponse hot = client.hot().fetchBoardgames();

// Other targets, e.g. people.
HotResponse hotPeople = client.hot().fetch(
    HotRequest.builder().type(HotType.BOARDGAMEPERSON).build());
```

### `/search` — title search

```java
// Convenience: fuzzy search.
SearchResponse hits = client.search().search("Catan");

// Restricted by type.
SearchResponse expansions = client.search().fetch(
    SearchRequest.builder()
        .query("Catan")
        .types(Set.of(ThingType.BOARDGAMEEXPANSION))
        .build());
```

## Error handling

All client failures are wrapped in `BggClientException`:

- `BggHttpException` — non-retryable HTTP failure, including the status code.
- `BggParseException` — the XML response could not be deserialised.
- `BggAuthenticationException` — the optional website login was rejected or returned no valid authentication cookies.

Retryable responses (`202`, `429`, `503`) are absorbed transparently up to `maxRetries`; only when the retry budget is exhausted does a `BggHttpException` surface.
