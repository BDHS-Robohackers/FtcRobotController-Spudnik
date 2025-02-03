package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.ExtendoSystem

class ExtendoCommand(var subsystem: ExtendoSystem, var direction: ExtendoSystem.Direction) :
    CommandBase() {
    init {
        addRequirements(subsystem)
    }

    override fun isFinished(): Boolean {
        return true
    }

    override fun initialize() {
        subsystem.setDirection(direction)
    }
}
