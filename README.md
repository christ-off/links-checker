# Links Checker

[![CodeQL](https://github.com/christ-off/links-checker/actions/workflows/codeql.yml/badge.svg)](https://github.com/christ-off/links-checker/actions/workflows/codeql.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=christ-off_links-checker&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=christ-off_links-checker)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=christ-off_links-checker&metric=coverage)](https://sonarcloud.io/summary/new_code?id=christ-off_links-checker)

CLI tool that crawls a website and reports broken (4xx+) and moved (3xx) links.
Made for checking a blog or static site before publishing.

## Requirements

- Java 25

## Build

```bash
mvn clean package
```

## Usage

```bash
java -jar target/links-checker-0.0.1-SNAPSHOT.jar https://example.com/
```

Sample output:

```
I.Links : 2
Time: 0s | Links/s: 3.2

--- Queries per external host ---
iana.org: 1
example.com: 1
--- BAD LINKS ---

--- MOVED LINKS ---
https://iana.org/domains/example -> https://www.iana.org/domains/example
```

Progress symbols: `I` internal page, `.` external link OK, `4`/`5` external link
failed with a 4xx/5xx, `s` skipped host.

Without an argument the tool prints usage and exits with code 1.

## How it works

- Pages under the start URL are crawled; external links are checked but not crawled deeper.
- Links are fetched in parallel on virtual threads, grouped by host: a given host
  never sees more than one request at a time.
- Relative links are resolved against the page they appear on; fragments are
  stripped; only `http(s)` links are followed (no `mailto:`, `javascript:`, ...).

## Configuration

Defaults live in [`src/main/resources/application.yml`](src/main/resources/application.yml):

| Property                          | Default | Description                            |
|-----------------------------------|---------|----------------------------------------|
| `links-checker.request-timeout-seconds` | `30`    | HTTP timeout per request               |
| `links-checker.sites-to-skip`     | see yml | Hosts that are never fetched           |

To override without rebuilding, place an `application.yml` next to the jar
(Spring Boot picks it up automatically).
