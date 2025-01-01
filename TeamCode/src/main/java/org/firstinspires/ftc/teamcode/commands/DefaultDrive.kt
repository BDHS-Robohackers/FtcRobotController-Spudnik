package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.hardware.Gamepad
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem
import java.util.function.DoubleSupplier

class DefaultDrive : CommandBase {
    private var forwardBackwardSupplier: DoubleSupplier? = null
    private var leftRightSupplier: DoubleSupplier? = null
    private var rotationSupplier: DoubleSupplier? = null
    private val m_drive: DriveSubsystem
    private var gamepad1: Gamepad? = null

    @Deprecated("")
    constructor(
        subsystem: DriveSubsystem, forwardBackward: DoubleSupplier?,
        leftRight: DoubleSupplier?, rotation: DoubleSupplier?
    ) {
        m_drive = subsystem
        forwardBackwardSupplier = forwardBackward
        leftRightSupplier = leftRight
        rotationSupplier = rotation
        addRequirements(subsystem)
    }

    constructor(subsystem: DriveSubsystem, gamepad: Gamepad?) {
        m_drive = subsystem
        addRequirements(subsystem)
        gamepad1 = gamepad
    }

    override fun execute() {
        m_drive.moveRobot(gamepad1)
    }

    override fun isFinished(): Boolean {
        return false
    }
}
