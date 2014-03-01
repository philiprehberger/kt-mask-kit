package com.philiprehberger.maskkit

/**
 * Annotation marking a property as containing sensitive data that should be masked
 * when using [maskedToString].
 *
 * @property strategy the masking strategy to apply (default: [MaskStrategy.PARTIAL])
 * @property keep the number of trailing characters to keep visible when using [MaskStrategy.PARTIAL] (default: 4)
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Masked(
    val strategy: MaskStrategy = MaskStrategy.PARTIAL,
    val keep: Int = 4,
)
