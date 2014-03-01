# Changelog

## 0.1.0 (2026-03-17)

- Add `mask()`, `maskEmail()`, `maskPhone()`, and `maskCreditCard()` top-level functions
- Add `@Masked` annotation with configurable strategy and keep count
- Add `maskedToString()` reflection-based extension for data classes
- Add `MaskStrategy` enum with PARTIAL, FULL, EMAIL, CREDIT_CARD, and PHONE strategies
