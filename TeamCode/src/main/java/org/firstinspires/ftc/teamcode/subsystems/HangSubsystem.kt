package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer

@Deprecated("")
class HangSubsystem(hardwareMap: HardwareMap) : SubsystemBase() {
    private val extensionMotor: DcMotor = RobotHardwareInitializer.MotorComponent.HANG_MOTOR[hardwareMap]
    private val dbp = FTCDashboardPackets("Hang Subsystem")

    fun setHangDirection(direction: HangDirection) {

        extensionMotor.power = (if (direction == HangDirection.IDLE) 0 else 1).toDouble()
        // TODO: Verify if the Direction.FORWARD and REVERSE is correct (may need to reverse it)
        val motorDirection =
            if (direction == HangDirection.DOWN) DcMotorSimple.Direction.FORWARD else DcMotorSimple.Direction.REVERSE
        extensionMotor.direction = motorDirection

        dbp.createNewTelePacket()
        dbp.info(String.format("Hang Direction: %s", direction.toString()))
        dbp.send(false)
    }

    // Used to determine whether the motor will go up, down, or remain idle
    enum class HangDirection {
        UP,
        DOWN,
        IDLE
    }
}
