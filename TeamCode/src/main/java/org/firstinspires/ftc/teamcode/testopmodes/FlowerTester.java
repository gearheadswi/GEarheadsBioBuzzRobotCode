package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Flower;

@TeleOp(name = "FlowerTest", group = "Tele Op")
public class FlowerTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Flower flower = new Flower(hardwareMap);
        while (opModeIsActive()) {
            if (gamepad1.leftBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.UP);
            }
            if (gamepad1.rightBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.DOWN);
            }
            telemetry.addData("Current Slide Position (UP/DOWN): ", flower.getCurrentSlidePosition());
            telemetry.update();
        }
    }

}
