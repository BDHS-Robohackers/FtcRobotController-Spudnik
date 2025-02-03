package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.hardware.ServoEx
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer
import java.util.Objects
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Config
@Deprecated("")
class EmergencyArmSubsystem(map: HardwareMap, var telemetry: Telemetry) : SubsystemBase() {
    var armMotorLower: DcMotorEx = RobotHardwareInitializer.MotorComponent.LOWER_ARM.getEx(map)
    var armMotorHigher: DcMotorEx = RobotHardwareInitializer.MotorComponent.HIGHER_ARM.getEx(map)
    var pinchServo: ServoEx? = null
    var wristServo: CRServo? = null

    /**
     * @param power between [-1, 1]
     */
    fun setLowerArmPower(power: Double) {
        armMotorLower.mode = DcMotor.RunMode.RUN_USING_ENCODER
        armMotorLower.setVelocity(
            power * LOWER_ARM_SPEED_PERCENT * LOWER_ARM_ANGLE_VELOCITY,
            AngleUnit.DEGREES
        )
        if (abs(power) < KINEMATICS_DIVISOR_THRESHOLD) {
            armMotorLower.setVelocity(0.0, AngleUnit.DEGREES)
            haltLowerArm()
        }
    }

    /**
     * @param power between [-1, 1]
     */
    fun setHigherArmPower(power: Double) {
        armMotorHigher.mode = DcMotor.RunMode.RUN_USING_ENCODER
        armMotorHigher.setVelocity(
            power * HIGHER_ARM_SPEED_PERCENT * HIGHER_ARM_ANGLE_VELOCITY,
            AngleUnit.DEGREES
        )
        if (abs(power) < KINEMATICS_DIVISOR_THRESHOLD) {
            armMotorHigher.power = 0.0
            armMotorHigher.setVelocity(0.0, AngleUnit.DEGREES)
            haltHigherArm()
        }
    }

    fun haltLowerArm() {
        armMotorLower.targetPosition = armMotorLower.currentPosition
        armMotorLower.mode = DcMotor.RunMode.RUN_TO_POSITION
        armMotorLower.power = SET_POSITION_POWER
    }

    fun haltHigherArm() {
        armMotorHigher.targetPosition = armMotorHigher.currentPosition
        armMotorHigher.mode = DcMotor.RunMode.RUN_TO_POSITION
        armMotorHigher.power = SET_POSITION_POWER
    }

    val KINEMATICS_DIVISOR_THRESHOLD: Double = .2

    init {
        if (KEEP_PINCHER) {
            pinchServo = RobotHardwareInitializer.ServoComponent.PINCHER.getEx(map)
            wristServo = RobotHardwareInitializer.CRServoComponent.WRIST[map]
        }

        armMotorLower.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        armMotorHigher.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        armMotorLower.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        armMotorHigher.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        armMotorLower.mode = DcMotor.RunMode.RUN_USING_ENCODER
        armMotorHigher.mode = DcMotor.RunMode.RUN_USING_ENCODER
        haltLowerArm()
        haltHigherArm()
    }

    /**
     * Keeps the arm at a constant x position.
     * @param angularVelocityLowerUngeared the angle to move the lower arm
     */
    fun constantX(angularVelocityLowerUngeared: Double) {
        val thetaL = angleLower
        val thetaH = angleHigher

        var angularVelocityHigherUngeared =
            -LOWER_ARM_LENGTH * sin(Math.toRadians(thetaL)) * angularVelocityLowerUngeared
        val divisor = HIGHER_ARM_LENGTH * sin(Math.toRadians(thetaH))

        if (divisor < KINEMATICS_DIVISOR_THRESHOLD) {
            armMotorHigher.velocity = 0.0
            armMotorLower.velocity = 0.0
            return
        }

        angularVelocityHigherUngeared /= divisor

        if (abs(angularVelocityHigherUngeared) > 180) {
            // The angle is too big to keep x constant, and its too fast
            armMotorHigher.velocity = 0.0
            armMotorLower.velocity = 0.0
            return
        }

        val angularVelocityHigherGeared = getAngularVelocityHigher(angularVelocityHigherUngeared)
        val angularVelocityLowerGeared = getAngularVelocityLower(angularVelocityLowerUngeared)
        armMotorHigher.setVelocity(angularVelocityHigherGeared, AngleUnit.DEGREES)
        armMotorLower.setVelocity(angularVelocityLowerGeared, AngleUnit.DEGREES)
    }

    fun constantY(angularVelocityLowerUngeared: Double) {
        val thetaL = angleLower
        val thetaH = angleHigher

        var angularVelocityHigherUngeared =
            -LOWER_ARM_LENGTH * cos(Math.toRadians(thetaL)) * angularVelocityLowerUngeared
        val divisor = HIGHER_ARM_LENGTH * cos(Math.toRadians(thetaH))

        if (divisor < KINEMATICS_DIVISOR_THRESHOLD) {
            armMotorHigher.velocity = 0.0
            armMotorLower.velocity = 0.0
            return
        }

        angularVelocityHigherUngeared /= divisor

        if (abs(angularVelocityHigherUngeared) > 180) {
            // The angle is too big to keep x constant, and its too fast
            armMotorHigher.velocity = 0.0
            armMotorLower.velocity = 0.0
            return
        }

        val angularVelocityHigherGeared = getAngularVelocityHigher(angularVelocityHigherUngeared)
        val angularVelocityLowerGeared = getAngularVelocityLower(angularVelocityLowerUngeared)
        armMotorHigher.setVelocity(angularVelocityHigherGeared, AngleUnit.DEGREES)
        armMotorLower.setVelocity(angularVelocityLowerGeared, AngleUnit.DEGREES)
    }

    enum class PinchState(val pinchPosition: Double) {
        PINCHED(0),  // left state
        OPEN(0.5f),
        // middle state
    }

    fun setPinchState(state: PinchState) {
        if (KEEP_PINCHER) {
            Objects.requireNonNull(state)
            pinchServo!!.position = state.pinchPosition
        }
    }

    fun closePincher() {
        setPinchState(PinchState.PINCHED)
    }

    fun openPincher() {
        setPinchState(PinchState.OPEN)
    }

    /**
     * @param power between [-1, 1]
     */
    fun setWristPower(power: Double) {
        if (KEEP_PINCHER) {
            wristServo!!.power = power * WRIST_SPEED
            if (abs(power) < KINEMATICS_DIVISOR_THRESHOLD) {
                wristServo!!.power = 0.0
            }
        }
    }

    val angleLower: Double
        /**
         * @return angle of the lower motor relative to the front horizontal in degrees
         */
        get() = encoderToAngleLower(armMotorLower.currentPosition)

    val angleHigher: Double
        /**
         * @return angle of the higher motor relative to the front horizontal in degrees
         */
        get() = encoderToAngleHigher(armMotorHigher.currentPosition)

    fun encoderToAngleLower(encoderPosition: Int): Double {
        var calculatedAngle = encoderPosition.toDouble()
        calculatedAngle *= 360.0
        calculatedAngle /= LOWER_GEAR_RATIO * TICKS_PER_REVOLUTION
        //calculatedAngle = (((float) encoderPosition) * 360f) / (LOWER_GEAR_RATIO * armMotorLower.getMotorType().getTicksPerRev());
        return calculatedAngle + INITIAL_ANGLE_LOWER
    }

    fun encoderToAngleHigher(encoderPosition: Int): Double {
        var calculatedAngle = encoderPosition.toDouble()
        calculatedAngle *= 360.0
        calculatedAngle /= HIGHER_GEAR_RATIO * TICKS_PER_REVOLUTION
        return calculatedAngle + INITIAL_ANGLE_HIGHER
    }

    fun angleToEncoderPositionLower(angle: Double): Int {
        var angle = angle
        angle *= TICKS_PER_REVOLUTION * LOWER_GEAR_RATIO
        angle /= 360.0
        return angle.toInt()
    }

    fun angleToEncoderPositionHigher(angle: Double): Int {
        var angle = angle
        angle *= TICKS_PER_REVOLUTION * HIGHER_GEAR_RATIO
        angle /= 360.0
        return angle.toInt()
    }

    /**
     * Scales the given angle by the gear ratio
     */
    fun getAngularVelocityLower(angle: Double): Double {
        return angle * LOWER_GEAR_RATIO
    }

    /**
     * Scales the given angle by the gear ratio
     */
    fun getAngularVelocityHigher(angle: Double): Double {
        return angle * HIGHER_GEAR_RATIO
    }

    fun moveToAngleLower(angle: Double) {
        armMotorLower.targetPosition = angleToEncoderPositionLower(angle)
        armMotorLower.mode = DcMotor.RunMode.RUN_TO_POSITION
        armMotorLower.power = SET_POSITION_POWER
    }

    fun moveToAngleHigher(angle: Double) {
        armMotorHigher.targetPosition = angleToEncoderPositionHigher(angle)
        armMotorHigher.mode = DcMotor.RunMode.RUN_TO_POSITION
        armMotorHigher.power = SET_POSITION_POWER
    }

    override fun periodic() {
        super.periodic()
        //dbp.info("Current position: "+armMotorLower.getCurrentPosition()+", "+armMotorLower.getTargetPosition()+"\n"+armMotorHigher.getCurrentPosition()+", "+armMotorHigher.getTargetPosition());
        dbp.info(
            """
                Lower angle: ${angleLower}
                Right angle: ${angleHigher}
                """.trimIndent()
        )
        dbp.send(true)
        telemetry.addData(
            "Arm Angle", """
     Angle lower: ${angleLower}
     Angle higher${angleHigher}
     """.trimIndent()
        )
        telemetry.update()
    }

    companion object {
        const val THRESHOLD: Double = .5

        var WRIST_SPEED: Double = 1.0
        var LOWER_ARM_SPEED_PERCENT: Double = 1.0
        var HIGHER_ARM_SPEED_PERCENT: Double = 1.0
        var SET_POSITION_POWER: Double = .55

        var LOWER_ARM_ANGLE_VELOCITY: Double = 250.0
        var HIGHER_ARM_ANGLE_VELOCITY: Double = 250.0

        var INITIAL_ANGLE_LOWER: Double = 90 - 36.7
        var INITIAL_ANGLE_HIGHER: Double = 90 - 78.7
        var LOWER_ARM_LENGTH: Double = 10.0
        var HIGHER_ARM_LENGTH: Double = 7.0
        var LOWER_GEAR_RATIO: Double = 100.0
        var HIGHER_GEAR_RATIO: Double = 60.0
        var TICKS_PER_REVOLUTION: Double = 28.0

        const val KEEP_PINCHER: Boolean = false

        private val dbp = FTCDashboardPackets("ArmSubsystem")
    }
}
