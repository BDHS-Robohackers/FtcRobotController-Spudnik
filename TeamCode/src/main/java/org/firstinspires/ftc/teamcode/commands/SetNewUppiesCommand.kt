package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem
import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem.UppieState

class SetNewUppiesCommand(var uppieTwoSubsystem: UppieTwoSubsystem, var state: UppieState) :
    CommandBase() {
    init {
        addRequirements(uppieTwoSubsystem)
    }

    override fun initialize() {
        super.initialize()
        uppieTwoSubsystem.setUppieState(state)
    }

    override fun isFinished(): Boolean {
        return true
    }
}
