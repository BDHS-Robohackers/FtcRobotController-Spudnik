package org.firstinspires.ftc.teamcode.subsystems;

import com.arcrobotics.ftclib.command.Subsystem;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.util.FTCDashboardPackets;

public class FingerSubsystem extends SubsystemBase {

    Servo finger;
    private final FTCDashboardPackets dbp = new FTCDashboardPackets("FingerSubsystem");

    public enum FingerPositions {
        OPEN(0),
        CLOSED(1);

        private final float position;

        FingerPositions(float position) {
            this.position = position;
        }

        public float getPosition() {
            return position;
        }
    }

    public FingerSubsystem(final Servo finger) {
        this.finger = finger;
    }

    public void locomoteFinger(double forward, double backward) {
        double power = forward-backward;
        power++;
        power/=2d;
        finger.setPosition(power);

        dbp.createNewTelePacket();
        dbp.info("Finger Power: "+power+", "+finger.getPosition()+", "+forward+", "+backward);
        dbp.send(false);
    }

    public void locomoteFinger(FingerPositions position) {
        finger.setPosition(position.getPosition());
    }

}
