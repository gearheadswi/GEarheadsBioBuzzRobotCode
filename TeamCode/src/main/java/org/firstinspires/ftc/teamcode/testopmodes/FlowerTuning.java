package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Flower;

@TeleOp(name = "Flower Tuner", group = "Flower")
public class FlowerTuning extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Flower flower = new Flower(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad2.dpadRightWasPressed()){
                flower.slidePidf.p += 0.001;
            }
            if (gamepad2.dpadLeftWasPressed()){
                flower.slidePidf.p -= 0.001;
            }

            if (gamepad2.dpadUpWasPressed()){
                flower.HOLD_POWER += 0.001;
            }
            if (gamepad2.dpadDownWasPressed()){
                flower.HOLD_POWER -= 0.001;
            }

            if (gamepad1.leftBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.UP);
            }
            if (gamepad1.rightBumperWasPressed()) {
                flower.setSlidePosition(Flower.FlowerSlidePosition.DOWN);
            }

            flower.update();
            telemetry.addData("P", flower.slidePidf.p);
            telemetry.addData("F", flower.HOLD_POWER);
            telemetry.addData("Current Slide Position (UP/DOWN): ", flower.getCurrentSlidePosition());
            telemetry.addData("Ticks", flower.getCurrentSlidePositionTicks());
            telemetry.addData("Position (in)", "%.2f", flower.getCurrentSlidePositionTicks() / Flower.TICKS_PER_INCH);
            telemetry.update();
        }
    }

}
