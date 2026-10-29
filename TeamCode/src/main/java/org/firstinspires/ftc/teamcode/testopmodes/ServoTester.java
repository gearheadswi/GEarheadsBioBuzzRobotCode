package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ServoTest", group = "Tele Op")
public class ServoTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo = hardwareMap.get(Servo.class, "test servo");
        double current_position = 0.5;
        double increment = 0.05;

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.leftBumperWasPressed() && current_position >= 0.05) {
                current_position -= increment;
            }
            if (gamepad1.rightBumperWasPressed() && current_position <= 0.95) {
                current_position += increment;
            }

            servo.setPosition(current_position);

            telemetry.addData("Current Servo Position [0.0-1.0]: ", current_position);
            telemetry.update();
        }
    }

}
