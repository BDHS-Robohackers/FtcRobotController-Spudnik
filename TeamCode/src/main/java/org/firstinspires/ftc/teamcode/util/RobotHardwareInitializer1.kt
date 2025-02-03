package org.firstinspires.ftc.teamcode.util

import com.acmerobotics.roadrunner.DualNum.value
import com.acmerobotics.roadrunner.Pose2dDual.value
import com.acmerobotics.roadrunner.PoseVelocity2dDual.value
import com.acmerobotics.roadrunner.Rotation2dDual.value
import com.acmerobotics.roadrunner.Twist2dDual.value
import com.arcrobotics.ftclib.hardware.ServoEx
import com.arcrobotics.ftclib.hardware.SimpleServo
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.IMU
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.hardware.TouchSensor
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets

/**
 * The RobotHardwareInitializer abstracts the process of setting the hardware variables in RobotOpMode.
 * Using this class helps to increase readability inside of the RobotOpMode class.
 */
object RobotHardwareInitializer {
    private fun Error(e: Exception, opMode: OpMode) {
        val dbp = FTCDashboardPackets("RobotHardwareInit")
        dbp.createNewTelePacket()

        dbp.error(e, true, false)
        opMode.terminateOpModeNow()
    }

    const val MIN_POWER: Float = 0f

    /*public enum Component {
        LEFT_FRONT("fl_drv", DcMotor.class),
        RIGHT_FRONT("fr_drv", DcMotor.class),
        LEFT_BACK("bl_drv", DcMotor.class),
        RIGHT_BACK("br_drv", DcMotor.class),
        ;

        public final String componentName;
        public final Class<? extends HardwareDevice> clazz;

        Component(String componentName, Class<? extends HardwareDevice> clazz) {
            this.componentName = componentName;
            this.clazz = clazz;
        }

        public <T extends HardwareDevice> T cast(HardwareDevice device) {
            return clazz
        }
    }*/
    @Deprecated("")
    fun initializeDriveMotors(hMap: HardwareMap, opMode: OpMode): HashMap<Component<*>, DcMotor>? {
        val leftFrontDrive: DcMotor
        val rightFrontDrive: DcMotor
        val leftBackDrive: DcMotor
        val rightBackDrive: DcMotor
        try {
            leftFrontDrive = MotorComponent.RIGHT_FRONT.get(hMap)
            rightFrontDrive = MotorComponent.RIGHT_FRONT.get(hMap)
            leftBackDrive = MotorComponent.LEFT_BACK.get(hMap)
            rightBackDrive = MotorComponent.RIGHT_BACK.get(hMap)

            leftFrontDrive.direction = DcMotor.Direction.FORWARD
            leftBackDrive.direction = DcMotor.Direction.REVERSE
            rightFrontDrive.direction = DcMotor.Direction.FORWARD
            rightBackDrive.direction = DcMotor.Direction.FORWARD
        } catch (e: Exception) {
            Error(e, opMode)
            return null
        }

        val encoderLeft: DcMotorEx
        val encoderRight: DcMotorEx
        val encoderBack: DcMotorEx

        try {
            encoderLeft = EncoderComponent.ENCODER_PAR0.get(hMap)
            encoderRight = EncoderComponent.ENCODER_PAR1.get(hMap)
            encoderBack = EncoderComponent.ENCODER_PERP.get(hMap)
        } catch (e: Exception) {
            Error(e, opMode)
            return null
        }

        val motorMap = HashMap<Component<*>, DcMotor>()

        motorMap[MotorComponent.LEFT_FRONT] = leftFrontDrive
        motorMap[MotorComponent.RIGHT_FRONT] = rightFrontDrive
        motorMap[MotorComponent.LEFT_BACK] = leftBackDrive
        motorMap[MotorComponent.RIGHT_BACK] = rightBackDrive
        motorMap[EncoderComponent.ENCODER_PAR0] = encoderLeft
        motorMap[EncoderComponent.ENCODER_PAR0] = encoderRight
        motorMap[EncoderComponent.ENCODER_PERP] = encoderBack

        return motorMap
    }

    /** @noinspection rawtypes
     */
    /*public static HashMap<Other, DynamicTypeValue> initializeAllOtherSystems(final OpMode opMode) {
        HashMap<Other, DynamicTypeValue> out = new HashMap<>();

        // Init Color Sensor
        //out.put(Other.COLOR_SENSOR, new ColorSensorTypeValue(initializeColorSensor(opMode)));

        // Init Webcam
        HashMap<Cameras, WebcamName> tmp = initializeCamera(opMode);
        assert tmp != null;
        out.put(Other.WEBCAM, new ArrayTypeValue<>(tmp.values().toArray()));

        return out;
    }*/
    /*public static ColorSensor initializeColorSensor(final OpMode opMode) {
        try {
            return opMode.hardwareMap.get(ColorSensor.class, "color_sensor");
        } catch(Exception e) {
            Error(e, opMode);
        }
        return null;
    }

    public enum Cameras {
        CAM1,
        CAM2
    }

    public static HashMap<Cameras, WebcamName> initializeCamera(final OpMode opMode) {
        HashMap<Cameras, WebcamName> out = new HashMap<>();
        try {
            out.put(Cameras.CAM1, opMode.hardwareMap.get(WebcamName.class, "Webcam 1"));
            out.put(Cameras.CAM2, opMode.hardwareMap.get(WebcamName.class, "Webcam 2"));
            return out;
        } catch(Exception e) {
            Error(e, opMode);
        }
        return null;
    }*/

    interface Component<T : HardwareDevice?> {
        @Throws(Exception::class)
        fun get(map: HardwareMap): T
        val componentName: String
    }

    enum class MotorComponent(override val componentName: String) :
        Component<DcMotor?> {
        LEFT_FRONT("fl_drv"),
        RIGHT_FRONT("fr_drv"),
        LEFT_BACK("bl_drv"),
        RIGHT_BACK("br_drv"),

        @Deprecated("")
        LOWER_ARM("low_arm"),

        @Deprecated("")
        HIGHER_ARM("high_arm"),

        @Deprecated("")
        HANG_MOTOR("hang"),

        @Deprecated("")
        ARM("robo_arm"),
        INTAKE_MOTOR("intake"),
        UPPIES("uppies"),
        EXTENSION_VIPER("viper_extension"),
        ;

        override fun get(map: HardwareMap): DcMotor {
            return map.get(DcMotor::class.java, componentName)
        }

        @Throws(Exception::class)
        fun getEx(map: HardwareMap): DcMotorEx {
            return map.get(DcMotorEx::class.java, componentName)
        }
    }

    enum class IMUComponent(override val componentName: String) : Component<IMU?> {
        IMU("arm_imu");

        @Throws(Exception::class)
        override fun get(map: HardwareMap): IMU {
            return map.get<IMU>(IMU::class.java, componentName)
        }
    }

    enum class ServoComponent(override val componentName: String) :
        Component<Servo?> {
        @Deprecated("")
        PINCHER("pincher"),

        @Deprecated("")
        FINGER_1("finger1"),  // left finger

        @Deprecated("")
        FINGER_2("finger2"),  // right finger

        @Deprecated("")
        BUCKET_DUMPER("bucket"),  // Used to dump the bucket and return to the collecting position
        INTAKE_TILTER("intake_servo"),
        ; // Used to tilt the intake system toward the bucket at to the ground

        override fun get(map: HardwareMap): Servo {
            return map.get(Servo::class.java, componentName)
        }

        @Throws(Exception::class)
        fun getEx(map: HardwareMap, minAngle: Double, maxAngle: Double): ServoEx {
            return SimpleServo(map, componentName, minAngle, maxAngle)
        }

        @Throws(Exception::class)
        fun getEx(map: HardwareMap): ServoEx {
            return SimpleServo(map, componentName, 0.0, 0.0)
        }
    }

    enum class CRServoComponent(override val componentName: String) :
        Component<CRServo?> {
        @Deprecated("")
        WRIST("wrist"),
        INTAKE_TILTER("intake_servo"),
        ;

        override fun get(map: HardwareMap): CRServo {
            return map.get(
                CRServo::class.java,
                componentName
            )
        }
    }

    enum class EncoderComponent(override val componentName: String) :
        Component<DcMotorEx?> {
        ENCODER_PAR0("fl_drv"),  // previously fr_drv, left
        ENCODER_PAR1("fr_drv"),  // previously intake, right
        ENCODER_PERP("br_drv"); // previously extender, back

        override fun get(map: HardwareMap): DcMotorEx {
            return map.get(DcMotorEx::class.java, componentName)
        }
    }

    enum class TouchSensorComponent(override val componentName: String) :
        Component<TouchSensor?> {
        ;

        override fun get(map: HardwareMap): TouchSensor {
            return map.get(TouchSensor::class.java, componentName)
        }
    }

    enum class ColorV3SensorComponent(override val componentName: String) :
        Component<ColorSensor?> {
        ;

        @Throws(Exception::class)
        override fun get(map: HardwareMap): ColorSensor {
            return map.get(ColorSensor::class.java, componentName)
        }
    }

    enum class DistanceSensorComponent(override val componentName: String) :
        Component<DistanceSensor?> {
        // EXTENDER_SENSOR("extender_color_sensor"),
        RIGHT_SENSOR("right_distance"),
        CENTER_SENSOR("center_distance"),
        LEFT_SENSOR("left_distance");

        override fun get(map: HardwareMap): DistanceSensor {
            return map.get(DistanceSensor::class.java, componentName)
        }
    }
}
