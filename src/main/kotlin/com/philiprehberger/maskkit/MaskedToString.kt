package com.philiprehberger.maskkit

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

/**
 * Produces a `toString()`-like representation of this object with properties annotated
 * with [Masked] automatically masked according to their specified strategy.
 *
 * Properties without the [Masked] annotation are included as-is. The output format
 * matches Kotlin data class `toString()`: `ClassName(prop1=value1, prop2=value2)`.
 *
 * @return a string representation with sensitive properties masked
 */
fun <T : Any> T.maskedToString(): String {
    val klass = this::class
    val props = klass.declaredMemberProperties
    val entries = props.map { prop ->
        @Suppress("UNCHECKED_CAST")
        val property = prop as KProperty1<T, *>
        val value = property.get(this)
        val annotation = property.findAnnotation<Masked>()

        val displayValue = if (annotation != null && value != null) {
            val str = value.toString()
            val opts = MaskOptions(maskChar = annotation.maskChar)
            when (annotation.strategy) {
                MaskType.PARTIAL -> mask(str, annotation.keep, opts)
                MaskType.FULL -> opts.maskChar.toString().repeat(str.length)
                MaskType.EMAIL -> maskEmail(str, opts)
                MaskType.CREDIT_CARD -> maskCreditCard(str, opts)
                MaskType.PHONE -> maskPhone(str, opts)
                MaskType.SSN -> maskSsn(str, opts)
                MaskType.IBAN -> maskIban(str, opts)
            }
        } else {
            value?.toString() ?: "null"
        }

        "${prop.name}=$displayValue"
    }

    return "${klass.simpleName}(${entries.joinToString(", ")})"
}
