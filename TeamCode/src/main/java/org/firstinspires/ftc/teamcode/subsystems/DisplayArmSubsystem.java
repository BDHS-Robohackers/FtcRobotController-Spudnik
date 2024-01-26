package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.function.DoubleSupplier;

public class DisplayArmSubsystem extends SubsystemBase {

    DcMotor arm1, arm2;

    public DisplayArmSubsystem(DcMotor arm1, DcMotor arm2) {
        this.arm1 = arm1;
        this.arm2 = arm2;
    }

    public enum DisplayArmType {
        ARM_1,
        ARM_2
    }

    public void moveArm(DisplayArmType type, double power) {
        if (type == DisplayArmType.ARM_1) {
            arm1.setPower(power);
            return;
        }
        if (type == DisplayArmType.ARM_2) {
            arm2.setPower(power);
        }
    }

}
