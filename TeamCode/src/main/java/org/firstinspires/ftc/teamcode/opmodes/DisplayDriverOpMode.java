package org.firstinspires.ftc.teamcode.opmodes;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.commands.DefaultDisplayArmCommand;
import org.firstinspires.ftc.teamcode.commands.DefaultDrive;
import org.firstinspires.ftc.teamcode.commands.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.MoveArmCommand;
import org.firstinspires.ftc.teamcode.commands.MoveFingerCommand;
import org.firstinspires.ftc.teamcode.commands.MoveWristCommand;
import org.firstinspires.ftc.teamcode.commands.ThrowAirplaneCommand;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DisplayArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.FingerSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.WristSubsystem;
import org.firstinspires.ftc.teamcode.util.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

import java.util.HashMap;
import java.util.function.DoubleSupplier;

@TeleOp(name = "8th Grade Night Display")
public class DisplayDriverOpMode extends CommandOpMode {
    private GamepadEx driverController, armerController;
    private final GamepadKeys.Button slowdownButton = GamepadKeys.Button.A;

    private DoubleSupplier slowdownMultiplier, forwardBack, leftRight, rotation;

    private DriveSubsystem driveSubsystem;
    private DisplayArmSubsystem displayArmSubsystem;

    private DefaultDisplayArmCommand defaultDisplayArmCommand;
    private DefaultDrive driveCommand;

    private final FTCDashboardPackets dbp = new FTCDashboardPackets("DisplaySystem");

    @Override
    public void initialize() {
        driverController = new GamepadEx(gamepad1);
        //armerController = new GamepadEx(gamepad2);

        dbp.createNewTelePacket();
        dbp.info("Initializing drive command op mode...");
        dbp.send(false);

        HashMap<RobotHardwareInitializer.DriveMotor, DcMotor> driveMotors = RobotHardwareInitializer.initializeDriveMotors(hardwareMap, this);

        assert driveMotors != null;
        driveSubsystem = new DriveSubsystem(driveMotors);

        boolean enableArm = false;

        if (enableArm) {
            displayArmSubsystem = new DisplayArmSubsystem(
                    hardwareMap.get(DcMotor.class, "arm1"),
                    hardwareMap.get(DcMotor.class, "arm2"));

            DoubleSupplier arm1Supplier = () -> armerController.getLeftX();
            DoubleSupplier arm2Supplier = () -> armerController.getRightX();
            defaultDisplayArmCommand = new DefaultDisplayArmCommand(displayArmSubsystem, arm1Supplier, arm2Supplier);
            register(displayArmSubsystem);
            displayArmSubsystem.setDefaultCommand(defaultDisplayArmCommand);
        }

        dbp.info("Subsystems built.");
        dbp.send(false);

        initializeDriveSuppliers();

        // Initialize commands for the subsystems

        driveCommand = new DefaultDrive(driveSubsystem, forwardBack, leftRight, rotation);

        register(driveSubsystem);

        driveSubsystem.setDefaultCommand(driveCommand);

        dbp.info("Subsystems registered.");
        dbp.send(false);

        dbp.info("Ready.");
        dbp.send(false);

        waitForStart();
    }

    private void initializeDriveSuppliers() {
        slowdownMultiplier = () -> 1d / (driverController.getButton(slowdownButton) ? 1d : 2d);
        rotation = () -> {
            /*return slowdownMultiplier.getAsDouble() *
                    (driverController.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) - driverController.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));*/
            return gamepad1.right_stick_x * slowdownMultiplier.getAsDouble();
            //(driverController.getRightX() * slowdownMultiplier.getAsDouble());
        };

        forwardBack = () -> {
            int dpadY = (driverController.getButton(GamepadKeys.Button.DPAD_UP) ? 1 : 0)
                    - (driverController.getButton(GamepadKeys.Button.DPAD_DOWN) ? 1 : 0);
            if(dpadY != 0) {
                return dpadY * slowdownMultiplier.getAsDouble();
            } else {
                return driverController.getLeftY() * slowdownMultiplier.getAsDouble();
            }
        };
        leftRight = () -> {
            int dpadX = (driverController.getButton(GamepadKeys.Button.DPAD_RIGHT) ? 1 : 0)
                    - (driverController.getButton(GamepadKeys.Button.DPAD_LEFT) ? 1 : 0);
            if(dpadX != 0) {
                return dpadX * slowdownMultiplier.getAsDouble();
            } else {
                return driverController.getLeftX() * slowdownMultiplier.getAsDouble();
            }
        };
    }
}
