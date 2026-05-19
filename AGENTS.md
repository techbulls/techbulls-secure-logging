# AGENTS.md - techbulls-secure-logging

## Overview

Java library that masks sensitive field values during JSON serialization for safe logging. Uses annotation-based approach with Jackson integration.

**Version:** 0.3
**Package:** `com.techbulls.commons.securelog`
**Java:** 11+

## Architecture

```
User object → SecureJson.toJson()
                  ↓
         SecureLogBeanSerializerModifier
         (Jackson BeanSerializerModifier)
                  ↓
         SecurePropertySerializer wraps delegate
         (intercepts serialize() calls)
                  ↓
         ValueFormatter.format(value, secureValue)
                  ↓
         Masked JSON output
```

## Project Structure

```
src/main/java/com/techbulls/commons/securelog/
├── annotation/
│   ├── SecureLog.java           # Class-level: pretty print, JsonView
│   ├── LogSensitive.java        # Field-level: mask value, formatter, secureNullValues
│   ├── CardNumber.java          # Meta-annotation: CardNumberFormatter pre-configured
│   ├── Email.java               # Meta-annotation: EmailFormatter pre-configured
│   ├── Aadhaar.java            # Meta-annotation: AadhaarFormatter pre-configured
│   ├── MobileNumber.java       # Meta-annotation: MobileNumberFormatter pre-configured
│   └── PanNumber.java          # Meta-annotation: PanFormatter pre-configured
├── formatter/
│   ├── CardNumberFormatter.java      # Shows last 4 digits, groups of 4
│   ├── EmailFormatter.java           # Shows first char + **** + domain
│   ├── AadhaarFormatter.java        # Shows last 4 digits in XXXX-XXXX-1234
│   ├── MobileNumberFormatter.java   # Shows last 4 digits, handles country codes
│   ├── PanFormatter.java           # Shows last 4 chars in XXXXXX234F
│   ├── LastNCharsFormatter.java    # Reveals last N chars (N = secureValue length)
│   └── FirstNCharsFormatter.java   # Reveals first N chars (N = secureValue length)
├── serialization/
│   ├── SecureJson.java                      # Public API entry point (static methods)
│   ├── SecureLogBeanSerializerModifier.java # Detects @LogSensitive, wraps serializers
│   ├── SecurePropertySerializer.java        # Delegates + applies masking
│   └── NullSecurePropertySerializer.java    # Handles null value masking
├── ValueFormatter.java          # Interface: format(Object value, String secureValue)
├── DefaultValueFormatter.java  # Returns secureValue unchanged
└── processor/
    └── SecureLogProcessor.java  # Compile-time toString() validation
```

## Annotations

| Annotation | Type | Purpose |
|------------|------|---------|
| `@SecureLog` | Class | Configures secure serialization (pretty, view) |
| `@LogSensitive` | Field | Marks field as sensitive, configures masking |
| `@CardNumber` | Field | Convenience: card number masking |
| `@Email` | Field | Convenience: email masking |
| `@Aadhaar` | Field | Convenience: Aadhaar number masking |
| `@MobileNumber` | Field | Convenience: mobile number masking |
| `@PanNumber` | Field | Convenience: PAN number masking |

## Convenience Annotation Examples

```java
@SecureLog
public class KycDetails {
    @Aadhaar
    private String aadhaarNumber;    // "234567891234" → "XXXX-XXXX-1234"

    @MobileNumber
    private String mobile;           // "+919876543210" → "********3210"

    @PanNumber
    private String pan;              // "ABCDE1234F" → "XXXXXX234F"
}
```

## Key Design Decisions

- **Thread-safe:** `SecureJson` uses double-checked locking for static ObjectMapper
- **Meta-annotation support:** `@LogSensitive` on an annotation makes it a meta-annotation; serializer resolves transitively
- **Custom formatters:** Implement `ValueFormatter` with no-arg constructor (instantiated reflectively)
- **Null handling:** `secureNullValues = true` uses `NullSecurePropertySerializer` for null masking
- **Custom ObjectMapper tracking:** `MAPPERS_ALREADY_INITIALIZED` Set prevents duplicate initialization

## Build and Test

```bash
mvn clean package    # Build
mvn test             # Run tests (JUnit 4)
```

**Test location:** `src/test/java/com/techbulls/commons/securelog/`

**Key test classes:**
- `SecureJsonTest` - basic masking, custom formatters
- `SecureFilterTest` - Jackson @JsonFilter integration
- `SecureNestedClassTest` - nested object hierarchies
- `SecureCollectionMapTest` - collections and maps
- `SecureVisibilityTest` - field visibility settings
- `SerializeWithViewTest` - @JsonView integration
- `SecureJsonConcurrencyTest` - thread safety (30 threads x 1000 iterations)
- `AadhaarFormatterTest`, `MobileFormatterTest`, `PanFormatterTest` - formatter-specific tests

## Release

Uses maven-release-plugin with GPG signing, javadoc, and sources attachment.

```bash
mvn release:prepare release:perform
```

## Dependencies

- jackson-databind 2.15.0
- junit 4.13.1 (test)
- lombok 1.18.22 (test)