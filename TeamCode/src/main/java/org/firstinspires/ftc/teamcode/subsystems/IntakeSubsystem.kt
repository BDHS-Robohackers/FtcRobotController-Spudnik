package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.hardware.ServoEx
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.util.FTCDashboardPackets

class IntakeSubsystem(var powerMotor: DcMotorEx, var tilterServo: ServoEx) : SubsystemBase() {
    private var currentState: Boolean = false

    init {
        tilterServo.setRange(0.0, 45.0)
        setIntakeDirection(DcMotorSimple.Direction.FORWARD)
    }

    fun tiltIntake() {
        tilterServo.position = 1.0
        dbp.info("Tilting Intake (Down)")
        dbp.send(true)
    }

    fun untiltIntake() {
        tilterServo.position = 0.0
        dbp.info("Untilting Intake (Up)")
        dbp.send(true)
    }

    private fun setIntakeState(activated: Boolean) {
        powerMotor.power = (if (activated) INTAKE_SPEED else .05f).toDouble()
        currentState = activated
        dbp.info("Setting Intake Activation State: $activated")
        dbp.send(true)
    }

    private fun setIntakeDirection(direction: DcMotorSimple.Direction) {
        powerMotor.direction = direction
    }

    fun toggleIntakeState() {
        setIntakeState(!currentState)
    }

    companion object {
        const val INTAKE_SPEED: Float = .95f

        private val dbp = FTCDashboardPackets("IntakeSubsystem")
    }
}
