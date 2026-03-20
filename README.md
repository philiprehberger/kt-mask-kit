# mask-kit

[![Tests](https://github.com/philiprehberger/kt-mask-kit/actions/workflows/publish.yml/badge.svg)](https://github.com/philiprehberger/kt-mask-kit/actions/workflows/publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.philiprehberger/mask-kit.svg)](https://central.sonatype.com/artifact/com.philiprehberger/mask-kit)
[![License](https://img.shields.io/github/license/philiprehberger/kt-mask-kit)](LICENSE)

Data masking for sensitive values in logs and API responses.

## Installation

### Gradle Kotlin DSL

```kotlin
implementation("com.philiprehberger:mask-kit:0.2.3")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>mask-kit</artifactId>
    <version>0.2.3</version>
</dependency>
```

## Usage

```kotlin
import com.philiprehberger.maskkit.*

// Top-level masking functions
mask("1234567890")               // "******7890"
mask("1234567890", keep = 2)     // "********90"
maskEmail("john@example.com")    // "j***@example.com"
maskPhone("+1-555-123-4567")     // "***********4567"
maskCreditCard("4111111111111111") // "****-****-****-1111"
maskSsn("123-45-6789")          // "***-**-6789"
maskIban("DE89370400440532016789") // "DE**************6789"
```

### Custom Mask Character

Pass a `MaskOptions` instance to any masking function to change the mask character:

```kotlin
val opts = MaskOptions(maskChar = '#')

mask("1234567890", options = opts)       // "######7890"
maskEmail("john@example.com", opts)      // "j###@example.com"
maskCreditCard("4111111111111111", opts)  // "####-####-####-1111"
maskSsn("123-45-6789", opts)             // "###-##-6789"
```

### MaskStrategy (Sealed Interface)

Use `MaskStrategy` for flexible, programmatic masking:

```kotlin
// Full masking
applyStrategy("secret", MaskStrategy.Full)
// "******"

// Partial masking with visible start and end
applyStrategy("1234567890", MaskStrategy.Partial(visibleStart = 2, visibleEnd = 4))
// "12****7890"

// Regex-based masking
applyStrategy("123-45-6789", MaskStrategy.Regex("(\\d{3})-(\\d{2})-(\\d{4})", "***-**-$3"))
// "***-**-6789"
```

### Batch Masking

Mask specific fields in a map:

```kotlin
val data = mapOf("name" to "John", "email" to "john@example.com", "age" to 30)
val fieldMasks = mapOf(
    "email" to MaskStrategy.Full,
    "name" to MaskStrategy.Partial(visibleStart = 1, visibleEnd = 0),
)
val result = maskMap(data, fieldMasks)
// {name=J***, email=****************, age=30}
```

Mask specific fields in any object using reflection:

```kotlin
data class Person(val name: String, val email: String, val age: Int)

val person = Person("John", "john@example.com", 30)
val result = maskFields(person, setOf("email", "name"))
// {name=****, email=****************, age=30}
```

### Annotation-Based Masking

```kotlin
data class User(
    val name: String,
    @Masked(strategy = MaskType.EMAIL)
    val email: String,
    @Masked(strategy = MaskType.SSN)
    val ssn: String,
    @Masked(strategy = MaskType.IBAN)
    val iban: String,
    @Masked(strategy = MaskType.FULL, maskChar = '#')
    val secret: String,
)

val user = User("John", "john@example.com", "123-45-6789", "DE89370400440532016789", "topsecret")
println(user.maskedToString())
// User(name=John, email=j***@example.com, ssn=***-**-6789, iban=DE**************6789, secret=#########)
```

## API

| Function / Type | Description |
|---|---|
| `mask(value, keep, options)` | Masks all but the last `keep` characters |
| `maskEmail(email, options)` | Masks email local part, preserving first character and domain |
| `maskPhone(phone, options)` | Masks phone number, showing last 4 digits |
| `maskCreditCard(card, options)` | Masks credit card, showing last 4 digits in grouped format |
| `maskSsn(ssn, options)` | Masks SSN, showing last 4 digits in `***-**-NNNN` format |
| `maskIban(iban, options)` | Masks IBAN, preserving country code and last 4 characters |
| `maskMap(map, fieldMasks, options)` | Masks specified fields in a map using given strategies |
| `maskFields(obj, fieldNames, options)` | Masks specified fields in any object via reflection |
| `applyStrategy(value, strategy, options)` | Applies a `MaskStrategy` to a value |
| `MaskOptions(maskChar)` | Configuration for mask character (default `*`) |
| `MaskStrategy` | Sealed interface: `Full`, `Partial(visibleStart, visibleEnd)`, `Regex(pattern, replacement)` |
| `MaskType` | Enum for annotations: `PARTIAL`, `FULL`, `EMAIL`, `CREDIT_CARD`, `PHONE`, `SSN`, `IBAN` |
| `@Masked(strategy, keep, maskChar)` | Property annotation for automatic masking |
| `T.maskedToString()` | Extension producing toString() with `@Masked` properties masked |

## Development

```bash
./gradlew build
./gradlew test
```

## License

MIT
