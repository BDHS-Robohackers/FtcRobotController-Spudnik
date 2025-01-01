package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.util.RandomUtils.withinThreshold;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

@Config
public class ArmSubsystem extends SubsystemBase {
    private final DcMotorEx ARM_MOTOR;
    private final FTCDashboardPackets dbp = new FTCDashboardPackets(this);

    static int MAX_ENCODER_POS = 1000;
    static int MIN_ENCODER_POS = 0;
    static int ENCODER_THRESHOLD = 5;
    static boolean useExternalEncoder = true;
    static boolean EMERGENCY_OVERRIDE = false;
    static double HOLD_POWER = 1;
    static boolean HOLD_ARM = true;

    public ArmSubsystem(final DcMotorEx armMotor) {
        ARM_MOTOR = armMotor;

        if (!useExternalEncoder) {
            ARM_MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            ARM_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        dbp.info(FTCDashboardPackets.CommonPackets.INIT);
    }

    public ArmSubsystem(final HardwareMap map) throws Exception {
        ARM_MOTOR = RobotHardwareInitializer.MotorComponent.ARM.getEx(map);

        if (!useExternalEncoder) {
            ARM_MOTOR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            ARM_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        dbp.info(FTCDashboardPackets.CommonPackets.INIT);
    }

    public void setPower(final double power) {
        if (
                ((withinThreshold(ARM_MOTOR.getCurrentPosition(), MAX_ENCODER_POS, ENCODER_THRESHOLD) && power > 0) ||
                (withinThreshold(ARM_MOTOR.getCurrentPosition(), MIN_ENCODER_POS, ENCODER_THRESHOLD) && power < 0)) &&
                !EMERGENCY_OVERRIDE
        ) {
            dbp.warn("Arm Motor attempted to go past set limits, stopping...");
            zeroPower();
            return;
        }

        if (power == 0) {
            zeroPower();
            return;
        }
        else
            nonZeroPower();

        ARM_MOTOR.setPower(power);
    }

    public void zeroPower() {
        if (!HOLD_ARM) {
            nonZeroPower();
            ARM_MOTOR.setPower(0);
        }
        if (ARM_MOTOR.getMode() == DcMotor.RunMode.RUN_TO_POSITION)
            return;
        ARM_MOTOR.setTargetPosition(ARM_MOTOR.getCurrentPosition());
        ARM_MOTOR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ARM_MOTOR.setPower(HOLD_POWER);
    }

    public void nonZeroPower() {
        if (ARM_MOTOR.getMode() == DcMotor.RunMode.RUN_USING_ENCODER)
            return;
        ARM_MOTOR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void periodic() {
        super.periodic();
        if (EMERGENCY_OVERRIDE)
            dbp.warn("EMERGENCY OVERRIDE IS ACTIVE!");
        dbp.info("Current Arm Motor Pos: " + ARM_MOTOR.getCurrentPosition());
        dbp.send(true);
    }
}
