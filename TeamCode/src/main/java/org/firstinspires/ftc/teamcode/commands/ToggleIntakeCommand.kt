package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem

@Deprecated("")
class ToggleIntakeCommand(var intakeSubsystem: IntakeSubsystem) : CommandBase() {
    init {
        addRequirements(intakeSubsystem)
    }

    override fun initialize() {
        super.initialize()
        intakeSubsystem.toggleIntakeState()
    }

    override fun isFinished(): Boolean {
        return true
    }
}
