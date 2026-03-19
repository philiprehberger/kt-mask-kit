package com.philiprehberger.maskkit

/**
 * Defines the strategy used to mask sensitive data in [Masked] annotations.
 *
 * This enum is used with the [@Masked][Masked] annotation on properties.
 */
public enum class MaskType {
    /** Masks all but the last [Masked.keep] characters. */
    PARTIAL,

    /** Replaces the entire value with the mask character. */
    FULL,

    /** Masks the local part of an email address, preserving domain and first character. */
    EMAIL,

    /** Masks a credit card number, showing only the last 4 digits. */
    CREDIT_CARD,

    /** Masks a phone number, showing only the last 4 digits. */
    PHONE,

    /** Masks an SSN, showing only the last 4 digits. */
    SSN,

    /** Masks an IBAN, showing only the country code and last 4 characters. */
    IBAN,
}

/**
 * Sealed interface defining flexible masking strategies for programmatic use.
 *
 * Use these strategies with [applyStrategy] or pass them to masking functions directly.
 */
public sealed interface MaskStrategy {
    /**
     * Replaces the entire value with the mask character.
     */
    public data object Full : MaskStrategy

    /**
     * Keeps [visibleStart] characters at the beginning and [visibleEnd] characters at the end visible,
     * masking everything in between.
     *
     * @property visibleStart number of leading characters to keep visible
     * @property visibleEnd number of trailing characters to keep visible
     */
    public data class Partial(public val visibleStart: Int = 0, public val visibleEnd: Int = 4) : MaskStrategy

    /**
     * Replaces matches of a regular expression [pattern] with the given [replacement].
     *
     * @property pattern the regex pattern to match
     * @property replacement the replacement string (may use group references like `$1`)
     */
    public data class Regex(public val pattern: kotlin.text.Regex, public val replacement: String) : MaskStrategy {
        public constructor(pattern: String, replacement: String) : this(kotlin.text.Regex(pattern), replacement)
    }
}

/**
 * Applies a [MaskStrategy] to the given [value] using the specified [options].
 *
 * @param value the string to mask
 * @param strategy the masking strategy to apply
 * @param options masking options (default: [MaskOptions.DEFAULT])
 * @return the masked string
 */
public fun applyStrategy(value: String, strategy: MaskStrategy, options: MaskOptions = MaskOptions.DEFAULT): String {
    return when (strategy) {
        is MaskStrategy.Full -> options.maskChar.toString().repeat(value.length)
        is MaskStrategy.Partial -> {
            val start = strategy.visibleStart
            val end = strategy.visibleEnd
            if (value.length <= start + end) {
                options.maskChar.toString().repeat(value.length)
            } else {
                val prefix = value.take(start)
                val suffix = value.takeLast(end)
                val maskedLength = value.length - start - end
                prefix + options.maskChar.toString().repeat(maskedLength) + suffix
            }
        }
        is MaskStrategy.Regex -> strategy.pattern.replace(value, strategy.replacement)
    }
}

// Backward-compatible typealias: the old enum name `MaskStrategy` is now `MaskType`.
// If existing code references the enum values (e.g., in annotations), update to use `MaskType`.
