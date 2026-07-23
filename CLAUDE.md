# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot CLI tool that crawls a website starting from a given URL, checks all discovered links, and reports their HTTP status. Designed for checking blog/static site links before publishing.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=LinksCrawlerImplTest

# Run the application (requires exactly one argument: the website URL)
java -jar target/links-checker-0.0.1-SNAPSHOT.jar <url>

# Run directly via Maven
mvn spring-boot:run -Dspring-boot.run.arguments=<url>
```

## Runtime behavior

The app runs non-interactively and exits after a single crawl:
1. Crawls `<url>` and all discovered internal links
2. Logs total link count and throughput (links/s)
3. Logs all bad links (4xx+)
4. Logs all moved/redirect links (3xx)

If no argument is provided, it logs an error and exits with code 1.

## Architecture

The application is wired manually via `LinksCheckerConfig` (no component scanning for service/core classes):

```
CheckCommand → LinksCrawler
                 ├── ContentRetriever  (HTTP GET via java.net.http.HttpClient)
                 ├── LinkRetriever     (extracts <a href> links via Jsoup, resolved to absolute URLs)
                 └── LinksManager      (tracks URL state: null=unvisited, BORROWED=in-progress, PageResult=done)
```

**Crawl flow** (`LinksCrawler.processSite`):
1. Seed `LinksManager` with the start URL
2. Wave loop: drain all pending links, group them by host, fetch each group on its own virtual thread — hosts are checked in parallel, but each host sees at most one request at a time
3. If a fetched URL is under `startUrl`, extract its links (absolute, fragment-stripped, http(s) only) and add them to `LinksManager`
4. External links are fetched (to check status) but not crawled deeper
5. Results stored as `PageResult(url, location-or-null, httpStatusCode)` — page bodies are parsed then dropped; only the `Location` of 3xx responses is kept

**Status codes** (`LinksClassifier`):
- `0` = "BORROWED" (in-progress sentinel)
- 2xx = good, 3xx = redirect/moved, 4xx+ = bad

## Key Technical Details

- Java 25, Spring Boot 4.1.0
- `LinksManager` is stateful and shared — call `reset()` between crawls; it is only accessed from the crawl thread (workers return `FetchOutcome`s, state updates happen between waves)
- 301/302 responses: `content` field holds the `Location` header value, not the body
- Tests use OkHttp `MockWebServer` to simulate HTTP responses
