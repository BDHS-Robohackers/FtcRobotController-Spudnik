package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.hardware.ServoEx

@Deprecated("")
class BucketSubsystem(private val servo: ServoEx) : SubsystemBase() {
    init {
        servo.setRange(0.0, MAX_POSITION.toDouble())
    }

    fun moveToNormalPosition(normalPosition: Boolean) {
        servo.position = if (normalPosition) 0.05 else .8
    }

    fun dumpBucket() {
        moveToNormalPosition(false)
    }

    fun resetBucket() {
        moveToNormalPosition(true)
    }

    companion object {
        const val MAX_POSITION: Float = (90 + 5 // Was 90 + 25 as of 12/9/2024
                ).toFloat()
    }
}
