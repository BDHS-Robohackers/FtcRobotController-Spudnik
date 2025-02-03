package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.AutoTrajectories.CompAutoTrajectorySequence

@Config
@Autonomous(name = "Main_Auto", group = "Autonomous", preselectTeleOp = "RealestDriverOpMode")
class MainAuto : LinearOpMode() {
    var visionOutputPosition: Int = 0

    override fun runOpMode() {
        val initialPose = Pose2d(7.00, -70.00, Math.toRadians(90.00))
        val drive = MecanumDrive(hardwareMap, initialPose)

        while (!isStopRequested && !opModeIsActive()) {
            val position = visionOutputPosition
            telemetry.addData("Position during Init", position)
            telemetry.update()
            if (isStopRequested) return
        }

        val compAutoTrajectorySequence = CompAutoTrajectorySequence(drive, hardwareMap)
        val action = compAutoTrajectorySequence.build()

        val startPosition = visionOutputPosition
        telemetry.addData("Starting Position", startPosition)
        telemetry.update()
        waitForStart()

        telemetry.addData("Sequence: ", action.initialActions.toString())
        println("Sequence: " + action.initialActions)
        telemetry.update()

        //action.run(new TelemetryPacket());
        runBlocking(compAutoTrajectorySequence.build())
    }
}
