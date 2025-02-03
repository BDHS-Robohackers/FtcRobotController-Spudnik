package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer
import java.util.Objects

class ExtendoSystem(hardwareMap: HardwareMap) : SubsystemBase() {
    val motor: DcMotorEx =
        RobotHardwareInitializer.MotorComponent.EXTENSION_VIPER.getEx(hardwareMap) /*, reverseMotor*/

    init {
        //this.reverseMotor = reverseMotor;
    }

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
        //reverseMotor.setPower(-power);
        dbp.info("Direction: $direction")
        dbp.send(true)
    }

    companion object {
        private val dbp = FTCDashboardPackets("ExtendoSubsystem")
    }
}
