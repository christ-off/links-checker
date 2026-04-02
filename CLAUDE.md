# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot CLI tool (using Spring Shell) that crawls a website starting from a given URL, checks all discovered links, and reports their HTTP status. Designed for checking blog/static site links before publishing.

## Build & Run Commands

```bash
# Build
mvn clean package

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=LinksCrawlerImplTest

# Run the application (interactive shell)
java -jar target/links-checker-0.0.1-SNAPSHOT.jar

# Run directly via Maven
mvn spring-boot:run
```

## Shell Commands (at runtime)

Once running, the interactive Spring Shell exposes:
- `check --website <url>` — crawl a site (default: `http://localhost:4000`)
- `good` — list all 2xx links from last crawl
- `bad` — list all 4xx+ links from last crawl
- `moved` — list all 3xx redirect links from last crawl

## Architecture

The application is wired manually via `LinksCheckerConfig` (no component scanning for service/core classes):

```
CheckCommand → LinksCrawler
                 ├── ContentRetriever  (HTTP GET via java.net.http.HttpClient)
                 ├── LinkRetriever     (extracts <a href> links from HTML via Jsoup)
                 └── LinksManager      (tracks URL state: null=unvisited, BORROWED=in-progress, PageResult=done)
```

**Crawl flow** (`LinksCrawler.processSite`):
1. Seed `LinksManager` with the start URL
2. Loop: pick next unprocessed link, fetch it via `ContentRetriever`
3. If the fetched URL is under `startUrl`, extract new links and add them to `LinksManager`
4. External links are fetched (to check status) but not crawled deeper
5. Results stored as `PageResult(url, content/location, httpStatusCode)`

**Status codes** (`LinksClassifier`):
- `0` = "BORROWED" (in-progress sentinel)
- 2xx = good, 3xx = redirect/moved, 4xx+ = bad

## Key Technical Details

- Java 25, Spring Boot 4.0.5, Spring Shell 4.0.1
- `LinksManager` is stateful and shared — call `reset()` between crawls
- 301/302 responses: `content` field holds the `Location` header value, not the body
- Tests use OkHttp `MockWebServer` to simulate HTTP responses
