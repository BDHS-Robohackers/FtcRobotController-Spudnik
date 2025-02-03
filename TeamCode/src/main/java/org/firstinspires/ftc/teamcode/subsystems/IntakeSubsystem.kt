package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer

@Config
class IntakeSubsystem(hardwareMap: HardwareMap) : SubsystemBase() {
    var powerMotor: DcMotorEx =
        RobotHardwareInitializer.MotorComponent.INTAKE_MOTOR.getEx(hardwareMap)

    //ServoEx tilterServo;
    var tilterServo: CRServo = RobotHardwareInitializer.CRServoComponent.INTAKE_TILTER[hardwareMap]
    val TILTER_POWER_SCALE: Double = .5
    var intakeState: Boolean = false
    val MAX_POWER: Double = 0.8

    init {
        tilterServo.direction = DcMotorSimple.Direction.FORWARD
        //this.tilterServo = RobotHardwareInitializer.ServoComponent.INTAKE_TILTER.getEx(hardwareMap);
        //this.tilterServo.setInverted(true);
        //tilterServo.setRange(0, 35);
    }

    fun tiltIntake() {
        //tilterServo.setPosition(.7f);
    }

    fun untiltIntake() {
        //tilterServo.setPosition(0.4f);
    }

    fun moveTiltIntake(power: Double) {
        tilterServo.power = power * TILTER_POWER_SCALE
    }

    fun setIntakeState(activated: Boolean, reversed: Boolean) {
        if (!reversed) powerMotor.power = if (activated) MAX_POWER else 0.0
        else powerMotor.power = if (activated) -MAX_POWER else 0.0
        intakeState = activated
    }

    fun toggleIntakeState() {
        setIntakeState(!intakeState, false)
    }

    fun reverseIntake() {
        setIntakeState(!intakeState, true)
    }

    companion object {
        var INTAKE_POSITION_PADDING: Double = 0.0 // "padding" from the max position of the servo
    }
}
