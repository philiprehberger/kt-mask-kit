package com.philiprehberger.maskkit

/**
 * Defines the strategy used to mask sensitive data.
 */
enum class MaskStrategy {
    /** Masks all but the last [Masked.keep] characters with asterisks. */
    PARTIAL,

    /** Replaces the entire value with asterisks. */
    FULL,

    /** Masks the local part of an email address, preserving domain and first character. */
    EMAIL,

    /** Masks a credit card number, showing only the last 4 digits. */
    CREDIT_CARD,

    /** Masks a phone number, showing only the last 4 digits. */
    PHONE,
}
