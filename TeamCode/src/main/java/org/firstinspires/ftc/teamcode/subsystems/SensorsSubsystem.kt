package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.hardware.RevIMU
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer

@Config
class SensorsSubsystem(hardwareMap: HardwareMap) : SubsystemBase() {
    private val imu_dbp = FTCDashboardPackets(this)

    var imu: RevIMU? = null; // RobotHardwareInitializer.IMUComponent.IMU[hardwareMap] as RevIMU

    init {
        imu?.init(parameters)
    }

    fun debugIMU() {
        imu_dbp.debug("IMU Angles: " + imu?.angles.contentToString())
        imu_dbp.debug("Absolute Heading: " + (imu?.absoluteHeading ))
        imu_dbp.debug("Rotation2D: " + (imu?.rotation2d ))
        imu_dbp.send(true)
    }

    override fun periodic() {
        super.periodic()
        debugIMU()
    }

    companion object {
        var parameters: BNO055IMU.Parameters = BNO055IMU.Parameters()
    }
}
