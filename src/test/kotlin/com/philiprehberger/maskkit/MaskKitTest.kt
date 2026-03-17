package com.philiprehberger.maskkit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MaskKitTest {

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
    fun `maskPhone shows last 4 digits`() {
        assertEquals("*******8901", maskPhone("+1-234-567-8901"))
    }

    @Test
    fun `maskPhone short number`() {
        assertEquals("****", maskPhone("1234"))
    }

    @Test
    fun `maskCreditCard shows last 4 digits with dashes`() {
        assertEquals("****-****-****-5678", maskCreditCard("1234-5678-9012-5678"))
    }

    @Test
    fun `maskCreditCard digits only`() {
        assertEquals("****-****-****-5678", maskCreditCard("1234567890125678"))
    }

    data class User(
        val name: String,
        @Masked(strategy = MaskStrategy.EMAIL)
        val email: String,
        @Masked(strategy = MaskStrategy.PARTIAL, keep = 4)
        val ssn: String,
        @Masked(strategy = MaskStrategy.CREDIT_CARD)
        val creditCard: String,
        @Masked(strategy = MaskStrategy.PHONE)
        val phone: String,
        @Masked(strategy = MaskStrategy.FULL)
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
}
