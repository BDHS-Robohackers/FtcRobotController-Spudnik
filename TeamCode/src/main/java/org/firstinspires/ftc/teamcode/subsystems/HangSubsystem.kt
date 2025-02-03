package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

@Deprecated
public class HangSubsystem extends SubsystemBase {

    final DcMotor extensionMotor;
    private final FTCDashboardPackets dbp = new FTCDashboardPackets("Hang Subsystem");

    public HangSubsystem(HardwareMap hardwareMap) {
        this.extensionMotor = RobotHardwareInitializer.MotorComponent.HANG_MOTOR.get(hardwareMap);
    }

    public void setHangDirection(HangDirection direction) {
        if (extensionMotor == null) {
            return;
        }

        extensionMotor.setPower(direction == HangDirection.IDLE ? 0 : 1);
        // TODO: Verify if the Direction.FORWARD and REVERSE is correct (may need to reverse it)
        DcMotorSimple.Direction motorDirection = direction == HangDirection.DOWN ? DcMotorSimple.Direction.FORWARD : DcMotorSimple.Direction.REVERSE;
        extensionMotor.setDirection(motorDirection);

        dbp.createNewTelePacket();
        dbp.info(String.format("Hang Direction: %s", direction.toString()));
        dbp.send(false);
    }

    // Used to determine whether the motor will go up, down, or remain idle
    public enum HangDirection {
        UP,
        DOWN,
        IDLE;
    }

}
