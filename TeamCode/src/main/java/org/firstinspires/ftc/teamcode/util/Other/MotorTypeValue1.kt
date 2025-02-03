package org.firstinspires.ftc.teamcode.util.Other

import com.qualcomm.robotcore.hardware.DcMotor

class MotorTypeValue(private var value: DcMotor) : DynamicTypeValue<DcMotor?> {
    override fun getValue(): DcMotor? {
        return value
    }

    override fun setValue(value: DcMotor?) {
        if (value != null) {
            this.value = value
        }
    }
}
