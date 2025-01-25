package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.util.RandomUtils.withinThreshold;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.util.LoggingUtils.FTCDashboardPackets;
import org.firstinspires.ftc.teamcode.util.RobotHardwareInitializer;

@Config
public class UppieTwoSubsystem extends SubsystemBase {
    private final DcMotorEx viper;
    //private final DcMotorEx viperRight;

    long lastStateChange = 0;

    private final FTCDashboardPackets dbp = new FTCDashboardPackets("UppieTwoSubsystem");

    public static int PICK_UP_ENCODER_HEIGHT = -350, HOOK_ENCODER_HEIGHT = -2182, ATTACH_ENCODER_HEIGHT = -1705;
    public static double MAX_POWER = .75f;
    public static double MAX_RTP_POWER = .9f;
    public static boolean KEEP_POS = true;

    /**
     * The max differential allowed between the two motor's encoder positions.
     */
    static int MAX_DIFFERENTIAL_BETWEEN_MOTORS = 10;
    static int TARGET_THRESHOLD = 5;

    private UppieState currentState = UppieState.IDLE;

    public enum UppieState {
        MAX(-4311),
        PICK_UP(PICK_UP_ENCODER_HEIGHT),
        HOOK(HOOK_ENCODER_HEIGHT),
        ATTACH(ATTACH_ENCODER_HEIGHT),
        MIN(0),
        IDLE(0);

        private final int encoderPos;

        UppieState(final int encoderPos) {
            this.encoderPos = encoderPos;
        }

        public int getEncoderPos() {
            return encoderPos;
        }
    }

    public UppieTwoSubsystem(final DcMotorEx viperLeft) {
        this.viper = viperLeft;
        this.viper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.viper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public UppieTwoSubsystem(final HardwareMap map) throws Exception {
        this.viper = RobotHardwareInitializer.MotorComponent.UPPIES.getEx(map);
        assert this.viper != null;
        this.viper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.viper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public boolean isIdle() {
        return currentState.equals(UppieState.IDLE);
    }

    @Override
    public void periodic() {
        super.periodic();
        if (currentState == UppieState.IDLE)
            return;

        int currentPosition = viper.getCurrentPosition();
        dbp.info("Uppies Position: " + currentPosition);
        dbp.send(true);


        if (currentState != UppieState.IDLE && withinThreshold(viper.getCurrentPosition(), viper.getTargetPosition(), 10)) {
            setUppieState(UppieState.IDLE);
        }

        /*if (!EMERGENCY_OVERRIDE && currentState == UppieState.MAX && currentPosition <= MAX_ENCODER_POS) {
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
        }*/
    }

    public void setUppieState(UppieState state) {
        dbp.info("Updating state to: " + state);
        dbp.send(true);
        UppieState lastState = this.currentState;
        this.currentState = state;

        if (state == UppieState.MIN)
            setPower(-1);
        else if (state == UppieState.MAX)
            setPower(1);
        else if (state == UppieState.IDLE) {
            haltMotors();
        }
        else if (state == UppieState.HOOK || state == UppieState.PICK_UP || state == UppieState.ATTACH) {
            viper.setTargetPosition(state.getEncoderPos());
            setUsingPositionMode();
            viper.setPower(0.8);
        }

        // If the state changed, reset the timer. Allows for this method to be called periodically/every update.
        if (lastState != this.currentState) {
            lastStateChange = System.currentTimeMillis();
        }
        dbp.info("CURRENT POSITION: " + viper.getCurrentPosition());
        dbp.send(true);
    }

    public void setPower(final float power) {
        /*if (!heightDifferenceWithinThreshold()) {
            haltMotors();
            return;
        }*/

        setUsingEncoderMode();

        double CLAMPED_POWER = power;

        if (power > MAX_POWER)
            CLAMPED_POWER = MAX_POWER;
        else if (power < -MAX_POWER)
            CLAMPED_POWER = -MAX_POWER;

        this.viper.setPower(CLAMPED_POWER);
    }

    public void holdMotorPosition() {
        if (this.viper.getMode() == DcMotor.RunMode.RUN_TO_POSITION)
            return;
        this.viper.setTargetPosition(this.viper.getCurrentPosition());
        this.viper.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.viper.setPower(MAX_RTP_POWER);
    }

    public void setUsingEncoderMode() {
        if (this.viper.getMode() == DcMotor.RunMode.RUN_USING_ENCODER)
            return;

        this.viper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // this.viper.setPower(0);
    }
    public void setUsingPositionMode() {
        if (this.viper.getMode() == DcMotor.RunMode.RUN_TO_POSITION)
            return;

        this.viper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void haltMotors() {
        if (viper.getPower() == 0)
            return;
        if (KEEP_POS) {
            holdMotorPosition();
            return;
        }
        dbp.info("Halting Motors!", true);
        this.viper.setPower(0);
    }
}
