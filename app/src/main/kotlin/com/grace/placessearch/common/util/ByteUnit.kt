package com.grace.placessearch.common.util

/**
 * A `ByteUnit` represents a size at a given unit of granularity which can be converted
 * into bytes. A `ByteUnit` does not maintain byte size information, but only helps use byte
 * size representations that may be maintained separately across various contexts.
 *
 * @see DecimalByteUnit
 *
 *
 * Taken from u2020 example.
 */
interface ByteUnit {
    /**
     * Converts the given size in the given unit to bytes. Conversions with arguments that would
     * numerically overflow saturate to `Long.MIN_VALUE` if negative or `Long.MAX_VALUE`
     * if positive.
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    fun toBytes(count: Long): Long
}

