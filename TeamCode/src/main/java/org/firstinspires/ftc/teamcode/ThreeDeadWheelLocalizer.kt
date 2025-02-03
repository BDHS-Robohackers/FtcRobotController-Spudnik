package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.DualNum
import com.acmerobotics.roadrunner.Time
import com.acmerobotics.roadrunner.Twist2dDual
import com.acmerobotics.roadrunner.Vector2d
import com.acmerobotics.roadrunner.Vector2dDual
import com.acmerobotics.roadrunner.ftc.Encoder
import com.acmerobotics.roadrunner.ftc.FlightRecorder.write
import com.acmerobotics.roadrunner.ftc.OverflowEncoder
import com.acmerobotics.roadrunner.ftc.RawEncoder
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.messages.ThreeDeadWheelInputsMessage
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer.EncoderComponent

@Config
class ThreeDeadWheelLocalizer(hardwareMap: HardwareMap, inPerTick: Double) : Localizer {
    class Params {
        var par0YTicks: Double =
            1845.6950345947898 // y position of the first parallel encoder (in tick units)
        var par1YTicks: Double =
            -2274.6147163090177 // y position of the second parallel encoder (in tick units)
        var perpXTicks: Double =
            1741.5823329440261 // x position of the perpendicular encoder (in tick units)
    }

    // TODO: make sure your config has **motors** with these names (or change them)
    //   the encoders should be plugged into the slot matching the named motor
    //   see https://ftc-docs.firstinspires.org/en/latest/hardware_and_software_configuration/configuring/index.html
    //par0 = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "fr_drv")));
    //par1 = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "intake")));
    //perp = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "extender")));
    val par0: Encoder =
        OverflowEncoder(RawEncoder(EncoderComponent.ENCODER_PAR0[hardwareMap]))
    val par1: Encoder =
        OverflowEncoder(RawEncoder(EncoderComponent.ENCODER_PAR1[hardwareMap]))
    val perp: Encoder =
        OverflowEncoder(RawEncoder(EncoderComponent.ENCODER_PERP[hardwareMap]))

    val inPerTick: Double

    private var lastPar0Pos = 0
    private var lastPar1Pos = 0
    private var lastPerpPos = 0
    private var initialized = false

    init {
        // TODO: reverse encoder directions if needed
        //   par0.setDirection(DcMotorSimple.Direction.REVERSE);
        par1.direction = DcMotorSimple.Direction.REVERSE

        this.inPerTick = inPerTick

        write("THREE_DEAD_WHEEL_PARAMS", PARAMS)
    }

    override fun update(): Twist2dDual<Time> {
        val par0PosVel = par0.getPositionAndVelocity()
        val par1PosVel = par1.getPositionAndVelocity()
        val perpPosVel = perp.getPositionAndVelocity()

        write(
            "THREE_DEAD_WHEEL_INPUTS",
            ThreeDeadWheelInputsMessage(par0PosVel, par1PosVel, perpPosVel)
        )

        if (!initialized) {
            initialized = true

            lastPar0Pos = par0PosVel.position
            lastPar1Pos = par1PosVel.position
            lastPerpPos = perpPosVel.position

            return Twist2dDual(
                Vector2dDual.constant(Vector2d(0.0, 0.0), 2),
                DualNum.constant(0.0, 2)
            )
        }

        val par0PosDelta = par0PosVel.position - lastPar0Pos
        val par1PosDelta = par1PosVel.position - lastPar1Pos
        val perpPosDelta = perpPosVel.position - lastPerpPos

        val twist = Twist2dDual(
            Vector2dDual(
                DualNum<Time>(
                    doubleArrayOf(
                        (PARAMS.par0YTicks * par1PosDelta - PARAMS.par1YTicks * par0PosDelta) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                        (PARAMS.par0YTicks * par1PosVel.velocity - PARAMS.par1YTicks * par0PosVel.velocity) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                    )
                ).times(inPerTick),
                DualNum<Time>(
                    doubleArrayOf(
                        (PARAMS.perpXTicks / (PARAMS.par0YTicks - PARAMS.par1YTicks) * (par1PosDelta - par0PosDelta) + perpPosDelta),
                        (PARAMS.perpXTicks / (PARAMS.par0YTicks - PARAMS.par1YTicks) * (par1PosVel.velocity - par0PosVel.velocity) + perpPosVel.velocity),
                    )
                ).times(inPerTick)
            ),
            DualNum(
                doubleArrayOf(
                    (par0PosDelta - par1PosDelta) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                    (par0PosVel.velocity - par1PosVel.velocity) / (PARAMS.par0YTicks - PARAMS.par1YTicks),
                )
            )
        )

        lastPar0Pos = par0PosVel.position
        lastPar1Pos = par1PosVel.position
        lastPerpPos = perpPosVel.position

        return twist
    }

    companion object {
        var PARAMS: Params = Params()
    }
}
