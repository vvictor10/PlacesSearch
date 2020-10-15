package com.grace.placessearch.common.util

/**
 * Taken from u2020 example.
 */
internal class UnitUtils private constructor() {
    init {
        throw AssertionError("No instances.")
    }

    companion object {

        /**
         * Multiply `size` by `factor` accounting for overflow.
         */
        fun multiply(size: Long, factor: Long, over: Long): Long {
            return when {
                (size > over) -> Long.MAX_VALUE
                (size < -over) -> Long.MIN_VALUE
                else -> size * factor
            }
        }
    }
}
