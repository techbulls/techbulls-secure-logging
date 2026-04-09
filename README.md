<!--
LLM-CONTEXT
library: techbulls-secure-logging
language: Java 11+
package: com.techbulls.commons.securelog
maven-group: com.techbulls.commons.securelog
maven-artifact: techbulls-secure-logging
dependency: jackson-databind 2.15+
license: Apache-2.0
annotations: @SecureLog (class-level), @LogSensitive (field-level), @CardNumber (field-level), @Email (field-level)
annotation-processor: SecureLogProcessor (compile-time toString() check)
entry-point: SecureJson.toJson(Object bean)
interface: ValueFormatter { String format(Object value, String secureValue) }
built-in-formatters: CardNumberFormatter, EmailFormatter, LastNCharsFormatter, FirstNCharsFormatter
default-mask: "XXXX"
meta-annotation: @LogSensitive can be placed on custom annotations for reusable masking strategies
purpose: Mask sensitive fields during JSON serialization for safe logging
-->

# TechBulls Secure Logging

[![CI](https://github.com/techbulls/techbulls-secure-logging/actions/workflows/ci.yml/badge.svg)](https://github.com/techbulls/techbulls-secure-logging/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/techbulls/techbulls-secure-logging/graph/badge.svg)](https://codecov.io/gh/techbulls/techbulls-secure-logging)

A Java library that masks sensitive field values during JSON serialization for safe logging. Uses an annotation-based approach with Jackson integration to prevent sensitive data (passwords, emails, tokens) from appearing in plain text in log files.

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Annotations](#annotations)
  - [@SecureLog](#securelog)
  - [@LogSensitive](#logsensitive)
  - [@CardNumber](#cardnumber)
  - [@Email](#email)
- [Built-in Formatters](#built-in-formatters)
- [API Reference](#api-reference)
- [Custom ValueFormatter](#custom-valueformatter)
- [Custom Meta-Annotations](#custom-meta-annotations)
- [Examples](#examples)
  - [Basic Masking](#basic-masking)
  - [Custom Formatters](#custom-formatters)
  - [Securing Null Values](#securing-null-values)
  - [Collections and Maps](#collections-and-maps)
  - [Nested Objects](#nested-objects)
  - [Pretty Printing](#pretty-printing)
- [Jackson Integration](#jackson-integration)
- [Custom ObjectMapper](#custom-objectmapper)
- [Implementing toString](#implementing-tostring)
- [Compile-Time Validation](#compile-time-validation)
- [Thread Safety](#thread-safety)
- [Requirements](#requirements)

## Installation

### Maven

```xml
<dependency>
    <groupId>com.techbulls.commons.securelog</groupId>
    <artifactId>techbulls-secure-logging</artifactId>
    <version>0.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.techbulls.commons.securelog:techbulls-secure-logging:0.1'
```

## Quick Start

**Without** secure logging:
```
Payment: {"cardNumber": "4111-1111-1111-1234", "cvv": "737", "amount": 49.99, "merchant": "Acme Corp"}
```

**With** secure logging:
```
Payment: {"cardNumber": "XXXX-XXXX-XXXX-1234", "cvv": "XXXX", "amount": 49.99, "merchant": "Acme Corp"}
```

Three steps to get started:

```java
import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.annotation.CardNumber;
import com.techbulls.commons.securelog.serialization.SecureJson;

// 1. Annotate the class with @SecureLog
@SecureLog
public class PaymentRequest {

    // 2. Annotate sensitive fields — use built-in annotations or @LogSensitive
    @CardNumber
    private String cardNumber;

    @LogSensitive
    private String cvv;

    private double amount;

    private String merchant;

    // getters and setters...

    // 3. Override toString to delegate to SecureJson
    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

Now any logging of this object masks sensitive fields automatically:

```java
PaymentRequest payment = new PaymentRequest();
payment.setCardNumber("4111-1111-1111-1234");
payment.setCvv("737");
payment.setAmount(49.99);
payment.setMerchant("Acme Corp");

log.info("Payment: {}", payment);
// Output: Payment: {"cardNumber":"XXXX-XXXX-XXXX-1234","cvv":"XXXX","amount":49.99,"merchant":"Acme Corp"}
```

## Annotations

### @SecureLog

Class-level annotation that configures secure JSON serialization for the annotated class.

```java
@SecureLog
@SecureLog(pretty = true)
@SecureLog(view = MyView.class)
```

| Attribute | Type       | Default              | Description                                                  |
|-----------|------------|----------------------|--------------------------------------------------------------|
| `pretty`  | `boolean`  | `false`              | Enable pretty-printed (indented) JSON output                 |
| `view`    | `Class<?>` | `SecureLog.Default.class` | Jackson `@JsonView` class to apply during serialization |

### @LogSensitive

Field-level annotation that marks a field as sensitive. The field value will be masked in the secure JSON output.

```java
@LogSensitive                                          // masks with "XXXX"
@LogSensitive(value = "***")                           // masks with "***"
@LogSensitive(formatter = EmailFormatter.class)        // custom masking logic
@LogSensitive(secureNullValues = true)                 // masks null values too
```

| Attribute          | Type                              | Default                       | Description                                                        |
|--------------------|-----------------------------------|-------------------------------|--------------------------------------------------------------------|
| `value`            | `String`                          | `"XXXX"`                      | The mask string used to replace the field value                    |
| `formatter`        | `Class<? extends ValueFormatter>` | `DefaultValueFormatter.class` | Custom formatter class for advanced masking logic                  |
| `secureNullValues` | `boolean`                         | `false`                       | If `true`, null values are masked with the `value` string instead of appearing as `null` |

### @CardNumber

Field-level convenience annotation that masks card numbers, revealing only the last 4 digits in dash-separated groups of 4. This is a meta-annotation over `@LogSensitive` with `CardNumberFormatter` pre-configured.

```java
@CardNumber
private String cardNumber;
// "4111111111111111" → "XXXX-XXXX-XXXX-1111"
// "4111-1111-1111-1111" → "XXXX-XXXX-XXXX-1111"
```

### @Email

Field-level convenience annotation that masks email addresses, revealing only the first character of the local part and the full domain. This is a meta-annotation over `@LogSensitive` with `EmailFormatter` pre-configured.

```java
@Email
private String emailAddress;
// "john.doe@gmail.com" → "j****@gmail.com"
```

## Built-in Formatters

The `com.techbulls.commons.securelog.formatter` package provides ready-to-use `ValueFormatter` implementations for common masking patterns. Use them directly via `@LogSensitive(formatter = ...)` or through the convenience annotations above.

| Formatter | Masking Strategy | Example |
|-----------|-----------------|---------|
| `CardNumberFormatter` | Shows last 4 digits, masks rest with `X`, dash-separated groups of 4 | `"4111111111111111"` → `"XXXX-XXXX-XXXX-1111"` |
| `EmailFormatter` | Shows first character of local part + `****` + full domain | `"john.doe@gmail.com"` → `"j****@gmail.com"` |
| `LastNCharsFormatter` | Shows last N characters, masks rest with `*` (N = `secureValue` length) | `"123456789"` with `"XXXX"` → `"*****6789"` |
| `FirstNCharsFormatter` | Shows first N characters, masks rest with `*` (N = `secureValue` length) | `"123456789"` with `"XXXX"` → `"1234*****"` |

**LastNCharsFormatter / FirstNCharsFormatter convention:** The number of characters to reveal is determined by the length of the `value` attribute in `@LogSensitive`. For example, `"XXXX"` (4 characters) reveals 4 characters:

```java
@LogSensitive(value = "XXXX", formatter = LastNCharsFormatter.class)
private String accountNumber;
// "9876543210" → "******3210" (last 4 revealed)

@LogSensitive(value = "XX", formatter = FirstNCharsFormatter.class)
private String memberId;
// "ABCDEF" → "AB****" (first 2 revealed)
```

If the input value is `null` or shorter than N characters, the `secureValue` string is returned as a fallback.

## API Reference

The `SecureJson` class is the public entry point. All methods are static and thread-safe.

### `SecureJson.toJson(Object bean)`

Converts the object to a secure JSON string. Reads `@SecureLog` annotation from the class to determine `pretty` and `view` settings.

```java
String json = SecureJson.toJson(myObject);
```

### `SecureJson.toJson(Object bean, boolean prettyPrint, Class<?> view)`

Converts the object with explicit pretty-print and view settings. These override the `@SecureLog` annotation values.

```java
// Pretty print, no view filtering
String json = SecureJson.toJson(myObject, true, null);

// With a specific JsonView
String json = SecureJson.toJson(myObject, false, PublicView.class);
```

### `SecureJson.toJson(ObjectMapper mapper, Object bean, boolean prettyPrint, Class<?> view)`

Uses a custom `ObjectMapper` instance. The library adds its secure serializer modifier to the mapper on first use. Useful when you need custom Jackson configuration (e.g., `@JsonFilter`).

```java
ObjectMapper customMapper = new ObjectMapper();
// configure customMapper as needed...
String json = SecureJson.toJson(customMapper, myObject, false, null);
```

## Custom ValueFormatter

Implement the `ValueFormatter` interface to define custom masking logic. The formatter must have a no-arg constructor.

```java
import com.techbulls.commons.securelog.ValueFormatter;

public interface ValueFormatter {
    /**
     * @param value       the actual field value
     * @param secureValue the mask string from @LogSensitive.value()
     * @return the masked string to use in the JSON output
     */
    String format(Object value, String secureValue);
}
```

**Default behavior:** The built-in `DefaultValueFormatter` simply returns `secureValue`, ignoring the actual value entirely.

### Example: Email Formatter

> **Note:** A built-in `EmailFormatter` is available in `com.techbulls.commons.securelog.formatter` — or use the `@Email` convenience annotation. The example below shows a custom alternative.

```java
public class CustomEmailFormatter implements ValueFormatter {
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) return secureValue;
        return value.toString().replaceAll("[A-Za-z0-9_\\-\\.]+@", "xxxxxx@");
    }
}

// Usage:
@LogSensitive(formatter = CustomEmailFormatter.class)
private String email;

// "john.doe@gmail.com" → "xxxxxx@gmail.com"
```

### Example: Mobile Formatter

Shows only the last 2 digits of a phone number:

```java
public class MobileFormatter implements ValueFormatter {
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) return secureValue;
        String mobile = value.toString();
        if (mobile.length() <= 2) {
            return mobile;
        }
        return "XXXXXXX" + mobile.substring(mobile.length() - 2);
    }
}

// Usage:
@LogSensitive(formatter = MobileFormatter.class)
private String mobile;

// "9876543210" → "XXXXXXX10"
```

## Custom Meta-Annotations

You can create your own convenience annotations by placing `@LogSensitive` on your annotation definition. The library resolves `@LogSensitive` as a meta-annotation automatically — no additional configuration is needed.

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@LogSensitive(formatter = MyPanFormatter.class)
public @interface PanNumber {
}
```

```java
@SecureLog
public class TaxRecord {

    @PanNumber
    private String pan;  // uses MyPanFormatter automatically

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

**Resolution order:** If a field has both a direct `@LogSensitive` annotation and a meta-annotated annotation, the direct `@LogSensitive` takes precedence.

## Examples

### Basic Masking

```java
import com.techbulls.commons.securelog.annotation.SecureLog;
import com.techbulls.commons.securelog.annotation.LogSensitive;
import com.techbulls.commons.securelog.serialization.SecureJson;

@SecureLog
public class UserInfo {

    @LogSensitive(value = "XXXXX XXXXX")
    private String name;

    @LogSensitive(formatter = EmailFormatter.class)
    private String email;

    @LogSensitive(formatter = MobileFormatter.class)
    private String mobile;

    @JsonProperty("member_id")
    private String memberId;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

```java
UserInfo user = new UserInfo();
user.setName("John Doe");
user.setEmail("john.doe@gmail.com");
user.setMobile("9876543210");
user.setMemberId("100001");

System.out.println("User: " + user);
// Output: User: {"name":"XXXXX XXXXX","email":"xxxxxx@gmail.com","mobile":"XXXXXXX10","member_id":"100001"}
```

### Custom Formatters

You can build any masking strategy by implementing `ValueFormatter`:

```java
// Mask all uppercase letters
public class UpperCaseFormatter implements ValueFormatter {
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) return secureValue;
        return value.toString().replaceAll("[A-Z]", "X");
    }
}

// Show first character, mask the rest
public class FirstCharFormatter implements ValueFormatter {
    @Override
    public String format(Object value, String secureValue) {
        if (value == null) return secureValue;
        String str = value.toString();
        if (str.length() <= 1) return str;
        return str.charAt(0) + "XXXX";
    }
}
```

### Securing Null Values

By default, null fields are serialized as `null` in JSON even if annotated with `@LogSensitive`. Use `secureNullValues = true` to mask them:

```java
@SecureLog
public class Payment {

    @LogSensitive(secureNullValues = true)
    private String cardNumber;

    @LogSensitive
    private String cvv;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

```java
Payment payment = new Payment();
// both fields are null

System.out.println(payment);
// Output: {"cardNumber":"XXXX","cvv":null}
//          ^^ masked null       ^^ null preserved (secureNullValues = false)
```

**Note:** When `secureNullValues = true`, the `value` parameter passed to `ValueFormatter.format()` will be `null`. Custom formatters used with `secureNullValues` must handle null values — for example, `if (value == null) return secureValue;`.

### Collections and Maps

Fields typed as `List`, `Set`, `Queue`, or `Map` can be annotated with `@LogSensitive`. The entire collection is replaced with the mask string:

```java
@SecureLog
public class UserProfile {

    @LogSensitive(value = "####")
    private List<String> phoneNumbers;

    @LogSensitive(value = "####")
    private Map<String, String> secrets;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

```java
UserProfile profile = new UserProfile();
profile.setPhoneNumbers(Arrays.asList("111-222-3333", "444-555-6666"));
profile.setSecrets(Map.of("ssn", "123-45-6789"));

System.out.println(profile);
// Output: {"phoneNumbers":"####","secrets":"####"}
```

### Nested Objects

Secure logging works with nested object hierarchies. Inner objects with their own `@SecureLog` and `@LogSensitive` annotations are handled correctly:

```java
@SecureLog
public class Order {

    private String orderId;

    @LogSensitive
    private String promoCode;

    private PaymentInfo payment;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}

@SecureLog
public class PaymentInfo {

    @LogSensitive
    private String cardNumber;

    private String currency;

    // getters and setters...
}
```

```java
Order order = new Order();
order.setOrderId("ORD-123");
order.setPromoCode("SAVE50");

PaymentInfo payment = new PaymentInfo();
payment.setCardNumber("4111-1111-1111-1111");
payment.setCurrency("USD");
order.setPayment(payment);

System.out.println(order);
// Output: {"orderId":"ORD-123","promoCode":"XXXX","payment":{"cardNumber":"XXXX","currency":"USD"}}
```

### Pretty Printing

Enable indented JSON output using the `@SecureLog` annotation or the `toJson` method parameter:

```java
// Via annotation
@SecureLog(pretty = true)
public class Config {
    @LogSensitive
    private String apiKey;
    private String endpoint;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}

// Or via method parameter (overrides annotation)
String json = SecureJson.toJson(myObject, true, null);
```

Output:
```json
{
  "apiKey" : "XXXX",
  "endpoint" : "https://api.example.com"
}
```

## Jackson Integration

Since this library uses Jackson for JSON serialization, all standard Jackson annotations are supported:

| Annotation         | Effect                                                    |
|--------------------|-----------------------------------------------------------|
| `@JsonProperty`    | Rename a field in the JSON output                         |
| `@JsonIgnore`      | Exclude a field from the JSON output entirely             |
| `@JsonView`        | Control which fields appear based on a view class         |
| `@JsonFilter`      | Apply dynamic property filtering via `SimpleBeanPropertyFilter` |
| `@JsonAutoDetect`  | Control field visibility (e.g., include/exclude private fields) |

### @JsonView Example

```java
public class Views {
    public static class Public {}
    public static class Internal extends Public {}
}

@SecureLog(view = Views.Public.class)
public class User {

    @JsonView(Views.Public.class)
    private String username;

    @JsonView(Views.Internal.class)
    @LogSensitive
    private String ssn;

    // getters and setters...

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

```java
// With Public view: ssn is excluded entirely (not part of the view)
System.out.println(user);
// Output: {"username":"johndoe"}

// With Internal view: ssn is included but masked
String json = SecureJson.toJson(user, false, Views.Internal.class);
// Output: {"username":"johndoe","ssn":"XXXX"}
```

### @JsonFilter Example

```java
@JsonFilter("myFilter")
@SecureLog
public class FilteredUser {

    private String name;

    @LogSensitive
    private String email;

    private String role;

    // getters and setters...
}
```

```java
ObjectMapper mapper = new ObjectMapper();
SimpleFilterProvider filters = new SimpleFilterProvider();
filters.addFilter("myFilter",
    SimpleBeanPropertyFilter.filterOutAllExcept("name", "email"));
mapper.setFilterProvider(filters);

String json = SecureJson.toJson(mapper, user, false, null);
// Output: {"name":"John","email":"XXXX"}
// "role" is filtered out, "email" is masked
```

### @JsonAutoDetect Example

```java
@SecureLog
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InternalData {

    @LogSensitive
    private String secret;  // included because visibility = ANY

    private String label;

    // no getters needed — fields are accessed directly
}
```

## Custom ObjectMapper

The `SecureJson.toJson(ObjectMapper, Object, boolean, Class<?>)` overload accepts a custom `ObjectMapper`. The library adds its secure serializer modifier to the mapper on first use and tracks it to avoid re-initialization:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
// add any custom Jackson configuration...

String json = SecureJson.toJson(mapper, myObject, false, null);
```

**Note:** When using any `toJson` overload other than `toJson(Object)`, the `prettyPrint` and `view` parameters override the values from the `@SecureLog` annotation.

## Implementing toString

We encourage you to implement a `toString` method on your model class that delegates to `SecureJson.toJson(this)`. This ensures that any accidental logging will produce masked output by default, without requiring developers to explicitly call the utility method.

```java
@SecureLog
public class MyModel {

    @LogSensitive
    private String sensitiveField;

    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

### Integration with Lombok

The annotations work alongside Lombok, but you **cannot** rely on Lombok's `@ToString` for masked output — Lombok generates its own format, not JSON, and does not invoke `SecureJson`.

If you use `@Data`, you **must** provide your own `toString()` to override Lombok's generated one:

```java
@Data
@SecureLog
public class MyModel {

    @LogSensitive
    private String password;

    private String username;

    // Override Lombok's generated toString
    @Override
    public String toString() {
        return SecureJson.toJson(this);
    }
}
```

Do **not** use `@ToString` from Lombok on any class that requires masked output.

## Compile-Time Validation

The library includes a built-in annotation processor that checks at compile time whether classes annotated with `@SecureLog` have a directly declared `toString()` method. This helps prevent accidental logging of sensitive data in plain text.

### How It Works

When your project includes this library on the annotation processor path, the processor automatically runs during compilation. If a concrete class is annotated with `@SecureLog` but does not override `toString()`, a **compiler warning** is emitted:

```
warning: Class annotated with @SecureLog does not override toString(). Add: @Override public String toString() { return SecureJson.toJson(this); }
```

### Strict Mode

To upgrade the warning to a **compile error** (recommended for CI pipelines), pass the compiler option `-Atechbulls.securelog.strict=true`:

**Maven:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <compilerArgs>
            <arg>-Atechbulls.securelog.strict=true</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

**Gradle:**
```groovy
compileJava {
    options.compilerArgs += ['-Atechbulls.securelog.strict=true']
}
```

### Suppressing the Warning

If you intentionally omit `toString()` on a specific class, suppress the warning with:

```java
@SecureLog
@SuppressWarnings("techbulls.securelog")
public class MySpecialCase {
    // ...
}
```

### Notes

- **Abstract classes and interfaces** are skipped — only concrete classes are checked.
- **Inherited `toString()`** does not satisfy the check. The method must be declared directly in the annotated class, since an inherited `toString()` is unlikely to call `SecureJson.toJson(this)`.
- **Lombok's `@ToString`** generates a method that does not call `SecureJson.toJson()`, so the processor will still warn. This is intentional — you should provide your own `toString()` override.

## Thread Safety

`SecureJson` is thread-safe. The internal `ObjectMapper` is initialized using a double-checked locking pattern, and custom `ObjectMapper` instances are tracked in a `Set` to avoid duplicate initialization. Safe to call `SecureJson.toJson()` concurrently from multiple threads.

## Requirements

- **Java:** 11 or higher
- **Jackson:** jackson-databind 2.15.0 or compatible
- **License:** Apache License 2.0
