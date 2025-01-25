package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExtendoSystem extends SubsystemBase {

    final DcMotorEx motor/*, reverseMotor*/;
    private final static FTCDashboardPackets dbp = new FTCDashboardPackets("ExtendoSubsystem");
    public ExtendoSystem(HardwareMap hardwareMap) throws Exception {
        this.motor = RobotHardwareInitializer.MotorComponent.EXTENSION_VIPER.getEx(hardwareMap);
        //this.reverseMotor = reverseMotor;
    }

    public enum Direction {
        OUTWARD,
        INWARD,
        NONE
    }

    public void setDirection(@NotNull Direction direction) {
        Objects.requireNonNull(direction);
        double power = (direction == Direction.OUTWARD ? 1 : 0) - (direction == Direction.INWARD ? 1 : 0);
        motor.setPower(power);
        //reverseMotor.setPower(-power);
        dbp.info("Direction: "+direction);
        dbp.send(true);
    }

}
