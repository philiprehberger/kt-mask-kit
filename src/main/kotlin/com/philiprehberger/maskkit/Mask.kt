package com.philiprehberger.maskkit

/**
 * Masks a string value, replacing all but the last [keep] characters with asterisks.
 *
 * If the value is shorter than or equal to [keep], the entire value is replaced with asterisks.
 *
 * @param value the string to mask
 * @param keep the number of trailing characters to leave visible (default: 4)
 * @return the masked string
 */
fun mask(value: String, keep: Int = 4): String {
    if (value.length <= keep) return "*".repeat(value.length)
    return "*".repeat(value.length - keep) + value.takeLast(keep)
}

/**
 * Masks an email address, preserving the first character of the local part and the full domain.
 *
 * For example, `"john.doe@example.com"` becomes `"j*******@example.com"`.
 * If the email has no `@` sign, falls back to [mask].
 *
 * @param email the email address to mask
 * @return the masked email string
 */
fun maskEmail(email: String): String {
    val atIndex = email.indexOf('@')
    if (atIndex <= 0) return mask(email)
    val local = email.substring(0, atIndex)
    val domain = email.substring(atIndex)
    return if (local.length <= 1) {
        "*$domain"
    } else {
        local[0] + "*".repeat(local.length - 1) + domain
    }
}

/**
 * Masks a phone number, showing only the last 4 digits.
 *
 * Non-digit characters are stripped before masking, then the result is formatted
 * as `***-****-{last4}`.
 *
 * @param phone the phone number to mask
 * @return the masked phone string
 */
fun maskPhone(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    if (digits.length <= 4) return "*".repeat(digits.length)
    return "*".repeat(digits.length - 4) + digits.takeLast(4)
}

/**
 * Masks a credit card number, showing only the last 4 digits.
 *
 * Non-digit characters are stripped before masking, then the result is formatted
 * as groups of 4 with the leading groups replaced by asterisks.
 *
 * @param card the credit card number to mask
 * @return the masked credit card string, e.g. `"****-****-****-1234"`
 */
fun maskCreditCard(card: String): String {
    val digits = card.filter { it.isDigit() }
    if (digits.length <= 4) return "*".repeat(digits.length)
    val masked = "*".repeat(digits.length - 4) + digits.takeLast(4)
    return masked.chunked(4).joinToString("-")
}
