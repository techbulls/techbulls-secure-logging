# Changelog

## 0.3

### Added

- **Built-in formatters** in `com.techbulls.commons.securelog.formatter`:
  - `CardNumberFormatter` — masks all but last 4 digits, formats in dash-separated groups of 4
  - `EmailFormatter` — shows first character of local part, masks rest, preserves domain
  - `AadhaarFormatter` — masks Aadhaar number, revealing only last 4 digits in `XXXX-XXXX-1234` format
  - `MobileNumberFormatter` — masks mobile number, revealing only last 4 digits, handles country codes
  - `PanFormatter` — masks PAN number, revealing only last 4 characters in `XXXXXX234F` format
  - `LastNCharsFormatter` — reveals last N characters (N = `secureValue` length), masks rest with `*`
  - `FirstNCharsFormatter` — reveals first N characters (N = `secureValue` length), masks rest with `*`
- **Convenience annotations** for common masking patterns:
  - `@CardNumber` — zero-config card number masking
  - `@Email` — zero-config email address masking
  - `@Aadhaar` — zero-config Aadhaar number masking
  - `@MobileNumber` — zero-config mobile number masking
  - `@PanNumber` — zero-config PAN number masking
- **Meta-annotation support** — `@LogSensitive` can now be placed on custom annotations to create reusable masking strategies. The serializer resolves `@LogSensitive` transitively on field annotations.

## 0.2

- Initial public release with `@SecureLog`, `@LogSensitive`, `SecureJson`, custom `ValueFormatter` support, and compile-time `toString()` validation.
