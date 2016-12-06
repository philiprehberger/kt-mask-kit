package com.philiprehberger.maskkit

/**
 * Configuration options for masking operations.
 *
 * @property maskChar the character used for masking (default: `'*'`)
 */
data class MaskOptions(
    val maskChar: Char = '*',
) {
    companion object {
        /** Default options using `*` as the mask character. */
        val DEFAULT = MaskOptions()
    }
}
