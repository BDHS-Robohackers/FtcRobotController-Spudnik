package org.firstinspires.ftc.teamcode.util;

public class RandomUtils {

    public static boolean withinThreshold(final int VALUE, final int TARGET, final int THRESHOLD) {
        return Math.abs(VALUE - TARGET) <= THRESHOLD;
    }

    public static boolean withinThreshold(final double VALUE, final double TARGET, final double THRESHOLD) {
        return Math.abs(VALUE - TARGET) <= THRESHOLD;
    }

    public static boolean withinThreshold(final float VALUE, final float TARGET, final float THRESHOLD) {
        return Math.abs(VALUE - TARGET) <= THRESHOLD;
    }
}
