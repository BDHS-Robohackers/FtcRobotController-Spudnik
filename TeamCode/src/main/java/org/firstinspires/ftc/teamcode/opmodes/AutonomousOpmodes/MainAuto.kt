package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.AutoTrajectories;

@Config
@Autonomous(name = "Main_Auto", group = "Autonomous", preselectTeleOp = "RealestDriverOpMode")
public class MainAuto extends LinearOpMode {
    int visionOutputPosition = 0;

    @Override
    public void runOpMode() {
        Pose2d initialPose = new Pose2d(7.00, -70.00, Math.toRadians(90.00));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
            if (isStopRequested()) return;
        }

        AutoTrajectories.CompAutoTrajectorySequence compAutoTrajectorySequence = new AutoTrajectories.CompAutoTrajectorySequence(drive, hardwareMap);
        SequentialAction action = compAutoTrajectorySequence.build();

        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.update();
        waitForStart();

        telemetry.addData("Sequence: ", action.getInitialActions().toString());
        System.out.println("Sequence: " + action.getInitialActions());
        telemetry.update();

        //action.run(new TelemetryPacket());
        Actions.runBlocking(compAutoTrajectorySequence.build());
    }
}
