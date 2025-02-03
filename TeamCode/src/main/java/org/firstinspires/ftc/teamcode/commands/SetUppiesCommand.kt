package org.firstinspires.ftc.teamcode.commands

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.UppiesSubsystem
import org.firstinspires.ftc.teamcode.subsystems.UppiesSubsystem.UppiesState
import java.util.function.BooleanSupplier

@Deprecated("")
class SetUppiesCommand(var uppiesSubsystem: UppiesSubsystem, var state: UppiesState) :
    CommandBase() {
    var buttonPressedSupplier: BooleanSupplier? = null

    init {
        addRequirements(uppiesSubsystem)
    }

    override fun initialize() {
        super.initialize()
        uppiesSubsystem.setUppiesState(state)
    }

    override fun isFinished(): Boolean {
        return true
    }
}
