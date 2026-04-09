# Changelog

## 0.3

### Added

- **Built-in formatters** in `com.techbulls.commons.securelog.formatter`:
  - `CardNumberFormatter` — masks all but last 4 digits, formats in dash-separated groups of 4
  - `EmailFormatter` — shows first character of local part, masks rest, preserves domain
  - `LastNCharsFormatter` — reveals last N characters (N = `secureValue` length), masks rest with `*`
  - `FirstNCharsFormatter` — reveals first N characters (N = `secureValue` length), masks rest with `*`
- **Convenience annotations** for common masking patterns:
  - `@CardNumber` — zero-config card number masking
  - `@Email` — zero-config email address masking
- **Meta-annotation support** — `@LogSensitive` can now be placed on custom annotations to create reusable masking strategies. The serializer resolves `@LogSensitive` transitively on field annotations.

## 0.2

- Initial public release with `@SecureLog`, `@LogSensitive`, `SecureJson`, custom `ValueFormatter` support, and compile-time `toString()` validation.
