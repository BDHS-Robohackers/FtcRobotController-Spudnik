package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.button.Trigger
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import org.firstinspires.ftc.teamcode.commands.DefaultDrive
import org.firstinspires.ftc.teamcode.commands.MovePincherCommand
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem
import org.firstinspires.ftc.teamcode.subsystems.EmergencyArmSubsystem
import org.firstinspires.ftc.teamcode.subsystems.HangSubsystem
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem
import org.firstinspires.ftc.teamcode.util.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer
import java.util.function.DoubleSupplier
import kotlin.math.abs

class DriveCommandOpMode : CommandOpMode() {

    private lateinit var driverController: GamepadEx
    private lateinit var armerController:GamepadEx
    private val slowdownButton = GamepadKeys.Button.RIGHT_BUMPER
    private val slowdownButton2 = GamepadKeys.Button.LEFT_BUMPER

    private lateinit var slowdownMultiplier: DoubleSupplier
    private lateinit var forwardBack:DoubleSupplier
    private lateinit var leftRight:DoubleSupplier
    private lateinit var rotation:DoubleSupplier

    private lateinit var driveSubsystem: DriveSubsystem
    private lateinit var hangSubsystem: HangSubsystem
    private lateinit var armSubsystem: EmergencyArmSubsystem
    private lateinit var pincherSubsystem: PincherSubsystem

    private lateinit var driveCommand: DefaultDrive

    private val dbp = FTCDashboardPackets("TeleOP")

    override fun initialize() {
        driverController = GamepadEx(gamepad1)
        armerController = GamepadEx(gamepad2)

        dbp.createNewTelePacket()
        dbp.info("Initializing drive command op mode...")
        dbp.send(false)

        try {
            driveSubsystem = DriveSubsystem(hardwareMap)
            register(driveSubsystem)
            initializeDriveSuppliers()
            driveCommand = DefaultDrive(driveSubsystem, gamepad1)
            driveSubsystem.defaultCommand = driveCommand
        } catch (e: Exception) {
            e.printStackTrace()
            requestOpModeStop()
            return
        }

        try {
            hangSubsystem = HangSubsystem(hardwareMap)

            // D-Pad Up and D-Pad Down toggles the manages the hanging
            armerController.getGamepadButton(GamepadKeys.Button.Y).whileActiveContinuous(Runnable {
                hangSubsystem.setHangDirection(HangSubsystem.HangDirection.UP)
            }).whenInactive(Runnable {
                hangSubsystem.setHangDirection(HangSubsystem.HangDirection.IDLE)
            })

            armerController.getGamepadButton(GamepadKeys.Button.X).whileActiveContinuous(Runnable {
                hangSubsystem.setHangDirection(HangSubsystem.HangDirection.DOWN)
            }).whenInactive(Runnable {
                hangSubsystem.setHangDirection(HangSubsystem.HangDirection.IDLE)
            })
        } catch (e: Exception) {
            dbp.info("ERROR IN HANG SYSTEM")
            dbp.error(e)
            dbp.send(true)
            telemetry.addData("Hang", "Error in hang subsystem: " + e.message)
            telemetry.update()
            throw RuntimeException(e)
        }

        try {
            armSubsystem = EmergencyArmSubsystem(hardwareMap, telemetry)
            val threshold = .1f


            // Bumpers handle the lower arm
            // Triggers handle the higher arm
            // "A" toggles the pincher state
            // Left Joystick X moves the wrist
            val higherSupplier =
                DoubleSupplier { armerController.rightY } //() -> armerController.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) - armerController.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);
            val lowerSupplier = DoubleSupplier {
                armerController.getTrigger(
                    GamepadKeys.Trigger.RIGHT_TRIGGER
                ) - armerController.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)
            }

            Trigger {
                abs(
                    higherSupplier.asDouble
                ) > threshold
            }.whileActiveContinuous(
                Runnable {
                    armSubsystem.setHigherArmPower(higherSupplier.asDouble)
                }).whenInactive(Runnable {
                armSubsystem.setHigherArmPower(0.0)
            })

            Trigger {
                abs(
                    lowerSupplier.asDouble
                ) > threshold
            }.whileActiveContinuous(
                Runnable {
                    armSubsystem.setLowerArmPower(lowerSupplier.asDouble)
                }).whenInactive(Runnable {
                armSubsystem.setLowerArmPower(0.0)
            })
            
            Trigger {
                abs(
                    armerController.leftX
                ) > threshold
            }.whileActiveContinuous(
                Runnable {
                    armSubsystem.setWristPower(armerController.leftX)
                }).whenInactive(Runnable { armSubsystem.setWristPower(0.0) })

            val angularVelocityLower = 25.0
            armerController.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(Runnable {
                armSubsystem.constantX(angularVelocityLower)
            }).whenInactive(Runnable {
                armSubsystem.setLowerArmPower(0.0)
                armSubsystem.setHigherArmPower(0.0)
            })
            armerController.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(Runnable {
                armSubsystem.constantX(-angularVelocityLower)
            }).whenInactive(Runnable {
                armSubsystem.setLowerArmPower(0.0)
                armSubsystem.setHigherArmPower(0.0)
            })

            armerController.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenPressed(Runnable {
                armSubsystem.constantY(angularVelocityLower) // Todo: Might need to swap the negative sign
            }).whenInactive(Runnable {
                armSubsystem.setLowerArmPower(0.0)
                armSubsystem.setHigherArmPower(0.0)
            })
            armerController.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).whenPressed(Runnable {
                armSubsystem.constantY(-angularVelocityLower) // Todo: Might need to swap the negative sign
            }).whenInactive(Runnable {
                armSubsystem.setLowerArmPower(0.0)
                armSubsystem.setHigherArmPower(0.0)
            })
            Trigger {
                abs(
                    armerController.leftX
                ) > threshold
            }.whileActiveContinuous(
                Runnable {
                    armSubsystem.setWristPower(armerController.leftX)
                }).whenInactive(Runnable {
                armSubsystem.setWristPower(0.0)
            })
        } catch (e: Exception) {
            dbp.info("ERROR IN ARM SYSTEM")
            dbp.error(e)
            dbp.send(true)
            telemetry.addData("Arm", "Error in arm subsystem: " + e.message)
            telemetry.update()
            throw RuntimeException(e)
        }

        try {
            val pincher1 = RobotHardwareInitializer.ServoComponent.FINGER_1.getEx(
                hardwareMap,
                0.0,
                PincherSubsystem.MAX_ANGLE.toDouble()
            )
            val pincher2 = RobotHardwareInitializer.ServoComponent.FINGER_2.getEx(
                hardwareMap,
                0.0,
                PincherSubsystem.MAX_ANGLE.toDouble()
            )
            pincherSubsystem = PincherSubsystem(pincher1, pincher2)
            register(pincherSubsystem)

            val closePincher =
                MovePincherCommand(pincherSubsystem, PincherSubsystem.FingerPositions.CLOSED)
            val openPincher =
                MovePincherCommand(pincherSubsystem, PincherSubsystem.FingerPositions.OPEN)

            armerController.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(closePincher, openPincher)
            pincherSubsystem.closeFinger()
        } catch (e: Exception) {
            dbp.info("ERROR IN PINCHER SYSTEM")
            dbp.error(e)
            dbp.send(true)
            throw RuntimeException(e)
        }

        dbp.info("Subsystems registered.")
        dbp.send(false)

        dbp.info("Ready.")
        dbp.send(false)

        waitForStart()

        dbp.info("GO GO GO!")
        dbp.send(false)

        // NOTE: Do not include the opModeIsActive() while loop, as it prevents commands from running
    }

    private fun initializeDriveSuppliers() {
        slowdownMultiplier = DoubleSupplier {
            1.0 / (if ((driverController.getButton(slowdownButton)
                        || driverController.getButton(slowdownButton2)
                        || driverController.getButton(GamepadKeys.Button.A))
            ) 1.0 else 2.0)
        }
        rotation = DoubleSupplier { driverController.rightX * slowdownMultiplier.asDouble }

        forwardBack = DoubleSupplier {
            val dpadY =
                ((if (driverController.getButton(GamepadKeys.Button.DPAD_UP)) 1 else 0)
                        - (if (driverController.getButton(GamepadKeys.Button.DPAD_DOWN)) 1 else 0))
            if (dpadY != 0) {
                return@DoubleSupplier dpadY * slowdownMultiplier.asDouble
            } else {
                return@DoubleSupplier driverController.leftY * slowdownMultiplier.asDouble
            }
        }
        leftRight = DoubleSupplier {
            val dpadX =
                ((if (driverController.getButton(GamepadKeys.Button.DPAD_RIGHT)) 1 else 0)
                        - (if (driverController.getButton(GamepadKeys.Button.DPAD_LEFT)) 1 else 0))
            if (dpadX != 0) {
                return@DoubleSupplier dpadX * slowdownMultiplier.asDouble
            } else {
                return@DoubleSupplier driverController.leftX * slowdownMultiplier.asDouble
            }
        }
    }

}