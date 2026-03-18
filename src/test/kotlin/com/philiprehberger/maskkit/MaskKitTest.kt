package com.philiprehberger.maskkit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MaskKitTest {

    // ---- mask() ----

    @Test
    fun `mask with default keep`() {
        assertEquals("******7890", mask("1234567890"))
    }

    @Test
    fun `mask with custom keep`() {
        assertEquals("*****67890", mask("1234567890", keep = 5))
    }

    @Test
    fun `mask short value is fully masked`() {
        assertEquals("***", mask("abc", keep = 4))
    }

    @Test
    fun `mask empty string`() {
        assertEquals("", mask(""))
    }

    @Test
    fun `mask single character`() {
        assertEquals("*", mask("a"))
    }

    @Test
    fun `mask with custom mask character`() {
        assertEquals("######7890", mask("1234567890", options = MaskOptions(maskChar = '#')))
    }

    @Test
    fun `mask with custom mask character and keep`() {
        assertEquals("XXXXX67890", mask("1234567890", keep = 5, options = MaskOptions(maskChar = 'X')))
    }

    @Test
    fun `mask unicode content`() {
        assertEquals("****\u00e9f", mask("abcd\u00e9f", keep = 2))
    }

    @Test
    fun `mask emoji content`() {
        val input = "hello\uD83D\uDE00world"
        val result = mask(input, keep = 5)
        assertTrue(result.endsWith("world"))
    }

    // ---- maskEmail() ----

    @Test
    fun `maskEmail preserves first char and domain`() {
        assertEquals("j*******@example.com", maskEmail("john.doe@example.com"))
    }

    @Test
    fun `maskEmail single char local part`() {
        assertEquals("*@example.com", maskEmail("j@example.com"))
    }

    @Test
    fun `maskEmail without at sign falls back to mask`() {
        assertEquals("*****alid", maskEmail("noinvalid"))
    }

    @Test
    fun `maskEmail with custom mask character`() {
        assertEquals("j#######@example.com", maskEmail("john.doe@example.com", MaskOptions(maskChar = '#')))
    }

    // ---- maskPhone() ----

    @Test
    fun `maskPhone shows last 4 digits`() {
        assertEquals("*******8901", maskPhone("+1-234-567-8901"))
    }

    @Test
    fun `maskPhone short number`() {
        assertEquals("****", maskPhone("1234"))
    }

    @Test
    fun `maskPhone empty string`() {
        assertEquals("", maskPhone(""))
    }

    // ---- maskCreditCard() ----

    @Test
    fun `maskCreditCard shows last 4 digits with dashes`() {
        assertEquals("****-****-****-5678", maskCreditCard("1234-5678-9012-5678"))
    }

    @Test
    fun `maskCreditCard digits only`() {
        assertEquals("****-****-****-5678", maskCreditCard("1234567890125678"))
    }

    @Test
    fun `maskCreditCard with custom mask character`() {
        assertEquals("####-####-####-5678", maskCreditCard("1234567890125678", MaskOptions(maskChar = '#')))
    }

    // ---- maskSsn() ----

    @Test
    fun `maskSsn standard format`() {
        assertEquals("***-**-6789", maskSsn("123-45-6789"))
    }

    @Test
    fun `maskSsn digits only`() {
        assertEquals("***-**-6789", maskSsn("123456789"))
    }

    @Test
    fun `maskSsn short input`() {
        assertEquals("****", maskSsn("1234"))
    }

    @Test
    fun `maskSsn empty string`() {
        assertEquals("", maskSsn(""))
    }

    @Test
    fun `maskSsn with custom mask character`() {
        assertEquals("###-##-6789", maskSsn("123-45-6789", MaskOptions(maskChar = '#')))
    }

    // ---- maskIban() ----

    @Test
    fun `maskIban standard format`() {
        assertEquals("DE****************6789", maskIban("DE89370400440532016789"))
    }

    @Test
    fun `maskIban with spaces`() {
        assertEquals("GB******************6789", maskIban("GB29 NWBK 6016 1331 9268 6789"))
    }

    @Test
    fun `maskIban short input`() {
        assertEquals("******", maskIban("DE1234"))
    }

    @Test
    fun `maskIban empty string`() {
        assertEquals("", maskIban(""))
    }

    @Test
    fun `maskIban with custom mask character`() {
        assertEquals("DE################6789", maskIban("DE89370400440532016789", MaskOptions(maskChar = '#')))
    }

    // ---- MaskStrategy sealed interface ----

    @Test
    fun `MaskStrategy Full masks entire value`() {
        assertEquals("*****", applyStrategy("hello", MaskStrategy.Full))
    }

    @Test
    fun `MaskStrategy Full with custom char`() {
        assertEquals("#####", applyStrategy("hello", MaskStrategy.Full, MaskOptions(maskChar = '#')))
    }

    @Test
    fun `MaskStrategy Full empty string`() {
        assertEquals("", applyStrategy("", MaskStrategy.Full))
    }

    @Test
    fun `MaskStrategy Partial default`() {
        assertEquals("******7890", applyStrategy("1234567890", MaskStrategy.Partial()))
    }

    @Test
    fun `MaskStrategy Partial with visible start and end`() {
        assertEquals("12****90", applyStrategy("12345690", MaskStrategy.Partial(visibleStart = 2, visibleEnd = 2)))
    }

    @Test
    fun `MaskStrategy Partial value shorter than visible`() {
        assertEquals("***", applyStrategy("abc", MaskStrategy.Partial(visibleStart = 2, visibleEnd = 2)))
    }

    @Test
    fun `MaskStrategy Partial with only visibleStart`() {
        assertEquals("he***", applyStrategy("hello", MaskStrategy.Partial(visibleStart = 2, visibleEnd = 0)))
    }

    @Test
    fun `MaskStrategy Regex replaces pattern`() {
        val strategy = MaskStrategy.Regex("\\d", "*")
        assertEquals("abc-***-def", applyStrategy("abc-123-def", strategy))
    }

    @Test
    fun `MaskStrategy Regex with group references`() {
        val strategy = MaskStrategy.Regex("(\\d{3})-(\\d{2})-(\\d{4})", "***-**-$3")
        assertEquals("***-**-6789", applyStrategy("123-45-6789", strategy))
    }

    @Test
    fun `MaskStrategy Regex no match leaves value unchanged`() {
        val strategy = MaskStrategy.Regex("\\d+", "***")
        assertEquals("hello", applyStrategy("hello", strategy))
    }

    // ---- maskMap() ----

    @Test
    fun `maskMap masks specified fields`() {
        val data = mapOf("name" to "John", "email" to "john@example.com", "age" to 30)
        val fieldMasks = mapOf(
            "email" to MaskStrategy.Full,
            "name" to MaskStrategy.Partial(visibleStart = 1, visibleEnd = 0),
        )
        val result = maskMap(data, fieldMasks)
        assertEquals("J***", result["name"])
        assertEquals("****************", result["email"])
        assertEquals(30, result["age"])
    }

    @Test
    fun `maskMap with null values`() {
        val data = mapOf<String, Any?>("name" to "John", "email" to null)
        val fieldMasks = mapOf("email" to MaskStrategy.Full)
        val result = maskMap(data, fieldMasks)
        assertEquals(null, result["email"])
        assertEquals("John", result["name"])
    }

    @Test
    fun `maskMap with missing field in map`() {
        val data = mapOf("name" to "John")
        val fieldMasks = mapOf("nonexistent" to MaskStrategy.Full)
        val result = maskMap(data, fieldMasks)
        assertEquals("John", result["name"])
    }

    @Test
    fun `maskMap with custom mask character`() {
        val data = mapOf("secret" to "password123")
        val fieldMasks = mapOf("secret" to MaskStrategy.Full)
        val result = maskMap(data, fieldMasks, MaskOptions(maskChar = '#'))
        assertEquals("###########", result["secret"])
    }

    // ---- maskFields() ----

    data class Person(val name: String, val email: String, val age: Int)

    @Test
    fun `maskFields masks specified field names`() {
        val person = Person("John", "john@example.com", 30)
        val result = maskFields(person, setOf("email", "name"))
        assertEquals("****", result["name"])
        assertEquals("****************", result["email"])
        assertEquals("30", result["age"])
    }

    @Test
    fun `maskFields with custom mask character`() {
        val person = Person("John", "john@example.com", 30)
        val result = maskFields(person, setOf("name"), MaskOptions(maskChar = '#'))
        assertEquals("####", result["name"])
        assertEquals("john@example.com", result["email"])
    }

    @Test
    fun `maskFields with no fields to mask`() {
        val person = Person("John", "john@example.com", 30)
        val result = maskFields(person, emptySet())
        assertEquals("John", result["name"])
        assertEquals("john@example.com", result["email"])
    }

    // ---- maskedToString() ----

    data class User(
        val name: String,
        @Masked(strategy = MaskType.EMAIL)
        val email: String,
        @Masked(strategy = MaskType.PARTIAL, keep = 4)
        val ssn: String,
        @Masked(strategy = MaskType.CREDIT_CARD)
        val creditCard: String,
        @Masked(strategy = MaskType.PHONE)
        val phone: String,
        @Masked(strategy = MaskType.FULL)
        val secret: String,
    )

    @Test
    fun `maskedToString masks annotated properties`() {
        val user = User(
            name = "John Doe",
            email = "john@example.com",
            ssn = "123-45-6789",
            creditCard = "4111111111111111",
            phone = "+1-555-123-4567",
            secret = "mysecret",
        )
        val result = user.maskedToString()

        assertTrue(result.startsWith("User("), "Should start with class name")
        assertTrue("name=John Doe" in result, "Non-masked property should be visible")
        assertTrue("j***@example.com" in result, "Email should be masked")
        assertTrue("*******6789" in result, "SSN should show last 4")
        assertTrue("****-****-****-1111" in result, "Credit card should be masked")
        assertTrue("*******4567" in result, "Phone should show last 4 digits")
        assertTrue("secret=********" in result, "Full masking should replace all chars")
    }

    @Test
    fun `maskedToString handles null values`() {
        data class OptionalUser(
            val name: String,
            @Masked
            val ssn: String?,
        )

        val user = OptionalUser(name = "Jane", ssn = null)
        val result = user.maskedToString()
        assertTrue("ssn=null" in result, "Null values should display as 'null'")
    }

    @Test
    fun `maskedToString with SSN strategy`() {
        data class SsnHolder(
            @Masked(strategy = MaskType.SSN)
            val ssn: String,
        )

        val holder = SsnHolder(ssn = "123-45-6789")
        val result = holder.maskedToString()
        assertTrue("***-**-6789" in result, "SSN should be formatted as ***-**-6789")
    }

    @Test
    fun `maskedToString with IBAN strategy`() {
        data class IbanHolder(
            @Masked(strategy = MaskType.IBAN)
            val iban: String,
        )

        val holder = IbanHolder(iban = "DE89370400440532016789")
        val result = holder.maskedToString()
        assertTrue("DE" in result, "IBAN should preserve country code")
        assertTrue(result.endsWith(")"), "Should end with closing paren")
        assertTrue("6789" in result, "IBAN should preserve last 4 characters")
    }

    @Test
    fun `maskedToString with custom mask character`() {
        data class SecretHolder(
            @Masked(strategy = MaskType.FULL, maskChar = '#')
            val secret: String,
        )

        val holder = SecretHolder(secret = "mysecret")
        val result = holder.maskedToString()
        assertTrue("########" in result, "Should use custom mask character")
    }

    // ---- Edge cases ----

    @Test
    fun `mask empty strings across all functions`() {
        assertEquals("", mask(""))
        assertEquals("", maskPhone(""))
        assertEquals("", maskSsn(""))
        assertEquals("", maskIban(""))
        assertEquals("", applyStrategy("", MaskStrategy.Full))
        assertEquals("", applyStrategy("", MaskStrategy.Partial()))
    }

    @Test
    fun `mask single character values`() {
        assertEquals("*", mask("a"))
        assertEquals("*", mask("a", keep = 0))
        assertEquals("*", mask("a", keep = 1))
        assertEquals("*", applyStrategy("a", MaskStrategy.Full))
        assertEquals("*", applyStrategy("a", MaskStrategy.Partial(visibleStart = 0, visibleEnd = 0)))
    }

    @Test
    fun `unicode content masking`() {
        assertEquals("***\u00fcr", mask("Nat\u00fcr", keep = 2))
        assertEquals("*****", applyStrategy("\u00e4\u00f6\u00fc\u00df\u20ac", MaskStrategy.Full))
        assertEquals("\u00e4***\u20ac", applyStrategy("\u00e4\u00f6\u00fc\u00df\u20ac", MaskStrategy.Partial(visibleStart = 1, visibleEnd = 1)))
    }

    @Test
    fun `unicode email masking`() {
        assertEquals("u****@example.com", maskEmail("u\u00fcser@example.com"))
    }
}
