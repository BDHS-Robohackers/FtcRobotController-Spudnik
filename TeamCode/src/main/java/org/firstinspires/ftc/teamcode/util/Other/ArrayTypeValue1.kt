package org.firstinspires.ftc.teamcode.util.Other

class ArrayTypeValue<T>(private val values: Array<T>) : DynamicTypeValue<T> {
    fun get(index: Int): T {
        return values[index]
    }

    fun set(index: Int, value: T) {
        values[index] = value
    }
}
