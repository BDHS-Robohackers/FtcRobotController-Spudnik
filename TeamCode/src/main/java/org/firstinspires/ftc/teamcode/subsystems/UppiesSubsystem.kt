package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets

@Deprecated("")
@Config
class UppiesSubsystem(var uppiesMotor: DcMotorEx) : SubsystemBase() {
    private var state: UppiesState
    var lastStateChange: Long = 0

    init {
        this.state = UppiesState.IDLE
        uppiesMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        uppiesMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        uppiesMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    fun setUppiesState(state: UppiesState) {
        dbp.info("Updating state to: $state")
        val lastState = this.state
        this.state = state
        // TODO: Verify that this is the correct direction
        uppiesMotor.power =
            (if (state == UppiesState.UN_UPPIES) speedMultiplier else -speedMultiplier).toDouble()
        if (state == UppiesState.IDLE) {
            uppiesMotor.power = 0.0
        }
        // If the state changed, reset the timer. Allows for this method to be called periodically/every update.
        if (lastState != this.state) {
            lastStateChange = System.currentTimeMillis()
        }
        dbp.send(true)
    }

    override fun periodic() {
        super.periodic()

        if (state == UppiesState.IDLE) {
            return
        }

        val currentPosition = uppiesMotor.currentPosition
        dbp2.info("Uppies Position: $currentPosition")
        dbp2.send(true)

        if (!PROGRAMATIC_IGNORE_LIMITS && state == UppiesState.UPPIES && currentPosition <= MIN_POSITION) {
            setUppiesState(UppiesState.IDLE)
            dbp.info("EXCEEDED MAX LIMIT. HALTING.")
            dbp.send(true)
        }

        if (!PROGRAMATIC_IGNORE_LIMITS && state == UppiesState.UN_UPPIES && currentPosition >= MAX_POSITION) {
            setUppiesState(UppiesState.IDLE)
            dbp.info("EXCEEDED MIN LIMIT. HALTING.")
            dbp.send(true)
        }

        if (!PROGRAMATIC_STALL_SAFETY) {
            return
        }

        val elapsedStateMillis = System.currentTimeMillis() - lastStateChange
        if (elapsedStateMillis > STALL_THRESHOLD) {
            val ticksPerSecond = uppiesMotor.getVelocity(AngleUnit.DEGREES)
            dbp.info("Ticks = $ticksPerSecond")
            dbp.send(false)
            if (ticksPerSecond < STALL_TICKS_PER_SECOND_THRESHOLD) {
                // STALLING!!!! STOP MOVING
                setUppiesState(UppiesState.IDLE)
                dbp.warn("UPPIES STALLING!")
                dbp.send(true)
            }
        }
    }

    enum class UppiesState {
        UPPIES,  // up
        UN_UPPIES,  // down
        IDLE
    }

    /**
     * @author Carter
     */
    fun IWantUp() {
        setUppiesState(UppiesState.UPPIES)
    }

    fun IWantDown() {
        setUppiesState(UppiesState.UN_UPPIES)
    }

    val isIdle: Boolean
        get() = this.state == UppiesState.IDLE

    companion object {
        var speedMultiplier: Float = .35f

        private val dbp = FTCDashboardPackets("UppiesSubsytem")
        private val dbp2 = FTCDashboardPackets("DebugPositionUppies")

        var PROGRAMATIC_STALL_SAFETY: Boolean = false
        var PROGRAMATIC_IGNORE_LIMITS: Boolean = false

        // After the motor has enough time to start moving, start checking if the motor is stalling.
        private const val STALL_THRESHOLD: Long = 300

        // Once the motor is moving less than the specified ticks per second, it is stalling.
        const val STALL_TICKS_PER_SECOND_THRESHOLD: Double = .5

        var MAX_POSITION: Int = -33
        var MIN_POSITION: Int = -5000
    }
}
