package com.grace.placessearch.util;

/**
 * Taken from u2020 example.
 */
final class UnitUtils {
    private UnitUtils() {
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
