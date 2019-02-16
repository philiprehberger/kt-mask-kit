# Changelog

## 0.2.4 (2026-03-22)

- Fix README installation section header, standardize CHANGELOG

## 0.2.3 (2026-03-20)

- Standardize README: fix title, badges, version sync, remove Requirements section

## 0.2.2 (2026-03-18)

- Add badges and Development section to README

## 0.2.1 (2026-03-18)

- Upgrade to Kotlin 2.0.21 and Gradle 8.12
- Enable explicitApi() for stricter public API surface
- Add issueManagement to POM metadata

## 0.2.0 (2026-03-18)

- Add `MaskOptions` configuration class with configurable mask character (default `*`)
- Add `MaskStrategy` sealed interface with `Full`, `Partial(visibleStart, visibleEnd)`, and `Regex(pattern, replacement)` implementations
- Add `applyStrategy()` function for programmatic masking with `MaskStrategy`
- Add `maskSsn()` function for Social Security Number masking (`***-**-NNNN` format)
- Add `maskIban()` function for IBAN masking (preserves country code and last 4 characters)
- Add `maskMap()` for batch masking of specific fields in a map
- Add `maskFields()` for batch masking of specific fields in any object using reflection
- Add `SSN` and `IBAN` variants to annotation-based masking via `MaskType`
- Add `maskChar` parameter to `@Masked` annotation for per-field custom mask character
- Rename `MaskStrategy` enum to `MaskType` (used in annotations)
- All existing masking functions now accept optional `MaskOptions` parameter

## 0.1.0 (2026-03-17)

- Add `mask()`, `maskEmail()`, `maskPhone()`, and `maskCreditCard()` top-level functions
- Add `@Masked` annotation with configurable strategy and keep count
- Add `maskedToString()` reflection-based extension for data classes
- Add `MaskStrategy` enum with PARTIAL, FULL, EMAIL, CREDIT_CARD, and PHONE strategies
