package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.subsystems.EmergencyArmSubsystem
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem
import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer.MotorComponent
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer.ServoComponent

object AutonomousActions {
    private val dbp = FTCDashboardPackets("AutoActions")

    class Uppie(map: HardwareMap) {
        private var uppieSubsystem: UppieTwoSubsystem? = null

        init {
            try {
                val viper = MotorComponent.UPPIES.getEx(map)
                uppieSubsystem = UppieTwoSubsystem(viper!!)
            } catch (e: Exception) {
                dbp.error("Error IN UPPIE (AUTO) SYSTEM")
                dbp.send(true)
                throw RuntimeException(e)
            }
        }

        inner class FullExtend : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    uppieSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.MAX)
                    initialized = true
                }
                return !uppieSubsystem!!.isIdle
            }
        }

        fun fullExtend(): Action {
            return FullExtend()
        }

        inner class FullRetract : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    uppieSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.MIN)
                    initialized = true
                }
                return !uppieSubsystem!!.isIdle
            }
        }

        fun fullRetract(): Action {
            return FullRetract()
        }

        inner class ToRung : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    uppieSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.HOOK)
                    initialized = true
                }
                return false
            }
        }

        fun toRung(): Action {
            return ToRung()
        }

        inner class ToAttach : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    uppieSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.ATTACH)
                    initialized = true
                }
                return false
            }
        }

        fun toAttach(): Action {
            return ToAttach()
        }

        inner class ToPickup : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    uppieSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.PICK_UP)
                    initialized = true
                }
                return false
            }
        }

        fun toPickup(): Action {
            return ToPickup()
        }
    }

    @Deprecated("")
    class Pincher(hardwareMap: HardwareMap) {
        private var pincherSubsystem: PincherSubsystem? = null

        init {
            try {
                val pincher1 = ServoComponent.FINGER_1.getEx(hardwareMap, 0.0, 45.0)
                val pincher2 = ServoComponent.FINGER_2.getEx(hardwareMap, 0.0, 45.0)
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

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    pincherSubsystem!!.openFinger()
                    initialized = true
                }
                return !pincherSubsystem!!.isFingerReady
            }
        }

        fun openPincher(): Action {
            return OpenPincher()
        }

        inner class ClosePincher : Action {
            private var initialized = false

            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                if (!initialized) {
                    pincherSubsystem!!.closeFinger()
                    initialized = true
                }
                return !pincherSubsystem!!.isFingerReady
            }
        }

        fun closePincher(): Action {
            return ClosePincher()
        }
    }

    @Deprecated("")
    class EmergencyArm(hardwareMap: HardwareMap, telemetry: Telemetry) {
        private var emergencyArmSubsystem: EmergencyArmSubsystem? = null

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
            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                emergencyArmSubsystem!!.openPincher()

                return true
            }
        }

        fun openPincher(): Action {
            return OpenPincher()
        }

        inner class ClosePincher : Action {
            override fun run(telemetryPacket: TelemetryPacket): Boolean {
                emergencyArmSubsystem!!.closePincher()

                return true
            }
        }

        fun closePincher(): Action {
            return ClosePincher()
        }
    }
}
