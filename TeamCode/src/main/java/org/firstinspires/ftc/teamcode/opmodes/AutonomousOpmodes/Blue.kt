package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.SequentialAction
import com.acmerobotics.roadrunner.Vector2d
import com.acmerobotics.roadrunner.ftc.runBlocking
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.AutonomousActions.EmergencyArm

@Config
@Autonomous(name = "BLUE_TEST_AUTO_PIXEL", group = "Autonomous")
class Blue : LinearOpMode() {
    private var visionOutputPosition: Int = 0

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
        val emergencyArm = EmergencyArm(hardwareMap, telemetry)

        val red = drive.actionBuilder(initialPose)
            .lineToY(-35.0) // (7, -25) 90 deg
            // Put Specimen on rung
            .afterTime(
                .5, SequentialAction(
                    emergencyArm.openPincher()
                )
            )
            .strafeTo(Vector2d(50.00, -35.00)) // (50, -35) 90 deg
            // Grab 1st sample
            .waitSeconds(3.0)
            .strafeToLinearHeading(
                Vector2d(50.0, -60.0),
                Math.toRadians(-90.0)
            ) // (50, -60) -90 deg
            // Drop off 1st sample
            .waitSeconds(3.0)
            .turnTo(Math.toRadians(90.0)) // 90 deg
            .strafeTo(Vector2d(60.0, -60.00)) // (60, -60) 90 deg
            .strafeTo(Vector2d(60.0, -35.00)) // (60, -35) 90 deg
            // Grab 2nd Sample
            .waitSeconds(3.0)
            .strafeToLinearHeading(
                Vector2d(60.0, -60.0),
                Math.toRadians(-90.0)
            ) // (60, -60) -90 deg
            // Drop off Second Sample
            .waitSeconds(3.0)
            .strafeTo(Vector2d(48.00, -52.00)) // (48, -52)
            // Wait for human player to put specimen on wall
            .waitSeconds(3.0)
            .strafeTo(Vector2d(48.0, -60.0)) // (48, -80) -90 deg
            // Move and pickup specimen
            .afterTime(.5, emergencyArm.closePincher())
            .waitSeconds(3.0)
            .turnTo(Math.toRadians(90.0))
            .strafeTo(Vector2d(7.00, -35.0)) // Put specimen on rung
            .afterTime(
                .5, SequentialAction(
                    emergencyArm.openPincher()
                )
            )
            .waitSeconds(3.0)

        runBlocking(red.build())
    }
}
