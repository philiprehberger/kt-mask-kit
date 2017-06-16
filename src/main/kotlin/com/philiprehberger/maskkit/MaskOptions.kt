package com.philiprehberger.maskkit

/**
 * Configuration options for masking operations.
 *
 * @property maskChar the character used for masking (default: `'*'`)
 */
public data class MaskOptions(
    public val maskChar: Char = '*',
) {
    public companion object {
        /** Default options using `*` as the mask character. */
        public val DEFAULT: MaskOptions = MaskOptions()
    }
}
