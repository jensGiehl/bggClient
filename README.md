# BGG Client

A lightweight Java 21 client for the [BoardGameGeek XML API v2](https://boardgamegeek.com/wiki/page/BGG_XML_API2).

It wraps every public read endpoint (`/thing`, `/family`, `/forumlist`, `/forum`, `/thread`, `/user`, `/guild`, `/plays`, `/collection`, `/hot`, `/search`) behind a small, immutable, thread-safe Java API. Responses are parsed into typed model classes; paginated endpoints offer single-page fetches, lazy `Stream`s and eager load-all variants. Retries for the typical BGG transient responses (`202 Accepted` on `/collection`, `429 Too Many Requests`, `503 Service Unavailable`) are built in.

## Minimal dependencies

The library is deliberately small. The only runtime dependency is Jackson XML for parsing; HTTP is handled by the JDK's built-in `java.net.http.HttpClient`.

| Dependency                                   | Scope     | Purpose                          |
|----------------------------------------------|-----------|----------------------------------|
| `com.fasterxml.jackson.dataformat:jackson-dataformat-xml` | runtime   | XML deserialisation              |
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

The client sends the token as an `apikey` query parameter on every request. The parameter name is configurable via `BggClientConfig.apiKeyParameter(...)` if your deployment needs a different name.

A few endpoints (`/user`, `/plays`, `/collection`, `/guild`) additionally operate on data tied to a specific BGG account. For those you pass the **username** (not credentials) as part of the request — separately from the API token used for authentication.

## Installation

The artifact is built with Maven. From the project root:

```bash
mvn install
```

Then declare the dependency:

```xml
<dependency>
    <groupId>de.agiehl</groupId>
    <artifactId>bgg-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

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

A single `BggClient` is immutable and thread-safe — share one instance across the application.

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

Retryable responses (`202`, `429`, `503`) are absorbed transparently up to `maxRetries`; only when the retry budget is exhausted does a `BggHttpException` surface.
