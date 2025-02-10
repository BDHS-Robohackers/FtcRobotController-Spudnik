package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class IfOpmode extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {
        if(gamepad1.left_stick_y < 0){
            telemetry.addData("Left stick", " is negitive");
        } else if (gamepad1.left_stick_y > 0){
            telemetry.addData("Left stick", " is positive");
        }
        telemetry.addData("Left stick y", gamepad1.left_stick_y);

        double trigger1 = gamepad1.left_trigger;
        double trigger2 = gamepad1.right_trigger;

        double motorPower = trigger2 - trigger1;
    }
}
