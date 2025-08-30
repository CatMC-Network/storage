# Storage Library

A simple Java library for interacting with MongoDB and Redis, using an annotation-driven approach for ease of use.

## Features

### MongoDB
- Annotation-driven mapping for POJOs (`@Document`, `@Id`, `@Field`, `@Transient`, `@Indexed`).
- Automatic handling of POJO serialization and deserialization.
- A simple `Mongo` class for managing the connection and accessing collections.
- Automatic index creation based on the `@Indexed` annotation.

### Redis
- Annotation-driven publish/subscribe messaging (`@RedisListener`, `@RedisSender`).
- Typed packet system for sending and receiving Java objects.
- A simple `Redis` class for managing the connection, publishing messages, and registering listeners.

## Installation

This project is available on GitHub Packages. To use it in your Maven project, add the following to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/CatMC-Network</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>club.catmc.utils.storage</groupId>
        <artifactId>storage</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Usage

See the [wiki](https://github.com/CatMC-Network/storage/wiki) for detailed documentation and usage examples.