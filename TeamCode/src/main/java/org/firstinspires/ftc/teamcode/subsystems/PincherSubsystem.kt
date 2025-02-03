package org.firstinspires.ftc.teamcode.subsystems

import android.annotation.SuppressLint
import com.arcrobotics.ftclib.command.SubsystemBase
import com.arcrobotics.ftclib.hardware.ServoEx
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets
import org.firstinspires.ftc.teamcode.util.MatchRecorder.MatchLogger
import java.util.Objects

@Deprecated("")
class PincherSubsystem(var finger1: ServoEx, var finger2: ServoEx) : SubsystemBase() {
    private val dbp = FTCDashboardPackets("FingerSubsystem")

    enum class FingerPositions(val angle: Float, val angleUnit: AngleUnit) {
        ZERO(0, AngleUnit.DEGREES),
        OPEN(MAX_ANGLE / 2f, AngleUnit.DEGREES),
        CLOSED(MAX_ANGLE, AngleUnit.DEGREES)
    }

    var currentFingerPosition: FingerPositions? = null

    var lastPower: Double = Double.MIN_VALUE

    init {
        Objects.requireNonNull(finger1)
        Objects.requireNonNull(finger2)
        finger1.setRange(0.0, MAX_ANGLE.toDouble(), AngleUnit.DEGREES)
        finger2.setRange(0.0, MAX_ANGLE.toDouble(), AngleUnit.DEGREES)

        //finger1.setInverted(true); //  Might need to change it to finger2
        //finger1.setInverted(true); //  Might need to change it to finger1
        // MIGHT cause errors
        //locomoteFinger(FingerPositions.ZERO);
    }

    @SuppressLint("DefaultLocale")
    fun locomoteFinger(position: FingerPositions) {
        //finger1.turnToAngle(position.getAngle(), position.getAngleUnit());
        //finger2.turnToAngle(position.getAngle(), position.getAngleUnit());

        val angleScale = (position.angle / MAX_ANGLE).toDouble()

        //finger1.setPosition(1f-angleScale);
        //finger2.setPosition(angleScale);
        finger1.turnToAngle(
            ((MAX_ANGLE - position.angle) / 3f).toDouble(),
            position.angleUnit
        )
        finger2.turnToAngle(((position.angle) / 3f).toDouble(), position.angleUnit)
        //dbp.info("ANGLE: "+angleScale);
        //dbp.info("POSITION: "+finger1.getPosition()+ ", "+finger2.getPosition());
        //dbp.info("OBJECT: "+finger1+ ", "+finger2);
        var debug = "Angle: %f\nTargetPos: %f\nPosition: %f\nPositionName: %s"
        debug = String.format(
            debug,
            position.angle, angleScale, finger1.position, position.name
        )
        dbp.info(debug)
        dbp.send(true)

        currentFingerPosition = position
        MatchLogger.getInstance().genericLog("Finger", MatchLogger.FileType.FINGER, position.name)
    }

    fun closeFinger() {
        locomoteFinger(FingerPositions.CLOSED)
    }

    fun openFinger() {
        locomoteFinger(FingerPositions.OPEN)
    }

    fun zeroFinger() {
        locomoteFinger(FingerPositions.ZERO)
    }

    val isFingerReady: Boolean
        get() {
            if (currentFingerPosition == null) {
                return false
            }
            val angleScale =
                (currentFingerPosition!!.angle / MAX_ANGLE).toDouble()
            return ((finger1.angle == 1f - angleScale) && (finger2.angle == angleScale))
        }

    companion object {
        var MAX_ANGLE: Float = 5 / 2f
    }
}
