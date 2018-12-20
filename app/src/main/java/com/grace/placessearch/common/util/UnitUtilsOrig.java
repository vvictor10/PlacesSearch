package com.grace.placessearch.common.util;

/**
 * Taken from u2020 example.
 */
@Deprecated
final class UnitUtilsOrig {
    private UnitUtilsOrig() {
        throw new AssertionError("No instances.");
    }

    /**
     * Multiply {@code size} by {@code factor} accounting for overflow.
     */
    static long multiply(long size, long factor, long over) {
        if (size > over) {
            return Long.MAX_VALUE;
        }
        if (size < -over) {
            return Long.MIN_VALUE;
        }
        return size * factor;
    }
}
