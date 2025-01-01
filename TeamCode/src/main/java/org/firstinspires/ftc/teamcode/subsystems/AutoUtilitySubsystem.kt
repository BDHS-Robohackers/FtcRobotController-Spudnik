package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.roadrunner.Pose2d
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer.DistanceSensorComponent
import kotlin.math.abs

class AutoUtilitySubsystem(hardwareMap: HardwareMap?) : SubsystemBase() {
    val LEFT: DistanceSensor = DistanceSensorComponent.LEFT_SENSOR.get(hardwareMap!!)
    val CENTER: DistanceSensor = DistanceSensorComponent.CENTER_SENSOR.get(hardwareMap!!)
    val RIGHT: DistanceSensor = DistanceSensorComponent.RIGHT_SENSOR.get(hardwareMap!!)
    val TARGET: Double = 1.0
    val UNIT: DistanceUnit = DistanceUnit.CM


    private var error: Double = 0.0
    var distance: Double = 0.0

    private var DL = 0.0
    private var DR = 0.0
    private var DC = 0.0

    private lateinit var robotPose: Pose2d

    fun setRobotPose(pose: Pose2d) {
        robotPose = pose
    }

    override fun periodic() {
        DR = RIGHT.getDistance(UNIT)
        DL = LEFT.getDistance(UNIT)
        DC = CENTER.getDistance(UNIT)
        if (abs(error) > TARGET) {
            error = DL - DR
            if (error > 0) {
                turnRight()
            } else if (error < 0) {
                turnLeft()
            }
        } else if ((((DL + DR) / 2) > (DC + 1)) || ((DL + DR) / 2) < (DC - 1)) {
            if (((DL + DR) / 2) > DC) {
                driveForward()
            } else if (((DL + DR) / 2) < DC) {
                driveBackward()
            }
        }
    }

    fun turnRight() {
    }

    fun turnLeft() {
    }

    fun driveForward() {
    }

    fun driveBackward() {
    }
}
