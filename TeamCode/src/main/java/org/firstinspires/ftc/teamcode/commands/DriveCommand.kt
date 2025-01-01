package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem

class DriveCommand(
    subsystem: DriveSubsystem, forwardBackward: Double,
    leftRight: Double, rotation: Double
) : CommandBase() {
    private val forwardBackward = 0.0
    private val leftRight = 0.0
    private val rotation = 0.0
    private val mDrive: DriveSubsystem

    init {
        var forwardBackward = forwardBackward
        var leftRight = leftRight
        var rotation = rotation
        mDrive = subsystem
        forwardBackward = forwardBackward
        leftRight = leftRight
        rotation = rotation
        addRequirements(subsystem)
    }

    override fun execute() {
        mDrive.moveRobotMecanum(forwardBackward, leftRight, rotation)
    }

    override fun isFinished(): Boolean {
        return true
    }
}
