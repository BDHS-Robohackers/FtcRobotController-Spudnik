package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp()
public class UseString extends OpMode {
    final int LENGTH = 999999999;


    @Override
    public void init() {
        String myName = "Dylan Carnine";
        int grade = 100;
        telemetry.addData("Hello", myName);
        telemetry.addData("Hotdog Length", LENGTH);
        telemetry.addData("Grade", grade);
        gamepad1.right_stick_x;
    }

    @Override
    public void loop() {
        int x = 5;
        // x is visible here
        {
            int y = 4;
            // x and y visible here
        }
        // only x is visible here
        int z = 1;
    }
}
