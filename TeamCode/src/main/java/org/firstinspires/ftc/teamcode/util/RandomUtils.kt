package org.firstinspires.ftc.teamcode.util

import kotlin.math.abs

object RandomUtils {
    fun withinThreshold(VALUE: Int, TARGET: Int, THRESHOLD: Int): Boolean {
        return abs((VALUE - TARGET).toDouble()) <= THRESHOLD
    }

    fun withinThreshold(VALUE: Double, TARGET: Double, THRESHOLD: Double): Boolean {
        return abs(VALUE - TARGET) <= THRESHOLD
    }

    fun withinThreshold(VALUE: Float, TARGET: Float, THRESHOLD: Float): Boolean {
        return abs((VALUE - TARGET).toDouble()) <= THRESHOLD
    }
}
