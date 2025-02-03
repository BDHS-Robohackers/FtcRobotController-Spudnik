package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.roadrunner.Pose2d
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.MatchRecorder.MatchLogger

class MatchLoggerSubsystem(hardwareMap: HardwareMap?, POSE_ESTIMATE: Pose2d?) :
    SubsystemBase() {
    var mecanumDrive: Unit? =
        hardwareMap?.let {
            if (POSE_ESTIMATE != null) {
                MecanumDrive(it, POSE_ESTIMATE)
            }
        }
    val MATCH_LOGGER: MatchLogger = MatchLogger.instance

    override fun periodic() {
        // mecanumDrive.()
        // MATCH_LOGGER.logRobotPose(mecanumDrive.pose)
        // NOTE: Some of the subsystems (like the ArmSubsystem) already has the logging system implemented in the code.
        // Remember to not log things twice.
        super.periodic()
    }
}
