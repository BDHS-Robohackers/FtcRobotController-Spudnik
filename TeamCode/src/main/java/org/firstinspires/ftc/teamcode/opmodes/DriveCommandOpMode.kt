package org.firstinspires.ftc.teamcode.opmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.button.Trigger
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.commands.DefaultDrive
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem
import org.firstinspires.ftc.teamcode.subsystems.ExtendoSystem
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem
import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import java.util.function.DoubleSupplier
import kotlin.math.abs

@TeleOp(name = "RealestDriverOpMode")
class DriveCommandOpMode : CommandOpMode() {
    private var driverController: GamepadEx? = null
    private var armerController: GamepadEx? = null
    private val slowdownButton = GamepadKeys.Button.RIGHT_BUMPER
    private val slowdownButton2 = GamepadKeys.Button.LEFT_BUMPER

    private var slowdownMultiplier: DoubleSupplier? = null
    private var forwardBack: DoubleSupplier? = null
    private var leftRight: DoubleSupplier? = null
    private var rotation: DoubleSupplier? = null

    private var driveSubsystem: DriveSubsystem? = null
    private val armSubsystem: ArmSubsystem? = null
    private var intakeSubsystem: IntakeSubsystem? = null
    private var uppieTwoSubsystem: UppieTwoSubsystem? = null
    private var extenderSystem: ExtendoSystem? = null

    private var driveCommand: DefaultDrive? = null

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
            driveCommand = DefaultDrive(driveSubsystem!!, gamepad1)
            driveSubsystem!!.defaultCommand = driveCommand
        } catch (e: Exception) {
            e.printStackTrace()
            requestOpModeStop()
            return
        }

        try {
            uppieTwoSubsystem = UppieTwoSubsystem(hardwareMap, false)

            armerController!!.getGamepadButton(GamepadKeys.Button.Y)
                .whileActiveContinuous(Runnable {
                    uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.MAX)
                })
                .whenInactive(Runnable { uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.IDLE) })

            armerController!!.getGamepadButton(GamepadKeys.Button.X)
                .whileActiveContinuous(Runnable {
                    uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.MIN)
                })
                .whenInactive(Runnable { uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.IDLE) })

            armerController!!.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenPressed(Runnable {
                uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.HOOK)
            })
            armerController!!.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).whenPressed(Runnable {
                uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.PICK_UP)
            })
            armerController!!.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(Runnable {
                    uppieTwoSubsystem!!.setUppieState(UppieTwoSubsystem.UppieState.ATTACH)
                })
        } catch (e: Exception) {
            dbp.info("ERROR IN UPPIE SYSTEM")
            dbp.error(e)
            dbp.send(true)
            telemetry.addData("UPPIE", "Error in uppie subsystem: " + e.message)
            telemetry.update()
            throw RuntimeException(e)
        }

        try {
            extenderSystem = ExtendoSystem(hardwareMap)

            /*armerController.getGamepadButton(GamepadKeys.Button.DPAD_UP).whileActiveContinuous(() -> {
                extenderSystem.setDirection(ExtendoSystem.Direction.OUTWARD);
            }).whenInactive(() -> extenderSystem.setDirection(ExtendoSystem.Direction.NONE));
            armerController.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whileActiveContinuous(() -> {
                extenderSystem.setDirection(ExtendoSystem.Direction.INWARD);
            }).whenInactive(() -> extenderSystem.setDirection(ExtendoSystem.Direction.NONE));*/
            val rightTrigger = Trigger {
                armerController!!.getTrigger(
                    GamepadKeys.Trigger.RIGHT_TRIGGER
                ) > 0.1
            }
            val leftTrigger = Trigger {
                armerController!!.getTrigger(
                    GamepadKeys.Trigger.LEFT_TRIGGER
                ) > 0.1
            }

            rightTrigger.whileActiveContinuous(Runnable {
                extenderSystem!!.setDirection(ExtendoSystem.Direction.OUTWARD)
            })
                .whenInactive(Runnable { extenderSystem!!.setDirection(ExtendoSystem.Direction.NONE) })
            leftTrigger.whileActiveContinuous(Runnable {
                extenderSystem!!.setDirection(ExtendoSystem.Direction.INWARD)
            })
                .whenInactive(Runnable { extenderSystem!!.setDirection(ExtendoSystem.Direction.NONE) })

            armerController!!.getGamepadButton(GamepadKeys.Button.BACK)
                .whenPressed(Runnable { uppieTwoSubsystem!!.resetMotorEncoders() })
        } catch (e: Exception) {
            dbp.info("ERROR IN EXTENDER SYSTEM")
            dbp.error(e)
            dbp.send(true)
            telemetry.addData("Extender", "Error in extender subsystem: " + e.message)
            telemetry.update()
            throw RuntimeException(e)
        }

        try {
            intakeSubsystem = IntakeSubsystem(hardwareMap)

            //armerController.getGamepadButton(GamepadKeys.Button.A).toggleWhenPressed(() -> intakeSubsystem.tiltIntake(), () -> intakeSubsystem.untiltIntake());
            val intakeTiltSupplier = DoubleSupplier { armerController!!.leftY }
            val intakeTiltTrigger = Trigger {
                abs(
                    intakeTiltSupplier.asDouble
                ) > .1
            }
            intakeTiltTrigger.whileActiveContinuous(Runnable {
                intakeSubsystem!!.moveTiltIntake(
                    intakeTiltSupplier.asDouble
                )
            })
                .whenInactive(Runnable { intakeSubsystem!!.moveTiltIntake(0.0) })

            //armerController.getGamepadButton(GamepadKeys.Button.B).whenPressed(() -> intakeSubsystem.toggleIntakeState());
            armerController!!.getGamepadButton(GamepadKeys.Button.B).whenPressed(Runnable {
                intakeSubsystem!!.setIntakeState(
                    true,
                    intakeSubsystem!!.intakeState
                )
            }).whenReleased(
                Runnable { intakeSubsystem!!.setIntakeState(false, intakeSubsystem!!.intakeState) })
            armerController!!.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(Runnable { intakeSubsystem!!.reverseIntake() }).whenReleased(
                    Runnable {
                        intakeSubsystem!!.setIntakeState(
                            false,
                            intakeSubsystem!!.intakeState
                        )
                    })
        } catch (e: Exception) {
            dbp.info("ERROR IN INTAKE SYSTEM")
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

    override fun reset() {
        super.reset()
    }

    private fun initializeDriveSuppliers() {
        slowdownMultiplier = DoubleSupplier {
            1.0 / (if ((driverController!!.getButton(slowdownButton)
                        || driverController!!.getButton(slowdownButton2)
                        || driverController!!.getButton(GamepadKeys.Button.A))
            ) 1.0 else 2.0)
        }
        rotation = DoubleSupplier { driverController!!.rightX * slowdownMultiplier!!.asDouble }

        forwardBack = DoubleSupplier {
            val dpadY = ((if (driverController!!.getButton(GamepadKeys.Button.DPAD_UP)) 1 else 0)
                    - (if (driverController!!.getButton(GamepadKeys.Button.DPAD_DOWN)) 1 else 0))
            if (dpadY != 0) {
                return@DoubleSupplier dpadY * slowdownMultiplier!!.asDouble
            } else {
                return@DoubleSupplier driverController!!.leftY * slowdownMultiplier!!.asDouble
            }
        }
        leftRight = DoubleSupplier {
            val dpadX =
                (-(if (driverController!!.getButton(GamepadKeys.Button.DPAD_RIGHT)) 1 else 0)
                        + (if (driverController!!.getButton(GamepadKeys.Button.DPAD_LEFT)) 1 else 0))
            if (dpadX != 0) {
                return@DoubleSupplier dpadX * slowdownMultiplier!!.asDouble
            } else {
                return@DoubleSupplier driverController!!.leftX * slowdownMultiplier!!.asDouble
            }
        }
    }
}
