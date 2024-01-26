package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.DisplayArmSubsystem;

import java.util.function.DoubleSupplier;

public class DefaultDisplayArmCommand extends CommandBase {
    private DoubleSupplier arm1Controls, arm2Controls;
    private DisplayArmSubsystem subsystem;

    public DefaultDisplayArmCommand(DisplayArmSubsystem subsystem, DoubleSupplier arm1, DoubleSupplier arm2) {
        this.arm1Controls = arm1;
        this.arm2Controls = arm2;
        addRequirements(subsystem);
    }

    @Override
    public void execute() {
        subsystem.moveArm(DisplayArmSubsystem.DisplayArmType.ARM_1, arm1Controls.getAsDouble());
        subsystem.moveArm(DisplayArmSubsystem.DisplayArmType.ARM_2, arm2Controls.getAsDouble());
    }
}
