package org.firstinspires.ftc.teamcode.opmodes.AutonomousOpmodes

import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.RunCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.commands.DefaultDrive
import org.firstinspires.ftc.teamcode.commands.DriveCommand
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer

@Autonomous(name = "Simple Park (WIP)", group = "Autonomous")
class MoveAutonomous : CommandOpMode() {
    var driveSubsystem: DriveSubsystem? = null
    var pincherSubsystem: PincherSubsystem? = null
    var driveCommand: DefaultDrive? = null

    override fun initialize() {
        try {
            driveSubsystem = DriveSubsystem(hardwareMap)
            register(driveSubsystem)
            driveCommand = DefaultDrive(driveSubsystem!!, gamepad1)
            //driveSubsystem.setDefaultCommand(driveCommand);
        } catch (e: Exception) {
            e.printStackTrace()
            requestOpModeStop()
            return
        }

        try {
            val pincher1 = RobotHardwareInitializer.ServoComponent.FINGER_1.getEx(
                hardwareMap,
                0.0,
                PincherSubsystem.MAX_ANGLE.toDouble()
            )
            val pincher2 = RobotHardwareInitializer.ServoComponent.FINGER_2.getEx(
                hardwareMap,
                0.0,
                PincherSubsystem.MAX_ANGLE.toDouble()
            )
            pincherSubsystem = PincherSubsystem(pincher1, pincher2)
            register(pincherSubsystem)
        } catch (e: Exception) {
            e.printStackTrace()
            requestOpModeStop()
            return
        }

        waitForStart()

        val commandGroup: SequentialCommandGroup = object : SequentialCommandGroup(
            WaitCommand(2605),  // pause before moving
            RunCommand({
                telemetry.addData("ITS MOVING NOW", "YAY")
                telemetry.update()
            }),
            DriveCommand(driveSubsystem!!, 0.0, .5, 0.0),
            WaitCommand(250),  // move away from wall for a bit
            DriveCommand(driveSubsystem!!, 0.0, 0.0, 0.0),
            WaitCommand(500),  // wait before moving forward
            DriveCommand(driveSubsystem!!, 1.0, 0.0, 0.0),
            WaitCommand(500),
            DriveCommand(driveSubsystem!!, 0.0, 0.0, 0.0)
        ) {
            override fun isFinished(): Boolean {
                return false
            }
        }
        driveSubsystem!!.defaultCommand = commandGroup
        pincherSubsystem!!.closeFinger()
    }
}
