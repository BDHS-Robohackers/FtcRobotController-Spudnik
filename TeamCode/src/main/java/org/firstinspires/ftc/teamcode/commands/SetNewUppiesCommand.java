package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.UppieTwoSubsystem;

public class SetNewUppiesCommand extends CommandBase {
    UppieTwoSubsystem uppieTwoSubsystem;
    UppieTwoSubsystem.UppieState state;


    public SetNewUppiesCommand(UppieTwoSubsystem subsystem, UppieTwoSubsystem.UppieState state) {
        super();
        this.uppieTwoSubsystem = subsystem;
        this.state = state;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        super.initialize();
        uppieTwoSubsystem.setUppieState(state);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
