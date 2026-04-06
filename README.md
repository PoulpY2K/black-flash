# ⚡ Blackflash

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 25](https://img.shields.io/badge/Java-25-blue.svg)](https://adoptium.net/)
[![Spring Boot 4.0.5](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build](https://github.com/PoulpY2K/blackflash/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/PoulpY2K/blackflash/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=poulpy2k_blackflash&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=poulpy2k_blackflash)

**Blackflash** is a Discord music bot built with **Spring Boot 4.0.5** and **Java 25**. It uses [JDA v6.4.1](https://github.com/discord-jda/JDA) for Discord API integration and [Lavalink Client v3.4.0](https://github.com/lavalink-devs/lavalink-client) for high-quality audio streaming via an external [Lavalink v4](https://github.com/lavalink-devs/Lavalink) node.

--

## ✨ Features

- 🎵 **Music playback** from YouTube, Spotify, SoundCloud, Bandcamp, Twitch, Vimeo, and more
- 🔗 **Lavalink v4** integration for performant, high-quality audio streaming
- 🔒 **DAVE E2E audio encryption** — Discord's end-to-end encrypted voice protocol
- ⚡ **Virtual threads** — leverages Java 25 virtual threads for efficient async I/O
- 📊 **Observability** — Actuator endpoints, Elasticsearch metrics export, and OpenTelemetry tracing
- 🛡️ **Resilience** — Circuit breaker support via Resilience4j
- 🧪 **Comprehensive test suite** with JaCoCo code coverage and SonarQube integration

## 🎮 Slash Commands

| Command    | Description                                        |
|------------|----------------------------------------------------|
| `/help`    | Display help information                           |
| `/join`    | Join the voice channel                             |
| `/play`    | Play a song or playlist from a URL or search query |
| `/skip`    | Skip the current track                             |
| `/stop`    | Stop playback and clear the queue                  |
| `/loop`    | Loop the current track or playlist                 |
| `/shuffle` | Shuffle the playlist                               |
| `/leave`   | Leave the voice channel                            |

---

## 📋 Prerequisites

- **Java 25** — [Eclipse Adoptium JDK](https://adoptium.net/) or compatible
- **Maven 3.9.14+**
- **Docker & Docker Compose** — for running the Lavalink node locally
- A **Discord Bot Token** — from the [Discord Developer Portal](https://discord.com/developers/applications)
- *(Optional)* **Spotify credentials** — Client ID, Client Secret, and `sp_dc` cookie for Spotify source support

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/poulpy2k/blackflash.git
cd blackflash
```

### 2. Configure environment variables

Create a `.env` file or export the following variables:

```bash
# Required
DISCORD_TOKEN=your-discord-bot-token

# Lavalink (defaults match docker-compose.yml)
LAVALINK_NAME=blackflash
LAVALINK_URI=127.0.0.1:2333
LAVALINK_PASSWORD=youshallnotpass

# Spotify (optional — for Spotify source support)
SPOTIFY_ID=your-spotify-client-id
SPOTIFY_SECRET=your-spotify-client-secret
SPOTIFY_DC=your-spotify-sp-dc-cookie

# Elasticsearch metrics (optional)
ELASTIC_PASSWORD=your-elastic-password
```

### 3. Start the Lavalink node

```bash
docker compose up -d
```

This starts a Lavalink v4 (Alpine) container on `127.0.0.1:2333` with the configuration from `lavalink/application.yml` and pre-downloaded plugins.

### 4. Build and run

```bash
# Build the project
mvn clean install

# Run the bot
java -jar target/blackflash-MANAGE_BY_EXTENSION.jar
```

---

## 🛠️ Development

### Build Commands

```bash
# Full build with tests and coverage
mvn clean install

# Compile only (skip tests — useful during development)
mvn clean compile

# Run tests with coverage report
mvn verify
# Coverage report → target/site/jacoco/index.html

# Package executable JAR
mvn clean package

# SonarQube analysis
mvn clean verify sonar:sonar
```

### Dev Tools

Spring Boot DevTools is included — the application auto-restarts on classpath changes during development.

### Lavalink Plugins

The following Lavalink plugins are pre-downloaded in `lavalink/plugins/`:

| Plugin                                                                                  | Version  | Purpose                              |
|-----------------------------------------------------------------------------------------|----------|--------------------------------------|
| [youtube-plugin](https://github.com/lavalink-devs/youtube-source)                       | `1.18.0` | YouTube audio source (replaces native) |
| [lavasrc-plugin](https://github.com/topi314/LavaSrc)                                   | `4.8.1`  | Spotify, Apple Music, Deezer, etc.   |
| [lavasearch-plugin](https://github.com/topi314/LavaSearch)                              | `1.0.0`  | Enhanced search capabilities         |

### Supported Audio Sources

| Source       | Status                      |
|--------------|-----------------------------|
| YouTube      | ✅ via youtube-plugin        |
| Spotify      | ✅ via LavaSrc plugin        |
| SoundCloud   | ❌ Disabled (configurable)   |
| Bandcamp     | ❌ Disabled (configurable)   |
| Twitch       | ❌ Disabled (configurable)   |
| Vimeo        | ❌ Disabled (configurable)   |
| Nico         | ❌ Disabled (configurable)   |
| HTTP streams | ✅ Native                    |
| Apple Music  | ❌ Disabled (configurable)   |
| Deezer       | ❌ Disabled (configurable)   |

---

### Key Design Decisions

- **Per-guild music managers** — each Discord guild gets its own `GuildMusicManager` with an independent `TrackScheduler` and queue
- **External Lavalink node** — audio processing is offloaded to a dedicated Lavalink v4 server communicating over WebSocket
- **Log4j2** — configured via `spring-boot-starter-log4j2` (not SLF4J + Logback)
- **Virtual threads** — enabled via `spring.threads.virtual.enabled: true` for efficient concurrency

---

## 🧪 Testing

```bash
# Run tests
mvn test

# Run tests with coverage
mvn verify
```

- **JUnit 5** with Spring Boot Test
- **Mockito 5** — with `MockedStatic` and `MockedConstruction` for static/constructor stubbing
- **JaCoCo** — code coverage reports generated at `target/site/jacoco/index.html`
- **SonarQube** — integrated for continuous code quality analysis

### Test Coverage

Tests cover all major components:

- Application bootstrap and timezone configuration
- Discord configuration (JDA initialization, error handling)
- Lavalink configuration (node setup, event subscriptions)
- ObjectMapper configuration (trimming, snake_case, null handling, JSR-310)
- Slash command listener and registry
- Audio loader, guild music manager, track scheduler
- UserData record equality

---

## 📡 Observability

### Actuator Endpoints

Available at `http://localhost:8080/actuator/`:

| Endpoint              | Description                  |
|-----------------------|------------------------------|
| `/actuator/health`    | Health status with details   |
| `/actuator/info`      | Application information      |
| `/actuator/metrics`   | Application metrics          |
| `/actuator/caches`    | Cache statistics             |

### Health Probes

Kubernetes-compatible liveness and readiness probes are enabled:

- `/actuator/health/liveness`
- `/actuator/health/readiness`

### Metrics Export

Metrics are exported to **Elasticsearch** when `ELASTIC_PASSWORD` is configured.

---

## 🔧 Technology Stack

| Component            | Technology                                  |
|----------------------|---------------------------------------------|
| Language             | Java 25                                     |
| Framework            | Spring Boot 4.0.5                           |
| Discord API          | JDA 6.4.1                                   |
| Audio Streaming      | Lavalink Client 3.4.0 + Lavalink v4 Node   |
| E2E Audio Encryption | jdave-api 0.1.8 (DAVE protocol)             |
| Build Tool           | Maven 3.9.14+                               |
| JSON Processing      | Jackson 3.x (`tools.jackson.*`)             |
| Caching              | Caffeine                                    |
| Resilience           | Resilience4j (Spring Cloud Circuit Breaker) |
| Logging              | Log4j2                                      |
| Code Quality         | JaCoCo, SonarQube                           |
| Code Generation      | Lombok, MapStruct 1.6.3                     |
| API Specification    | OpenAPI 3.1.1                               |
| Tracing              | OpenTelemetry                               |
| Containerization     | Docker Compose                              |

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE.md](LICENSE.md) file for details.

## 👤 Author

**Jérémy Laurent** ([@poulpy2k](https://github.com/poulpy2k))

- Website: [jeremy-laurent.com](https://jeremy-laurent.com)
- Email: contact@jeremy-laurent.fr

