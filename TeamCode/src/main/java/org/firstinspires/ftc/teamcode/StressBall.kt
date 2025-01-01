package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.Range
import kotlin.math.abs

/**
 * Created by Jessica on 4/10/2018.
 */
@TeleOp(name = "StressBall", group = "TOComp")
class StressBall : OpMode() {
    private val PULSE_PER_REVOLUTION_NEVEREST40 = 1120

    private val PULSE_PER_REVOLUTION_NEVEREST40_OVER_40 = 280

    private lateinit var frontLeft: DcMotor
    private lateinit var frontRight: DcMotor
    private lateinit var backLeft: DcMotor
    private lateinit var backRight: DcMotor

    //
    private val FeedMotor: CRServo? = null

    private val TurnServo: Servo? = null

    private lateinit var ContinuousShooter: CRServo

    private lateinit var SmallContinuous: CRServo

    var SERVO_LATCH_UP: Float = 1.0.toFloat()
    var SERVO_STOP: Float = 0f

    private val FeedSpeed = 0.05.toFloat()

    val CLAW_SPEED: Double = 0.01 // sets rate to move servo
    val ARM_SPEED: Double = 0.01 // sets rate to move servo


    override fun init() {
        frontLeft = hardwareMap.dcMotor["fl_drv"]
        frontRight = hardwareMap.dcMotor["fr_drv"]
        backLeft = hardwareMap.dcMotor["bl_drv"]
        backRight = hardwareMap.dcMotor["br_drv"]

        //
        //FeedMotor = hardwareMap.crservo.get("FeedMotor");
        ContinuousShooter = hardwareMap.crservo["ContinuousShooter"]
        SmallContinuous = hardwareMap.crservo["SmallContinuous"]

        frontLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        frontRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        backLeft.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        backRight.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        backLeft.direction = DcMotorSimple.Direction.FORWARD
        frontLeft.direction = DcMotorSimple.Direction.FORWARD
        frontRight.direction = DcMotorSimple.Direction.REVERSE
        backRight.direction = DcMotorSimple.Direction.REVERSE

        //        FeedMotor.setDirection(FORWARD);
        telemetry.update()
    }

    override fun init_loop() {
        super.init_loop()
        //FeedMotor.setMode(RUN_TO_POSITION);
    }


    override fun loop() {
        val speed = -gamepad1.right_stick_y
        val direction = gamepad1.right_stick_x
        val strafe = gamepad1.left_stick_x

        var Magnitude = (abs(speed.toDouble()) + abs(direction.toDouble()) + abs(
            strafe.toDouble()
        )).toFloat()
        if (Magnitude < 1) {
            Magnitude = 1f
        }
        frontLeft.power = Range.scale(
            (speed + direction - strafe).toDouble(),
            -Magnitude.toDouble(),
            Magnitude.toDouble(),
            -1.0,
            1.0
        )
        frontRight.power = Range.scale(
            (speed - direction + strafe).toDouble(),
            -Magnitude.toDouble(),
            Magnitude.toDouble(),
            -1.0,
            1.0
        )
        backLeft.power = Range.scale(
            (speed + direction + strafe).toDouble(),
            -Magnitude.toDouble(),
            Magnitude.toDouble(),
            -1.0,
            1.0
        )
        backRight.power = Range.scale(
            (speed - direction - strafe).toDouble(),
            -Magnitude.toDouble(),
            Magnitude.toDouble(),
            -1.0,
            1.0
        )

        //        //FeedMotor
//        if (gamepad1.right_bumper && ContinuousShooter.getPower()==1) {
//            //OneFlickRotation(1); //Change MAX
//
//            FeedMotor.setPower(FeedSpeed);
//            FeedMotor.setTargetPosition(FeedMotor.getCurrentPosition() + PULSE_PER_REVOLUTION_NEVEREST40_OVER_40); //Uses 70 percent of power
//            //telemetry.addData("FlickerAfter", /*"%10d",*/ Flicker.getCurrentPosition());
//
//        }
//        else if (gamepad1.left_bumper && ContinuousShooter.getPower()==1) {
//            FeedMotor.setPower(FeedSpeed);
//            FeedMotor.setTargetPosition(FeedMotor.getCurrentPosition() - PULSE_PER_REVOLUTION_NEVEREST40_OVER_40);
//        }
//
//        else  {
//            FeedMotor.setPower(0);
//        }

        //ContinuosBigOl'Servo
        if (gamepad1.a) {
            ContinuousShooter.power = 1.0
        } else if (gamepad1.b) {
            ContinuousShooter.power = 0.0
        }

        //ContinuosBabyDuboisServo
        if (gamepad1.dpad_up) {
            SmallContinuous.power = -1.0
        } else if (gamepad1.dpad_down) {
            SmallContinuous.power = 1.0
        } else {
            SmallContinuous.power = 0.0
        }
    }

    override fun stop() {
        super.stop()
        ContinuousShooter.power = 0.0
    }
}
