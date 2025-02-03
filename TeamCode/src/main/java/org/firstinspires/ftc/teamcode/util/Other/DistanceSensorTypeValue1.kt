package org.firstinspires.ftc.teamcode.util.Other

import com.qualcomm.robotcore.hardware.DistanceSensor

class DistanceSensorTypeValue(private var value: DistanceSensor) :
    DynamicTypeValue<DistanceSensor?> {
    override fun getValue(): DistanceSensor? {
        return value
    }

    override fun setValue(value: DistanceSensor?) {
        if (value != null) {
            this.value = value
        }
    }
}
