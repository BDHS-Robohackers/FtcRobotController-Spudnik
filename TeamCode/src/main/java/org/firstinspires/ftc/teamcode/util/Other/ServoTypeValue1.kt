package org.firstinspires.ftc.teamcode.util.Other

import com.qualcomm.robotcore.hardware.Servo

class ServoTypeValue(private var value: Servo) : DynamicTypeValue<Servo?> {
    override fun getValue(): Servo? {
        return this.value
    }

    override fun setValue(value: Servo) {
        this.value = value
    }
}
