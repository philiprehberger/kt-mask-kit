package com.philiprehberger.maskkit

/**
 * Annotation marking a property as containing sensitive data that should be masked
 * when using [maskedToString].
 *
 * @property strategy the masking strategy to apply (default: [MaskType.PARTIAL])
 * @property keep the number of trailing characters to keep visible when using [MaskType.PARTIAL] (default: 4)
 * @property maskChar the character used for masking (default: `'*'`)
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Masked(
    val strategy: MaskType = MaskType.PARTIAL,
    val keep: Int = 4,
    val maskChar: Char = '*',
)
