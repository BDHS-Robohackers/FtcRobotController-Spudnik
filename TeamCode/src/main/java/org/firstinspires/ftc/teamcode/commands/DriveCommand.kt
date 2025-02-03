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
    private val m_drive: DriveSubsystem

    init {
        var forwardBackward = forwardBackward
        var leftRight = leftRight
        var rotation = rotation
        m_drive = subsystem
        forwardBackward = forwardBackward
        leftRight = leftRight
        rotation = rotation
        addRequirements(subsystem)
    }

    override fun execute() {
        m_drive.moveRobotMecanum(forwardBackward, leftRight, rotation)
    }

    override fun isFinished(): Boolean {
        return true
    }
}
