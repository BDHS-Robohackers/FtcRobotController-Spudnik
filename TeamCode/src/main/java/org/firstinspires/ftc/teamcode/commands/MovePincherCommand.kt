package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem
import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem.FingerPositions

@Deprecated("")
class MovePincherCommand(var subsystem: PincherSubsystem, var position: FingerPositions) :
    CommandBase() {
    private var ran: Boolean = false

    init {
        addRequirements(subsystem)
    }

    override fun execute() {
        subsystem.locomoteFinger(position)
        ran = true
    }

    override fun isFinished(): Boolean {
        return ran
    }
}
