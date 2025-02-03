package org.firstinspires.ftc.teamcode.util.Other

import com.qualcomm.robotcore.hardware.CRServo

class CRServoTypeValue(private var value: CRServo) : DynamicTypeValue<CRServo?> {
    override fun getValue(): CRServo? {
        return value
    }

    override fun setValue(value: CRServo) {
        this.value = value
    }
}
