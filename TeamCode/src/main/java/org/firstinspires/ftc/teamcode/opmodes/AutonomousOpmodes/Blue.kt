package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.AutoTrajectories.CompAutoTrajectorySequence

@Config
@Autonomous(name = "BLUE_TEST_AUTO_PIXEL", group = "Autonomous")
class Blue : LinearOpMode() {
    var visionOutputPosition: Int = 0

    @Throws(InterruptedException::class)
    override fun runOpMode() {
        val initialPose = Pose2d(7.00, -70.00, Math.toRadians(90.00))
        val drive = MecanumDrive(hardwareMap, initialPose)

        while (!isStopRequested && !opModeIsActive()) {
            val position = visionOutputPosition
            telemetry.addData("Position during Init", position)
            telemetry.update()
            if (isStopRequested) return
        }

        val startPosition = visionOutputPosition
        telemetry.addData("Starting Position", startPosition)
        telemetry.update()
        waitForStart()

        val tab1 = drive.actionBuilder(initialPose)
            .waitSeconds(4.605)
            .strafeTo(Vector2d(initialPose.position.x, initialPose.position.y + 5))
            .strafeTo(Vector2d(initialPose.position.x + 5, initialPose.position.y + 5))

        // Trajectories

        // AutonomousActions.EmergencyArm emergencyArm = new AutonomousActions.EmergencyArm(hardwareMap, telemetry);
        val main = CompAutoTrajectorySequence(drive, hardwareMap).build()

        runBlocking(main)
    }
}
