package org.firstinspires.ftc.teamcode.util.Other

import com.qualcomm.robotcore.hardware.ColorSensor

class ColorSensorTypeValue(private var value: ColorSensor) : DynamicTypeValue<ColorSensor?> {
    override fun getValue(): ColorSensor? {
        return value
    }

     override fun setValue(value: ColorSensor?) {
         if (value != null) {
             this.value = value
         }
    }
}
