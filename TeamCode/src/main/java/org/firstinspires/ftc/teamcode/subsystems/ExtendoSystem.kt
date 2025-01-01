package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.util.FTCDashboardPackets
import java.util.Objects

class ExtendoSystem(val motor: DcMotorEx, val reverseMotor: DcMotorEx) : SubsystemBase() {
    enum class Direction {
        OUTWARD,
        INWARD,
        NONE
    }

    fun setDirection(direction: Direction) {
        Objects.requireNonNull(direction)
        val power =
            ((if (direction == Direction.OUTWARD) 1 else 0) - (if (direction == Direction.INWARD) 1 else 0)).toDouble()
        motor.power = power
        reverseMotor.power = -power
        dbp.info("Direction: $direction")
        dbp.send(true)
    }

    companion object {
        private val dbp = FTCDashboardPackets("ExtendoSubsystem")
    }
}
