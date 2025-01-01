package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

public class IntakeSubsystem extends SubsystemBase {

    ServoEx intakeServo;
    private final static FTCDashboardPackets dbp = new FTCDashboardPackets("IntakeSubsystem");

    public IntakeSubsystem(HardwareMap map) throws Exception {
        this.intakeServo = RobotHardwareInitializer.ServoComponent.INTAKE.getEx(map);
    }

    public void kickSampleOut() {
        // Might need to add a delay between these two
        this.intakeServo.setPosition(1);
    }

    public void reset() {
        this.intakeServo.setPosition(0);
    }

    @Override
    public void periodic() {
        super.periodic();
    }
}
