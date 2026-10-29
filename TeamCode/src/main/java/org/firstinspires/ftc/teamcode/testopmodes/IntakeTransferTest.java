package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;

@TeleOp(name = "Intake/Transfer Test", group = "Tele Op")
public class IntakeTransferTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        IntakeTransfer intakeTransfer = new IntakeTransfer(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.aWasPressed()) {
                intakeTransfer.activateIntake();
            }
            if (gamepad1.bWasPressed()) {
                intakeTransfer.reverseIntake();
            }
            if (gamepad1.xWasPressed()) {
                intakeTransfer.flowerIntakeArmUp();
            }
            if (gamepad1.yWasPressed()) {
                intakeTransfer.flowerIntakeArmDown();
            }
            if (gamepad1.left_trigger_pressed) {
                intakeTransfer.startBallRelease();
            }
            if (gamepad1.right_trigger_pressed) {
                intakeTransfer.stopBallRelease();
            }

            intakeTransfer.updateBallCount();

            intakeTransfer.updateCurrentLimiting();

            telemetry.addData("Ball Count:", intakeTransfer.returnBallCount());
            telemetry.update();
        }
    }

}
