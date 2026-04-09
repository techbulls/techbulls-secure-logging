# CLAUDE.md - techbulls-secure-logging

## What is this project?

A Java library that masks sensitive field values during JSON serialization for safe logging. Uses annotation-based approach with Jackson integration.

## How it works

1. Annotate class with `@SecureLog` (class-level)
2. Annotate sensitive fields with `@LogSensitive` (field-level, default mask: "XXXX")
3. Override `toString()` to delegate to `SecureJson.toJson(this)`
4. Serialization pipeline: `SecureJson` → `SecureLogBeanSerializerModifier` detects `@LogSensitive` → wraps serializer with `SecurePropertySerializer` → applies `ValueFormatter`

## Project structure

```
src/main/java/com/techbulls/commons/securelog/
├── annotation/
│   ├── SecureLog.java          # Class-level annotation (pretty print, JsonView)
│   └── LogSensitive.java       # Field-level annotation (mask value, formatter, null handling)
├── serialization/
│   ├── SecureJson.java                      # Public API entry point
│   ├── SecureLogBeanSerializerModifier.java # Jackson modifier that detects annotations
│   ├── SecurePropertySerializer.java        # Wraps delegate serializer with masking
│   └── NullSecurePropertySerializer.java    # Handles null value masking
├── ValueFormatter.java          # Interface: format(Object value, String secureValue)
└── DefaultValueFormatter.java   # Returns secureValue unchanged
```

## Build and test

```bash
mvn clean package    # Build
mvn test             # Run tests (JUnit 4)
```

- Java 1.8 source/target
- Dependencies: jackson-databind 2.15.0, junit 4.13.1 (test), lombok 1.18.22 (test)
- Published to Maven Central via Sonatype OSS

## Key design decisions

- Thread-safe: `SecureJson` uses double-checked locking for static ObjectMapper
- Supports custom ObjectMapper (e.g., with `@JsonFilter`), tracked in `MAPPERS_ALREADY_INITIALIZED` set
- Compatible with `@JsonView`, `@JsonFilter`, `@JsonAutoDetect`
- Custom formatters implement `ValueFormatter` interface with no-arg constructor (instantiated reflectively)

## Test coverage

Tests in `src/test/java/.../serialization/`:
- `SecureJsonTest` - basic masking, custom formatters
- `SecureFilterTest` - Jackson @JsonFilter integration
- `SecureNestedClassTest` - nested object hierarchies
- `SecureCollectionMapTest` - collections and maps
- `SecureVisibilityTest` - field visibility settings
- `SerializeWithViewTest` - @JsonView integration
- `SecureJsonConcurrencyTest` - thread safety (30 threads x 1000 iterations)

## Release

Uses maven-release-plugin. Release profile adds javadoc, sources, and GPG signing.

```bash
mvn release:prepare release:perform
```
