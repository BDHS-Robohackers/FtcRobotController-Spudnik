package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.subsystems.EmergencyArmSubsystem
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem

object AutonomousActions {
    private val dbp = FTCDashboardPackets("AutoActions")

    @Deprecated("")
    class Pincher(hardwareMap: HardwareMap?) {
        private var pincherSubsystem: PincherSubsystem

        init {
            try {
                val pincher1 =
                    RobotHardwareInitializer.ServoComponent.FINGER_1.getEx(hardwareMap, 0.0, 45.0)
                val pincher2 =
                    RobotHardwareInitializer.ServoComponent.FINGER_2.getEx(hardwareMap, 0.0, 45.0)
                pincherSubsystem = PincherSubsystem(pincher1, pincher2)
            } catch (e: Exception) {
                //e.printStackTrace();
                dbp.info("ERROR IN PINCHER (AUTO) SYSTEM")
                dbp.send(true)
                throw RuntimeException(e)
            }

            pincherSubsystem.closeFinger()
        }

        inner class OpenPincher : Action {
            private var initialized = false

            override fun run(p: TelemetryPacket): Boolean {
                if (!initialized) {
                    pincherSubsystem.openFinger()
                    initialized = true
                }
                return !pincherSubsystem.isFingerReady
            }
        }

        fun openPincher(): Action {
            return OpenPincher()
        }

        inner class ClosePincher : Action {
            private var initialized = false

            override fun run(p: TelemetryPacket): Boolean {
                if (!initialized) {
                    pincherSubsystem.closeFinger()
                    initialized = true
                }
                return !pincherSubsystem.isFingerReady
            }
        }

        fun closePincher(): Action {
            return ClosePincher()
        }
    }

    class EmergencyArm(hardwareMap: HardwareMap?, telemetry: Telemetry?) {
        private var emergencyArmSubsystem: EmergencyArmSubsystem

        init {
            try {
                emergencyArmSubsystem = EmergencyArmSubsystem(hardwareMap, telemetry)
            } catch (e: Exception) {
                dbp.info("[EMER_ARM] ERROR IN ARM SYSTEM")
                dbp.error(e)
                dbp.send(true)
                throw RuntimeException(e)
            }
        }

        inner class OpenPincher : Action {
            override fun run(p: TelemetryPacket): Boolean {
                emergencyArmSubsystem.openPincher()

                return true
            }
        }

        fun openPincher(): Action {
            return OpenPincher()
        }

        inner class ClosePincher : Action {
            override fun run(p: TelemetryPacket): Boolean {
                emergencyArmSubsystem.closePincher()

                return true
            }
        }

        fun closePincher(): Action {
            return ClosePincher()
        }
    }
}
