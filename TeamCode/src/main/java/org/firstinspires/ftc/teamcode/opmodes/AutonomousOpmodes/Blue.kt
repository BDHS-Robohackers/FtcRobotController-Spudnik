package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.MecanumDrive;

import org.firstinspires.ftc.teamcode.util.AutoTrajectories;
import org.firstinspires.ftc.teamcode.util.AutonomousActions;

@Config
@Autonomous(name = "BLUE_TEST_AUTO_PIXEL", group = "Autonomous")
public class Blue extends LinearOpMode {
    int visionOutputPosition = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPose = new Pose2d(7.00, -70.00, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
            if (isStopRequested()) return;
        }

        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.update();
        waitForStart();

        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .waitSeconds(4.605)
                .strafeTo(new Vector2d(initialPose.position.x, initialPose.position.y+5))
                .strafeTo(new Vector2d(initialPose.position.x + 5, initialPose.position.y+5));

        // Trajectories

        // AutonomousActions.EmergencyArm emergencyArm = new AutonomousActions.EmergencyArm(hardwareMap, telemetry);

        SequentialAction main = new AutoTrajectories.CompAutoTrajectorySequence(drive, hardwareMap).build();

        Actions.runBlocking(main);
    }
}
