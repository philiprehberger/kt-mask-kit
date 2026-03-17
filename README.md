# mask-kit

Data masking for sensitive values in logs and API responses.

## Installation

### Gradle Kotlin DSL

```kotlin
implementation("com.philiprehberger:mask-kit:0.1.0")
```

### Maven

```xml
<dependency>
    <groupId>com.philiprehberger</groupId>
    <artifactId>mask-kit</artifactId>
    <version>0.1.0</version>
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

// Annotation-based masking on data classes
data class User(
    val name: String,
    @Masked(strategy = MaskStrategy.EMAIL)
    val email: String,
    @Masked(strategy = MaskStrategy.PARTIAL, keep = 4)
    val ssn: String,
)

val user = User("John", "john@example.com", "123-45-6789")
println(user.maskedToString())
// User(name=John, email=j***@example.com, ssn=*******6789)
```

## API

| Function / Annotation | Description |
|---|---|
| `mask(value, keep)` | Masks all but the last `keep` characters |
| `maskEmail(email)` | Masks email local part, preserving first character and domain |
| `maskPhone(phone)` | Masks phone number, showing last 4 digits |
| `maskCreditCard(card)` | Masks credit card, showing last 4 digits in grouped format |
| `@Masked(strategy, keep)` | Property annotation for automatic masking |
| `T.maskedToString()` | Extension producing toString() with `@Masked` properties masked |
| `MaskStrategy` | Enum: `PARTIAL`, `FULL`, `EMAIL`, `CREDIT_CARD`, `PHONE` |

## License

MIT
