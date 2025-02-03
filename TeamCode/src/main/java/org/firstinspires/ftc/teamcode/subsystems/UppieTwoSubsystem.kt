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
class UppieTwoSubsystem : SubsystemBase {
    private val viper: DcMotorEx

    //private final DcMotorEx viperRight;
    var lastStateChange: Long = 0

    private val dbp = FTCDashboardPackets("UppieTwoSubsystem")

    private var currentState = UppieState.IDLE

    enum class UppieState(val encoderPos: Int) {
        MAX(-4311),
        PICK_UP(PICK_UP_ENCODER_HEIGHT),
        HOOK(HOOK_ENCODER_HEIGHT),
        ATTACH(ATTACH_ENCODER_HEIGHT),
        MIN(0),
        IDLE(0)
    }

    @JvmOverloads
    constructor(viperLeft: DcMotorEx, resetMotorEncoder: Boolean = true) {
        this.viper = viperLeft
        if (resetMotorEncoder) viper.mode =
            DcMotor.RunMode.STOP_AND_RESET_ENCODER
        viper.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    @JvmOverloads
    constructor(map: HardwareMap, resetMotorEncoder: Boolean = true) {
        this.viper = RobotHardwareInitializer.MotorComponent.UPPIES.getEx(map)
        checkNotNull(this.viper)
        if (resetMotorEncoder) viper.mode =
            DcMotor.RunMode.STOP_AND_RESET_ENCODER
        viper.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    val isIdle: Boolean
        get() = currentState == UppieState.IDLE

    fun resetMotorEncoders() {
        viper.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        haltMotors()
    }

    override fun periodic() {
        super.periodic()
        if (currentState == UppieState.IDLE) return

        val currentPosition = viper.currentPosition
        dbp.info("Uppies Position: $currentPosition")
        dbp.send(true)


        if (currentState != UppieState.IDLE && RandomUtils.withinThreshold(
                viper.currentPosition,
                viper.targetPosition,
                10
            )
        ) {
            setUppieState(UppieState.IDLE)
        }

        /*if (!EMERGENCY_OVERRIDE && currentState == UppieState.MAX && currentPosition <= MAX_ENCODER_POS) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED MAX LIMIT. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.RUNG && withinThreshold(currentPosition, UppieState.RUNG.encoderPos, TARGET_THRESHOLD)) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED LIMIT to RUNG. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.RUNG && withinThreshold(currentPosition, UppieState.ATTACH.encoderPos, TARGET_THRESHOLD)) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED LIMIT to ATTACH. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.MIN && currentPosition >= MIN_ENCODER_POS) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED MIN LIMIT. HALTING.");
            dbp.send(true);
        }*/
    }

    fun setUppieState(state: UppieState) {
        dbp.info("Updating state to: $state")
        dbp.send(true)
        val lastState = this.currentState
        this.currentState = state

        if (state == UppieState.MIN) setPower(-1f)
        else if (state == UppieState.MAX) setPower(1f)
        else if (state == UppieState.IDLE) {
            haltMotors()
        } else if (state == UppieState.HOOK || state == UppieState.PICK_UP || state == UppieState.ATTACH) {
            viper.targetPosition = state.encoderPos
            setUsingPositionMode()
            viper.power = 0.8
        }

        // If the state changed, reset the timer. Allows for this method to be called periodically/every update.
        if (lastState != this.currentState) {
            lastStateChange = System.currentTimeMillis()
        }
        dbp.info("CURRENT POSITION: " + viper.currentPosition)
        dbp.send(true)
    }

    fun setPower(power: Float) {
        /*if (!heightDifferenceWithinThreshold()) {
            haltMotors();
            return;
        }*/

        setUsingEncoderMode()

        var CLAMPED_POWER = power.toDouble()

        if (power > MAX_POWER) CLAMPED_POWER = MAX_POWER
        else if (power < -MAX_POWER) CLAMPED_POWER = -MAX_POWER

        viper.power = CLAMPED_POWER
    }

    fun holdMotorPosition() {
        if (viper.mode == DcMotor.RunMode.RUN_TO_POSITION) return
        viper.targetPosition = viper.currentPosition
        viper.mode = DcMotor.RunMode.RUN_TO_POSITION

        viper.power = MAX_RTP_POWER
    }

    fun setUsingEncoderMode() {
        if (viper.mode == DcMotor.RunMode.RUN_USING_ENCODER) return

        viper.mode = DcMotor.RunMode.RUN_USING_ENCODER
        // this.viper.setPower(0);
    }

    fun setUsingPositionMode() {
        if (viper.mode == DcMotor.RunMode.RUN_TO_POSITION) return

        viper.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    fun haltMotors() {
        if (viper.power == 0.0) return
        if (KEEP_POS) {
            holdMotorPosition()
            return
        }
        dbp.info("Halting Motors!", true)
        viper.power = 0.0
    }

    companion object {
        //public static int PICK_UP_ENCODER_HEIGHT = -350, HOOK_ENCODER_HEIGHT = -2182, ATTACH_ENCODER_HEIGHT = -1705;
        var DOWNWARD_SHIFT: Int = 30
        var PICK_UP_ENCODER_HEIGHT: Int = DOWNWARD_SHIFT - 340
        var HOOK_ENCODER_HEIGHT: Int = DOWNWARD_SHIFT - 2182
        var ATTACH_ENCODER_HEIGHT: Int = DOWNWARD_SHIFT - 1695
        var MAX_POWER: Double = .75
        var MAX_RTP_POWER: Double = .9
        var KEEP_POS: Boolean = true

        /**
         * The max differential allowed between the two motor's encoder positions.
         */
        var MAX_DIFFERENTIAL_BETWEEN_MOTORS: Int = 10
        var TARGET_THRESHOLD: Int = 5
    }
}
