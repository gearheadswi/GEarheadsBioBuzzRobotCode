package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Flower;

@TeleOp(name = "Flower Test", group = "Flower")
public class FlowerTester extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Flower flower = new Flower(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.leftBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.UP);
            }
            if (gamepad1.rightBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.DOWN);
            }

            telemetry.addData("Current Slide Position (UP/DOWN): ", flower.getCurrentSlidePosition());
            telemetry.addData("Ticks", flower.getCurrentSlidePositionTicks());
            telemetry.addData("Position (in)", "%.2f", flower.getCurrentSlidePositionTicks() / Flower.TICKS_PER_INCH);
            telemetry.update();
        }
    }

}
