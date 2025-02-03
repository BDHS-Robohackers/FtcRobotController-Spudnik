package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem

@Deprecated("")
class TiltIntakeCommand(var intakeSubsystem: IntakeSubsystem, var tilt: Boolean) : CommandBase() {
    init {
        addRequirements(intakeSubsystem)
    }

    override fun isFinished(): Boolean {
        return true
    }

    override fun initialize() {
        super.initialize()
        if (tilt) {
            intakeSubsystem.tiltIntake()
        } else {
            intakeSubsystem.untiltIntake()
        }
    }
}
