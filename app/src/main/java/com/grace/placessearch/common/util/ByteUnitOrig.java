package com.grace.placessearch.common.util;

/**
 * A {@code ByteUnit} represents a size at a given unit of granularity which can be converted
 * into bytes. A {@code ByteUnit} does not maintain byte size information, but only helps use byte
 * size representations that may be maintained separately across various contexts.
 *
 * @see DecimalByteUnitOrig
 * <p/>
 * Taken from u2020 example.
 */
@Deprecated
public interface ByteUnitOrig {
    /**
     * Converts the given size in the given unit to bytes. Conversions with arguments that would
     * numerically overflow saturate to {@code Long.MIN_VALUE} if negative or {@code Long.MAX_VALUE}
     * if positive.
     *
     * @param count the bit count
     * @return the converted count, or {@code Long.MIN_VALUE} if conversion would negatively
     * overflow, or {@code Long.MAX_VALUE} if it would positively overflow.
     */
    long toBytes(long count);
}
