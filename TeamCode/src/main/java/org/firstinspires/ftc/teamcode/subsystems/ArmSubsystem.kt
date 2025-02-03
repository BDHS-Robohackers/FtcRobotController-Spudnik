package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RandomUtils
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer

@Config
@Deprecated("")
class ArmSubsystem : SubsystemBase {
    private val ARM_MOTOR: DcMotorEx
    private val dbp = FTCDashboardPackets(this)

    constructor(armMotor: DcMotorEx) {
        ARM_MOTOR = armMotor

        if (!useExternalEncoder) {
            ARM_MOTOR.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            ARM_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    constructor(map: HardwareMap) {
        ARM_MOTOR = RobotHardwareInitializer.MotorComponent.ARM.getEx(map)

        if (!useExternalEncoder) {
            ARM_MOTOR.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            ARM_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    fun setPower(power: Double) {
        if (((RandomUtils.withinThreshold(
                ARM_MOTOR.currentPosition,
                MAX_ENCODER_POS,
                ENCODER_THRESHOLD
            ) && power > 0) ||
                    (RandomUtils.withinThreshold(
                        ARM_MOTOR.currentPosition,
                        MIN_ENCODER_POS,
                        ENCODER_THRESHOLD
                    ) && power < 0)) &&
            !EMERGENCY_OVERRIDE
        ) {
            dbp.warn("Arm Motor attempted to go past set limits, stopping...")
            zeroPower()
            return
        }

        if (power == 0.0) {
            zeroPower()
            return
        } else nonZeroPower()

        ARM_MOTOR.power = power
    }

    fun zeroPower() {
        if (!HOLD_ARM) {
            nonZeroPower()
            ARM_MOTOR.power = 0.0
        }
        if (ARM_MOTOR.mode == DcMotor.RunMode.RUN_TO_POSITION) return
        ARM_MOTOR.targetPosition = ARM_MOTOR.currentPosition
        ARM_MOTOR.mode = DcMotor.RunMode.RUN_TO_POSITION
        ARM_MOTOR.power = HOLD_POWER
    }

    fun nonZeroPower() {
        if (ARM_MOTOR.mode == DcMotor.RunMode.RUN_USING_ENCODER) return
        ARM_MOTOR.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    override fun periodic() {
        super.periodic()
        if (EMERGENCY_OVERRIDE) dbp.warn("EMERGENCY OVERRIDE IS ACTIVE!")
        //dbp.info("Current Arm Motor Pos: " + ARM_MOTOR.getCurrentPosition());
        // dbp.send(true);
    }

    companion object {
        var MAX_ENCODER_POS: Int = 1000
        var MIN_ENCODER_POS: Int = 0
        var ENCODER_THRESHOLD: Int = 5
        var useExternalEncoder: Boolean = true
        var EMERGENCY_OVERRIDE: Boolean = false
        var HOLD_POWER: Double = 1.0
        var HOLD_ARM: Boolean = true
    }
}
