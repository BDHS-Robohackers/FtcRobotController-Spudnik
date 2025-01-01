package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.PincherSubsystem;

@Deprecated
public class MovePincherCommand extends CommandBase {

    PincherSubsystem subsystem;
    PincherSubsystem.FingerPositions position;
    boolean ran = false;

    public MovePincherCommand(PincherSubsystem subsystem, PincherSubsystem.FingerPositions position) {
        this.subsystem = subsystem;
        this.position = position;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        subsystem.locomoteFinger(position);
        ran = true;
    }

    @Override
    public boolean isFinished() {
        return ran;
    }

}
