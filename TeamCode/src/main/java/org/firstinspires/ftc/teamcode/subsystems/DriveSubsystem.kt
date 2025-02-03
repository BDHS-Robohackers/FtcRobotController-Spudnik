package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer.MIN_POWER;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

import android.annotation.SuppressLint;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.HashMap;

public class DriveSubsystem extends SubsystemBase {
    private DcMotor lF, rF, lB, rB;
    private DcMotor eL, eB, eR;
    private final double INCHES_PER_TICK = 0.0018912d;
    public ElapsedTime elapsedTime;
    private final FTCDashboardPackets dbp = new FTCDashboardPackets("DriveSubsystem");
    MecanumDrive drive;

    @Deprecated
    public DriveSubsystem(HashMap<RobotHardwareInitializer.Component, DcMotor> driveMotors) {
        this(driveMotors.get(RobotHardwareInitializer.MotorComponent.LEFT_FRONT),
                driveMotors.get(RobotHardwareInitializer.MotorComponent.RIGHT_FRONT),
                driveMotors.get(RobotHardwareInitializer.MotorComponent.LEFT_BACK),
                driveMotors.get(RobotHardwareInitializer.MotorComponent.RIGHT_BACK),
                driveMotors.get(RobotHardwareInitializer.EncoderComponent.ENCODER_PAR0),
                driveMotors.get(RobotHardwareInitializer.EncoderComponent.ENCODER_PERP),
                driveMotors.get(RobotHardwareInitializer.EncoderComponent.ENCODER_PAR1));
    }

    public DriveSubsystem(HardwareMap hMap) {
        drive = new MecanumDrive(hMap, new Pose2d(0, 0, 0));
    }

    public DriveSubsystem(final DcMotor leftFront, final DcMotor rightFront,
                          final DcMotor leftBack, final DcMotor rightBack,
                          final DcMotor encoderLeft, final DcMotor encoderBack,
                          final DcMotor encoderRight) {
        dbp.createNewTelePacket();
        dbp.info(leftFront+", "+leftBack+", "+rightFront+", "+rightBack);
        dbp.send(false);

        lF = leftFront;
        rF = rightFront;
        lB = leftBack;
        rB = rightBack;

        eL = encoderLeft;
        eB = encoderBack;
        eR = encoderRight;

        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    }

    @SuppressLint("DefaultLocale")
    @Deprecated
    public boolean moveRobot(double axial, double lateral, double yaw) {

        dbp.createNewTelePacket();
        dbp.info("Moving robot in subsystem: "+axial+", "+lateral+", "+yaw);
        dbp.send(true);

        double max;

        double leftFrontPower  = axial - lateral - yaw;
        double rightFrontPower = axial + lateral + yaw;
        double leftBackPower   = axial + lateral - yaw;
        double rightBackPower  = axial - lateral + yaw;

        dbp.createNewTelePacket();

        dbp.debug("LF: " + String.format("%f\n", leftFrontPower), false);
        dbp.debug("RF: " + String.format("%f\n", rightFrontPower), false);
        dbp.debug("LB: " + String.format("%f\n", leftBackPower), false);
        dbp.debug("RB: " + String.format("%f\n", rightBackPower), true);

        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower  /= max;
            rightFrontPower /= max;
            leftBackPower   /= max;
            rightBackPower  /= max;
        }
        lF.setPower(leftFrontPower);
        rF.setPower(rightFrontPower);
        lB.setPower(leftBackPower);
        rB.setPower(rightBackPower);
        return true;
    }

    public void moveRobotMecanum(double forwardBackward, double leftRight, double rotation) {
        drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(
                        forwardBackward,
                        -leftRight
                ),
                -rotation
        ));

        drive.updatePoseEstimate();
    }

    public void moveRobot(Gamepad gamepad1) {
        /*drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x
                ),
                -gamepad1.right_stick_x
        ));*/
        drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(
                        -gamepad1.left_stick_y,
                        -(gamepad1.right_trigger - gamepad1.left_trigger)
                ),
                -gamepad1.right_stick_x
        ));

        drive.updatePoseEstimate();
    }

    /**
     * Moves the robot by a specified distance
     * @param axial Forward/Backward Movement
     * @param lateral Left/Right Movement
     * @param yaw Rotation
     * @param distance Distance to move (in inches)
     */
    public void moveRobotByDistance(double axial, double lateral, double yaw, double distance) {
        moveRobot(axial, lateral, yaw);

        if ((axial + lateral + yaw) == 0) return;
        if (distance == 0) return;

        // TODO: implement directionality
        final int DIRECTION = (distance < 0) ? -1 : 1;

        // Axial = y, Lateral = x, yaw = z
        double[] startingPosition = new double[3];
        startingPosition[0] = eL.getCurrentPosition() * INCHES_PER_TICK;
        startingPosition[1] = eB.getCurrentPosition() * INCHES_PER_TICK;
        startingPosition[2] = eR.getCurrentPosition() * INCHES_PER_TICK;

        while (lF.isBusy()) {
            double[] newPositions = new double[3];
            newPositions[0] =
                    ((eL.getCurrentPosition() * INCHES_PER_TICK) * DIRECTION) - startingPosition[0];
            newPositions[1] =
                    ((eB.getCurrentPosition() * INCHES_PER_TICK) * DIRECTION) - startingPosition[1];
            newPositions[2] =
                    ((eR.getCurrentPosition() * INCHES_PER_TICK) * DIRECTION) - startingPosition[2];

            if ((newPositions[0] + newPositions[1] + newPositions[2]) >= distance) {
                resetDriveMotors();
                return;
            }
        }
    }

    public void resetDriveMotors() {
        dbp.debug("Resetting Drive Motors...");
        lF.setPower(MIN_POWER);
        rF.setPower(MIN_POWER);
        lB.setPower(MIN_POWER);
        rB.setPower(MIN_POWER);
    }

    @Override
    public void periodic() {
        super.periodic();
    }
}
