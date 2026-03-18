package com.philiprehberger.maskkit

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

/**
 * Masks a string value, replacing all but the last [keep] characters with the mask character.
 *
 * If the value is shorter than or equal to [keep], the entire value is replaced.
 *
 * @param value the string to mask
 * @param keep the number of trailing characters to leave visible (default: 4)
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked string
 */
fun mask(value: String, keep: Int = 4, options: MaskOptions = MaskOptions.DEFAULT): String {
    if (value.length <= keep) return options.maskChar.toString().repeat(value.length)
    return options.maskChar.toString().repeat(value.length - keep) + value.takeLast(keep)
}

/**
 * Masks an email address, preserving the first character of the local part and the full domain.
 *
 * For example, `"john.doe@example.com"` becomes `"j*******@example.com"`.
 * If the email has no `@` sign, falls back to [mask].
 *
 * @param email the email address to mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked email string
 */
fun maskEmail(email: String, options: MaskOptions = MaskOptions.DEFAULT): String {
    val atIndex = email.indexOf('@')
    if (atIndex <= 0) return mask(email, options = options)
    val local = email.substring(0, atIndex)
    val domain = email.substring(atIndex)
    return if (local.length <= 1) {
        "${options.maskChar}$domain"
    } else {
        local[0] + options.maskChar.toString().repeat(local.length - 1) + domain
    }
}

/**
 * Masks a phone number, showing only the last 4 digits.
 *
 * Non-digit characters are stripped before masking.
 *
 * @param phone the phone number to mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked phone string
 */
fun maskPhone(phone: String, options: MaskOptions = MaskOptions.DEFAULT): String {
    val digits = phone.filter { it.isDigit() }
    if (digits.length <= 4) return options.maskChar.toString().repeat(digits.length)
    return options.maskChar.toString().repeat(digits.length - 4) + digits.takeLast(4)
}

/**
 * Masks a credit card number, showing only the last 4 digits.
 *
 * Non-digit characters are stripped before masking, then the result is formatted
 * as groups of 4 with the leading groups replaced by the mask character.
 *
 * @param card the credit card number to mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked credit card string, e.g. `"****-****-****-1234"`
 */
fun maskCreditCard(card: String, options: MaskOptions = MaskOptions.DEFAULT): String {
    val digits = card.filter { it.isDigit() }
    if (digits.length <= 4) return options.maskChar.toString().repeat(digits.length)
    val masked = options.maskChar.toString().repeat(digits.length - 4) + digits.takeLast(4)
    return masked.chunked(4).joinToString("-")
}

/**
 * Masks a Social Security Number (SSN), showing only the last 4 digits.
 *
 * Non-digit characters are stripped before masking. The result is formatted as `***-**-1234`.
 * If the input has fewer than 4 digits, the entire value is masked.
 *
 * @param ssn the SSN to mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked SSN string
 */
fun maskSsn(ssn: String, options: MaskOptions = MaskOptions.DEFAULT): String {
    val digits = ssn.filter { it.isDigit() }
    val m = options.maskChar.toString()
    if (digits.length <= 4) return m.repeat(digits.length)
    val last4 = digits.takeLast(4)
    return "${m.repeat(3)}-${m.repeat(2)}-$last4"
}

/**
 * Masks an IBAN, preserving the country code (first 2 characters) and the last 4 characters.
 *
 * Everything between the country code and the last 4 characters is replaced with the mask character.
 *
 * @param iban the IBAN to mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked IBAN string
 */
fun maskIban(iban: String, options: MaskOptions = MaskOptions.DEFAULT): String {
    val cleaned = iban.replace(" ", "")
    if (cleaned.length <= 6) return options.maskChar.toString().repeat(cleaned.length)
    val country = cleaned.take(2)
    val last4 = cleaned.takeLast(4)
    val maskedLength = cleaned.length - 2 - 4
    return country + options.maskChar.toString().repeat(maskedLength) + last4
}

/**
 * Masks specific fields in a map according to the given field-to-strategy mapping.
 *
 * Fields not present in [fieldMasks] are left unchanged. Fields present in [fieldMasks]
 * but not in [map] are ignored.
 *
 * @param map the input map with string keys and any values
 * @param fieldMasks a map of field names to their [MaskStrategy]
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return a new map with the specified fields masked
 */
fun maskMap(
    map: Map<String, Any?>,
    fieldMasks: Map<String, MaskStrategy>,
    options: MaskOptions = MaskOptions.DEFAULT,
): Map<String, Any?> {
    return map.mapValues { (key, value) ->
        val strategy = fieldMasks[key]
        if (strategy != null && value != null) {
            applyStrategy(value.toString(), strategy, options)
        } else {
            value
        }
    }
}

/**
 * Masks specific fields of an object using reflection.
 *
 * Returns a map representation of the object with the specified fields masked using
 * [MaskStrategy.Full]. Properties not in [fieldNames] are included as-is.
 *
 * @param obj the object whose fields to mask
 * @param fieldNames the names of the fields to fully mask
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return a map of property names to their (possibly masked) string values
 */
fun maskFields(
    obj: Any,
    fieldNames: Set<String>,
    options: MaskOptions = MaskOptions.DEFAULT,
): Map<String, String?> {
    val klass = obj::class
    val props = klass.declaredMemberProperties
    return props.associate { prop ->
        @Suppress("UNCHECKED_CAST")
        val property = prop as KProperty1<Any, *>
        val value = property.get(obj)
        val displayValue = if (prop.name in fieldNames && value != null) {
            applyStrategy(value.toString(), MaskStrategy.Full, options)
        } else {
            value?.toString()
        }
        prop.name to displayValue
    }
}
