package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.util.RandomUtils.withinThreshold;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

import java.util.Locale;

@Config
public class UppieTwoSubsystem extends SubsystemBase {
    private final DcMotorEx viperLeft;
    private final DcMotorEx viperRight;

    long lastStateChange = 0;

    private final FTCDashboardPackets dbp = new FTCDashboardPackets("UppieTwoSubsystem");

    static int MAX_ENCODER_POS = 1000;
    static int RUNG_ENCODER_POS = 500;
    /**
     * The amount the motors lower to attach the sample to the rung.
     */
    static int ATTACH_RUNG_HEIGHT = 100;
    static int MIN_ENCODER_POS = 0;
    static boolean EMERGENCY_OVERRIDE = false;
    static boolean STALL_SAFETY = true;
    static int STALL_THRESHOLD = 300;
    public static final double STALL_TICKS_PER_SECOND_THRESHOLD = .5;
    static double MAX_POWER = .3f;

    /**
     * The max differential allowed between the two motor's encoder positions.
     */
    static int MAX_DIFFERENTIAL_BETWEEN_MOTORS = 10;
    static int TARGET_THRESHOLD = 5;

    private UppieState currentState = UppieState.IDLE;

    public enum UppieState {
        MAX(MAX_ENCODER_POS),
        RUNG(RUNG_ENCODER_POS),
        ATTACH(RUNG_ENCODER_POS - ATTACH_RUNG_HEIGHT),
        MIN(MIN_ENCODER_POS),
        IDLE(0);

        private final int encoderPos;

        UppieState(final int encoderPos) {
            this.encoderPos = encoderPos;
        }

        public int getEncoderPos() {
            return encoderPos;
        }
    }

    public UppieTwoSubsystem(final DcMotorEx viperLeft, final DcMotorEx viperRight) {
        this.viperLeft = viperLeft;
        this.viperRight = viperRight;

        this.viperLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.viperRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.viperLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.viperRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public UppieTwoSubsystem(final HardwareMap map) throws Exception {
        this.viperLeft = RobotHardwareInitializer.MotorComponent.VIPER_LEFT.getEx(map);
        this.viperRight = RobotHardwareInitializer.MotorComponent.VIPER_RIGHT.getEx(map);

        this.viperLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.viperRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        this.viperLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.viperRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Checks the difference between the two motor encoder positions.
     * @return True if they are within the set amount, False if otherwise.
     * @author Carter Rommelfanger
     */
    public boolean checkDifferential() {
        dbp.debug("Checking differential");
        final boolean isWithinThreshold = withinThreshold(viperLeft.getCurrentPosition(), viperRight.getCurrentPosition(), MAX_DIFFERENTIAL_BETWEEN_MOTORS);

        if (EMERGENCY_OVERRIDE)
            return false;

        if (isWithinThreshold)
            dbp.warn("DIFFERENCE IS > MAXIMUM ALLOWED...");

        return isWithinThreshold;
    }

    public boolean isIdle() {
        return currentState.equals(UppieState.IDLE);
    }

    @Override
    public void periodic() {
        super.periodic();
        dbp.debug(String.format(Locale.ENGLISH, "ViperLeft: %d, ViperRight: %d", viperLeft.getCurrentPosition(), viperRight.getCurrentPosition()), true);
        if (currentState == UppieState.IDLE)
            return;

        if (!checkDifferential())
            return;

        int currentPosition = viperRight.getCurrentPosition();
        dbp.info("Uppies Position: " + currentPosition);
        dbp.send(true);

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.MAX && currentPosition <= MAX_ENCODER_POS) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED MAX LIMIT. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.RUNG && withinThreshold(currentPosition, UppieState.RUNG.encoderPos, TARGET_THRESHOLD)) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED LIMIT to RUNG. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.RUNG && withinThreshold(currentPosition, UppieState.ATTACH.encoderPos, TARGET_THRESHOLD)) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED LIMIT to ATTACH. HALTING.");
            dbp.send(true);
        }

        if (!EMERGENCY_OVERRIDE && currentState == UppieState.MIN && currentPosition >= MIN_ENCODER_POS) {
            setUppieState(UppieState.IDLE);
            dbp.info("EXCEEDED MIN LIMIT. HALTING.");
            dbp.send(true);
        }

        if (!STALL_SAFETY) {
            return;
        }

        long elapsedStateMillis = System.currentTimeMillis() - lastStateChange;
        if (elapsedStateMillis > STALL_THRESHOLD) {
            double ticksPerSecond = viperLeft.getVelocity(AngleUnit.DEGREES);
            dbp.info("Ticks = "+ticksPerSecond);
            dbp.send(false);
            if (ticksPerSecond < STALL_TICKS_PER_SECOND_THRESHOLD) {
                // STALLING!!!! STOP MOVING
                setUppieState(UppieState.IDLE);
                dbp.warn("UPPIES STALLING!");
                dbp.send(true);
            }
        }
    }

    public void setUppieState(UppieState state) {
        dbp.info("Updating state to: " + state);
        UppieState lastState = this.currentState;
        this.currentState = state;

        setPower(state == UppieState.MIN ? 1 : -1);
        if (state == UppieState.IDLE) {
            haltMotors();
        }
        // If the state changed, reset the timer. Allows for this method to be called periodically/every update.
        if (lastState != this.currentState) {
            lastStateChange = System.currentTimeMillis();
        }
        dbp.send(true);
    }

    public void setPower(final float power) {
        if (!checkDifferential()) {
            haltMotors();
            return;
        }

        double CLAMPED_POWER = power;

        if (power > MAX_POWER)
            CLAMPED_POWER = MAX_POWER;
        else if (power < -MAX_POWER)
            CLAMPED_POWER = -MAX_POWER;

        viperLeft.setPower(CLAMPED_POWER);
        viperRight.setPower(CLAMPED_POWER);
    }

    public void haltMotors() {
        if (viperRight.getPower() == 0 && viperLeft.getPower() == 0)
            return;
        dbp.info("Halting Motors!", true);
        viperLeft.setPower(0);
        viperRight.setPower(0);
    }
}
