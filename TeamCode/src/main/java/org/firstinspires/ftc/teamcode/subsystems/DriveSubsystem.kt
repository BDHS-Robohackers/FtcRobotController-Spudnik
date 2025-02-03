package org.firstinspires.ftc.teamcode.subsystems

import android.annotation.SuppressLint
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.PoseVelocity2d
import com.acmerobotics.roadrunner.Vector2d
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.MecanumDrive
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer
import kotlin.math.abs
import kotlin.math.max

class DriveSubsystem : SubsystemBase {
    private var lF: DcMotor? = null
    private var rF: DcMotor? = null
    private var lB: DcMotor? = null
    private var rB: DcMotor? = null
    private var eL: DcMotor? = null
    private var eB: DcMotor? = null
    private var eR: DcMotor? = null
    private val INCHES_PER_TICK = 0.0018912
    var elapsedTime: ElapsedTime? = null
    private val dbp = FTCDashboardPackets("DriveSubsystem")
    var drive: MecanumDrive? = null

    @Deprecated("")
    constructor(driveMotors: HashMap<RobotHardwareInitializer.Component<*>?, DcMotor?>) : this(
        driveMotors[RobotHardwareInitializer.MotorComponent.LEFT_FRONT],
        driveMotors[RobotHardwareInitializer.MotorComponent.RIGHT_FRONT],
        driveMotors[RobotHardwareInitializer.MotorComponent.LEFT_BACK],
        driveMotors[RobotHardwareInitializer.MotorComponent.RIGHT_BACK],
        driveMotors[RobotHardwareInitializer.EncoderComponent.ENCODER_PAR0],
        driveMotors[RobotHardwareInitializer.EncoderComponent.ENCODER_PERP],
        driveMotors[RobotHardwareInitializer.EncoderComponent.ENCODER_PAR1]
    )

    constructor(hMap: HardwareMap?) {
        drive = hMap?.let { MecanumDrive(it, Pose2d(0.0, 0.0, 0.0)) }
    }

    constructor(
        leftFront: DcMotor?, rightFront: DcMotor?,
        leftBack: DcMotor?, rightBack: DcMotor?,
        encoderLeft: DcMotor?, encoderBack: DcMotor?,
        encoderRight: DcMotor?
    ) {
        dbp.createNewTelePacket()
        dbp.info(leftFront.toString() + ", " + leftBack + ", " + rightFront + ", " + rightBack)
        dbp.send(false)

        lF = leftFront
        rF = rightFront
        lB = leftBack
        rB = rightBack

        eL = encoderLeft
        eB = encoderBack
        eR = encoderRight

        elapsedTime = ElapsedTime(ElapsedTime.Resolution.MILLISECONDS)
    }

    @SuppressLint("DefaultLocale")
    @Deprecated("")
    fun moveRobot(axial: Double, lateral: Double, yaw: Double): Boolean {
        dbp.createNewTelePacket()
        dbp.info("Moving robot in subsystem: $axial, $lateral, $yaw")
        dbp.send(true)

        var max: Double

        var leftFrontPower = axial - lateral - yaw
        var rightFrontPower = axial + lateral + yaw
        var leftBackPower = axial + lateral - yaw
        var rightBackPower = axial - lateral + yaw

        dbp.createNewTelePacket()

        dbp.debug("LF: " + String.format("%f\n", leftFrontPower), false)
        dbp.debug("RF: " + String.format("%f\n", rightFrontPower), false)
        dbp.debug("LB: " + String.format("%f\n", leftBackPower), false)
        dbp.debug("RB: " + String.format("%f\n", rightBackPower), true)

        max = max(abs(leftFrontPower), abs(rightFrontPower))
        max = max(max, abs(leftBackPower))
        max = max(max, abs(rightBackPower))

        if (max > 1.0) {
            leftFrontPower /= max
            rightFrontPower /= max
            leftBackPower /= max
            rightBackPower /= max
        }
        lF!!.power = leftFrontPower
        rF!!.power = rightFrontPower
        lB!!.power = leftBackPower
        rB!!.power = rightBackPower
        return true
    }

    fun moveRobotMecanum(forwardBackward: Double, leftRight: Double, rotation: Double) {
        drive!!.setDrivePowers(
            PoseVelocity2d(
                Vector2d(
                    forwardBackward,
                    -leftRight
                ),
                -rotation
            )
        )

        drive!!.updatePoseEstimate()
    }

    fun moveRobot(gamepad1: Gamepad) {
        /*drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x
                ),
                -gamepad1.right_stick_x
        ));*/
        drive!!.setDrivePowers(
            PoseVelocity2d(
                Vector2d(
                    -gamepad1.left_stick_y.toDouble(),
                    -(gamepad1.right_trigger - gamepad1.left_trigger).toDouble()
                ),
                -gamepad1.right_stick_x.toDouble()
            )
        )

        drive!!.updatePoseEstimate()
    }

    /**
     * Moves the robot by a specified distance
     * @param axial Forward/Backward Movement
     * @param lateral Left/Right Movement
     * @param yaw Rotation
     * @param distance Distance to move (in inches)
     */
    fun moveRobotByDistance(axial: Double, lateral: Double, yaw: Double, distance: Double) {
        moveRobot(axial, lateral, yaw)

        if ((axial + lateral + yaw) == 0.0) return
        if (distance == 0.0) return

        // TODO: implement directionality
        val DIRECTION = if ((distance < 0)) -1 else 1

        // Axial = y, Lateral = x, yaw = z
        val startingPosition = DoubleArray(3)
        startingPosition[0] = eL!!.currentPosition * INCHES_PER_TICK
        startingPosition[1] = eB!!.currentPosition * INCHES_PER_TICK
        startingPosition[2] = eR!!.currentPosition * INCHES_PER_TICK

        while (lF!!.isBusy) {
            val newPositions = DoubleArray(3)
            newPositions[0] =
                ((eL!!.currentPosition * INCHES_PER_TICK) * DIRECTION) - startingPosition[0]
            newPositions[1] =
                ((eB!!.currentPosition * INCHES_PER_TICK) * DIRECTION) - startingPosition[1]
            newPositions[2] =
                ((eR!!.currentPosition * INCHES_PER_TICK) * DIRECTION) - startingPosition[2]

            if ((newPositions[0] + newPositions[1] + newPositions[2]) >= distance) {
                resetDriveMotors()
                return
            }
        }
    }

    fun resetDriveMotors() {
        dbp.debug("Resetting Drive Motors...")
        lF!!.power = RobotHardwareInitializer.MIN_POWER.toDouble()
        rF!!.power = RobotHardwareInitializer.MIN_POWER.toDouble()
        lB!!.power = RobotHardwareInitializer.MIN_POWER.toDouble()
        rB!!.power = RobotHardwareInitializer.MIN_POWER.toDouble()
    }

    override fun periodic() {
        super.periodic()
    }
}
