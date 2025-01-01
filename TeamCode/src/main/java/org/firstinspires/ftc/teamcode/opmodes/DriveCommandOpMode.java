package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.DefaultDrive;
import org.firstinspires.ftc.teamcode.commands.MovePincherCommand;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.EmergencyArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.HangSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem;
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

import java.util.function.DoubleSupplier;

@TeleOp(name = "RealestDriverOpMode")
public class DriveCommandOpMode extends CommandOpMode {


    private GamepadEx driverController, armerController;
    private final GamepadKeys.Button slowdownButton = GamepadKeys.Button.RIGHT_BUMPER;
    private final GamepadKeys.Button slowdownButton2 = GamepadKeys.Button.LEFT_BUMPER;

    private DoubleSupplier slowdownMultiplier, forwardBack, leftRight, rotation;

    private DriveSubsystem driveSubsystem;
    private ArmSubsystem armSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private UppieTwoSubsystem uppieTwoSubsystem;

    private DefaultDrive driveCommand;

    private final FTCDashboardPackets dbp = new FTCDashboardPackets("TeleOP");

    @Override
    public void initialize() {
        driverController = new GamepadEx(gamepad1);
        armerController = new GamepadEx(gamepad2);

        dbp.createNewTelePacket();
        dbp.info("Initializing drive command op mode...");
        dbp.send(false);

        try {
            driveSubsystem = new DriveSubsystem(hardwareMap);
            register(driveSubsystem);
            initializeDriveSuppliers();
            driveCommand = new DefaultDrive(driveSubsystem, gamepad1);
            driveSubsystem.setDefaultCommand(driveCommand);
        } catch (Exception e) {
            e.printStackTrace();
            requestOpModeStop();
            return;
        }

        try {
            uppieTwoSubsystem = new UppieTwoSubsystem(hardwareMap);

            // D-Pad Up and D-Pad Down toggles the manages the hanging

            armerController.getGamepadButton(GamepadKeys.Button.Y).whileActiveContinuous(() -> {
                uppieTwoSubsystem.setUppieState(UppieTwoSubsystem.UppieState.MAX);
            }).whenInactive(() -> uppieTwoSubsystem.setUppieState(UppieTwoSubsystem.UppieState.IDLE));

            armerController.getGamepadButton(GamepadKeys.Button.X).whileActiveContinuous(() -> {
                uppieTwoSubsystem.setUppieState(UppieTwoSubsystem.UppieState.MIN);
            }).whenInactive(() -> uppieTwoSubsystem.setUppieState(UppieTwoSubsystem.UppieState.IDLE));
        } catch (Exception e) {
            dbp.info("ERROR IN uppie SYSTEM");
            dbp.error(e);
            dbp.send(true);
            telemetry.addData("UPPIE", "Error in uppie subsystem: "+e.getMessage());
            telemetry.update();
            throw new RuntimeException(e);
        }

        try {
            armSubsystem = new ArmSubsystem(hardwareMap);
            float threshold = .1f;

            // Bumpers handle the lower arm
            // Triggers handle the higher arm
            // "A" toggles the pincher state
            // Left Joystick X moves the wrist

            DoubleSupplier higherSupplier = () -> armerController.getRightY();//() -> armerController.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) - armerController.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);

            new Trigger(() -> Math.abs(higherSupplier.getAsDouble()) > threshold).whileActiveContinuous(() -> {
                armSubsystem.setPower(higherSupplier.getAsDouble());
            }).whenInactive(() -> {
                armSubsystem.setPower(0);
            });

            /*armerController.getGamepadButton(GamepadKeys.Button.A).toggleWhenPressed(() -> {
                armSubsystem.setPinchState(EmergencyArmSubsystem.PinchState.OPEN);
            }, () -> {
                armSubsystem.setPinchState(EmergencyArmSubsystem.PinchState.PINCHED);
            });*/

        } catch (Exception e) {
            dbp.info("ERROR IN ARM SYSTEM");
            dbp.error(e);
            dbp.send(true);
            telemetry.addData("Arm", "Error in arm subsystem: "+e.getMessage());
            telemetry.update();
            throw new RuntimeException(e);
        }


        try {
            intakeSubsystem = new IntakeSubsystem(hardwareMap);

            armerController.getGamepadButton(GamepadKeys.Button.A).whenPressed(() -> intakeSubsystem.kickSampleOut()).whenReleased(() -> {
                intakeSubsystem.reset();
            });
        } catch (Exception e) {
            dbp.info("ERROR IN INTAKE SYSTEM");
            dbp.error(e);
            dbp.send(true);
            throw new RuntimeException(e);
        }


        dbp.info("Subsystems registered.");
        dbp.send(false);

        dbp.info("Ready.");
        dbp.send(false);

        waitForStart();

        dbp.info("GO GO GO!");
        dbp.send(false);

        // NOTE: Do not include the opModeIsActive() while loop, as it prevents commands from running
    }

    @Override
    public void reset() {
        super.reset();
    }

    private void initializeDriveSuppliers() {
        slowdownMultiplier = () -> 1d / ((driverController.getButton(slowdownButton)
                || driverController.getButton(slowdownButton2)
                || driverController.getButton(GamepadKeys.Button.A)) ? 1d : 2d);
        rotation = () -> driverController.getRightX() * slowdownMultiplier.getAsDouble();

        forwardBack = () -> {
            int dpadY = (driverController.getButton(GamepadKeys.Button.DPAD_UP) ? 1 : 0)
                    - (driverController.getButton(GamepadKeys.Button.DPAD_DOWN) ? 1 : 0);
            if (dpadY != 0) {
                return dpadY * slowdownMultiplier.getAsDouble();
            } else {
                return driverController.getLeftY() * slowdownMultiplier.getAsDouble();
            }
        };
        leftRight = () -> {
            int dpadX = (driverController.getButton(GamepadKeys.Button.DPAD_RIGHT) ? 1 : 0)
                    - (driverController.getButton(GamepadKeys.Button.DPAD_LEFT) ? 1 : 0);
            if (dpadX != 0) {
                return dpadX * slowdownMultiplier.getAsDouble();
            } else {
                return driverController.getLeftX() * slowdownMultiplier.getAsDouble();
            }
        };
    }

}
