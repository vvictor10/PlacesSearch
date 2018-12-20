package com.grace.placessearch.common.util

import com.grace.placessearch.common.util.UnitUtils.Companion.multiply

/**
 * A `DecimalByteUnit` represents power-of-ten byte sizes at a given unit of granularity and
 * provides utility methods to convert across units. A `DecimalByteUnit` does not maintain
 * byte size information, but only helps organize and use byte size representations that may be
 * maintained separately across various contexts.
 *
 *
 * Taken from u2020 example.
 */
enum class DecimalByteUnit : ByteUnit {
    /**
     * Byte unit representing one byte.
     */
    BYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toBytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return count
        }

        override fun toKilobytes(count: Long): Long {
            return count / (KB / B)
        }

        override fun toMegabytes(count: Long): Long {
            return count / (MB / B)
        }

        override fun toGigabytes(count: Long): Long {
            return count / (GB / B)
        }

        override fun toTerabytes(count: Long): Long {
            return count / (TB / B)
        }

        override fun toPetabytes(count: Long): Long {
            return count / (PB / B)
        }
    },

    /**
     * A byte unit representing 1000 bytes.
     */
    KILOBYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toKilobytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return multiply(count, KB / B, MAX / (KB / B))
        }

        override fun toKilobytes(count: Long): Long {
            return count
        }

        override fun toMegabytes(count: Long): Long {
            return count / (MB / KB)
        }

        override fun toGigabytes(count: Long): Long {
            return count / (GB / KB)
        }

        override fun toTerabytes(count: Long): Long {
            return count / (TB / KB)
        }

        override fun toPetabytes(count: Long): Long {
            return count / (PB / KB)
        }
    },

    /**
     * A byte unit representing 1000 kilobytes.
     */
    MEGABYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toMegabytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return multiply(count, MB / B, MAX / (MB / B))
        }

        override fun toKilobytes(count: Long): Long {
            return multiply(count, MB / KB, MAX / (MB / KB))
        }

        override fun toMegabytes(count: Long): Long {
            return count
        }

        override fun toGigabytes(count: Long): Long {
            return count / (GB / MB)
        }

        override fun toTerabytes(count: Long): Long {
            return count / (TB / MB)
        }

        override fun toPetabytes(count: Long): Long {
            return count / (PB / MB)
        }
    },

    /**
     * A byte unit representing 1000 megabytes.
     */
    GIGABYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toGigabytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return multiply(count, GB / B, MAX / (GB / B))
        }

        override fun toKilobytes(count: Long): Long {
            return multiply(count, GB / KB, MAX / (GB / KB))
        }

        override fun toMegabytes(count: Long): Long {
            return multiply(count, GB / MB, MAX / (GB / MB))
        }

        override fun toGigabytes(count: Long): Long {
            return count
        }

        override fun toTerabytes(count: Long): Long {
            return count / (TB / GB)
        }

        override fun toPetabytes(count: Long): Long {
            return count / (PB / GB)
        }
    },

    /**
     * A byte unit representing 1000 gigabytes.
     */
    TERABYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toTerabytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return multiply(count, TB / B, MAX / (TB / B))
        }

        override fun toKilobytes(count: Long): Long {
            return multiply(count, TB / KB, MAX / (TB / KB))
        }

        override fun toMegabytes(count: Long): Long {
            return multiply(count, TB / MB, MAX / (TB / MB))
        }

        override fun toGigabytes(count: Long): Long {
            return multiply(count, TB / GB, MAX / (TB / GB))
        }

        override fun toTerabytes(count: Long): Long {
            return count
        }

        override fun toPetabytes(count: Long): Long {
            return count / (PB / TB)
        }
    },

    /**
     * A byte unit representing 1000 terabytes.
     */
    PETABYTES {
        override fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
            return sourceUnit.toPetabytes(sourceCount)
        }

        override fun toBytes(count: Long): Long {
            return multiply(count, PB / B, MAX / (PB / B))
        }

        override fun toKilobytes(count: Long): Long {
            return multiply(count, PB / KB, MAX / (PB / KB))
        }

        override fun toMegabytes(count: Long): Long {
            return multiply(count, PB / MB, MAX / (PB / MB))
        }

        override fun toGigabytes(count: Long): Long {
            return multiply(count, PB / GB, MAX / (PB / GB))
        }

        override fun toTerabytes(count: Long): Long {
            return multiply(count, PB / TB, MAX / (PB / TB))
        }

        override fun toPetabytes(count: Long): Long {
            return count
        }
    };

    /**
     * Converts the given size in the given unit to this unit. Conversions from finer to coarser
     * granularities truncate, so lose precision. For example, converting from `999` bytes to
     * kilobytes results in `0`. Conversions from coarser to finer granularities with arguments
     * that would numerically overflow saturate to `Long.MIN_VALUE` if negative or
     * `Long.MAX_VALUE` if positive.
     *
     *
     * For example, to convert 10 kilobytes to bytes, use:
     * `ByteUnit.KILOBYTES.convert(10, ByteUnit.BYTES)`
     *
     * @param sourceCount the size in the given `sourceUnit`.
     * @param sourceUnit  the unit of the `sourceCount` argument.
     * @return the converted size in this unit, or `Long.MIN_VALUE` if conversion would
     * negatively overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun convert(sourceCount: Long, sourceUnit: DecimalByteUnit): Long {
        throw AbstractMethodError()
    }

    /**
     * Equivalent to [KILOBYTES.convert(count, this)][.convert].
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun toKilobytes(count: Long): Long {
        throw AbstractMethodError()
    }

    /**
     * Equivalent to [MEGABYTES.convert(count, this)][.convert].
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun toMegabytes(count: Long): Long {
        throw AbstractMethodError()
    }

    /**
     * Equivalent to [GIGABYTES.convert(count, this)][.convert].
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun toGigabytes(count: Long): Long {
        throw AbstractMethodError()
    }

    /**
     * Equivalent to [TERABYTES.convert(count, this)][.convert].
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun toTerabytes(count: Long): Long {
        throw AbstractMethodError()
    }

    /**
     * Equivalent to [PETABYTES.convert(count, this)][.convert].
     *
     * @param count the bit count
     * @return the converted count, or `Long.MIN_VALUE` if conversion would negatively
     * overflow, or `Long.MAX_VALUE` if it would positively overflow.
     */
    open fun toPetabytes(count: Long): Long {
        throw AbstractMethodError()
    }

    companion object {

        private val B = 1L
        private val KB = B * 1000L
        private val MB = KB * 1000L
        private val GB = MB * 1000L
        private val TB = GB * 1000L
        private val PB = TB * 1000L

        private val MAX = java.lang.Long.MAX_VALUE
    }
}