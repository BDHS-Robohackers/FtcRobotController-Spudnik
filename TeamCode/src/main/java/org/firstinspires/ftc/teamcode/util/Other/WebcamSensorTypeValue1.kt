package org.firstinspires.ftc.teamcode.util.Other

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName

class WebcamSensorTypeValue(private var value: WebcamName) : DynamicTypeValue<WebcamName?> {
    override fun getValue(): WebcamName? {
        return value
    }

    override fun setValue(value: WebcamName?) {
        if (value != null) {
            this.value = value
        }
    }
}
