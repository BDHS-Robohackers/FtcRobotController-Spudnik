package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

@Config
public class IntakeSubsystem extends SubsystemBase {

    DcMotorEx powerMotor;
    ServoEx tilterServo;
    boolean currentState = false;
    final double MAX_POWER = 0.8;

    public static double INTAKE_POSITION_PADDING = 0; // "padding" from the max position of the servo

    public IntakeSubsystem(HardwareMap hardwareMap) throws Exception {
        this.powerMotor = RobotHardwareInitializer.MotorComponent.INTAKE_MOTOR.getEx(hardwareMap);
        this.tilterServo = RobotHardwareInitializer.ServoComponent.INTAKE_TILTER.getEx(hardwareMap);
        this.tilterServo.setInverted(true);
        tilterServo.setRange(0, 45);
    }

    public void tiltIntake() {
        tilterServo.setPosition(1f);
    }

    public void untiltIntake() {
        tilterServo.setPosition(0f);
    }

    public void setIntakeState(boolean activated, boolean reversed) {
        if (!reversed)
            powerMotor.setPower(activated ? MAX_POWER : 0);
        else
            powerMotor.setPower(activated ? -MAX_POWER : 0);
        currentState = activated;
    }

    public void toggleIntakeState() {
        setIntakeState(!currentState, false);
    }
    public void reverseIntake() {setIntakeState(!currentState, true);}
    public boolean getIntakeState() {
        return currentState;
    }
}
