package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.RevIMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

import java.util.Arrays;

@Config
public class SensorsSubsystem extends SubsystemBase {
    static BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

    private final FTCDashboardPackets imu_dbp = new FTCDashboardPackets(this);

    public RevIMU imu;

    public SensorsSubsystem(HardwareMap hardwareMap) throws Exception {
        imu = (RevIMU) RobotHardwareInitializer.IMUComponent.IMU.get(hardwareMap);
        imu.init(parameters);
    }

    public void debugIMU() {
        imu_dbp.debug("IMU Angles: " + Arrays.toString(imu.getAngles()));
        imu_dbp.debug("Absolute Heading: " + imu.getAbsoluteHeading());
        imu_dbp.debug("Rotation2D: " + imu.getRotation2d());
        imu_dbp.send(true);
    }

    @Override
    public void periodic() {
        super.periodic();
        debugIMU();

    }
}
