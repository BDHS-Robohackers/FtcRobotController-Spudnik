package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Autonomous
public class DJBCII extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "Dylan");
    }

    @Override
    public void loop() {

    }
}
