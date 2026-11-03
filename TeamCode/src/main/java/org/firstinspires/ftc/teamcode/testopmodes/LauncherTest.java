package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Launcher&HoodTest", group = "Tele Op")
public class LauncherTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx launchMotor1 = hardwareMap.get(DcMotorEx.class, "launch1");
        Servo hoodServo = hardwareMap.get(Servo.class, "hood"); // TODO: correct mapping

        launchMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launchMotor1.setPIDFCoefficients(DcMotor.RunMode.RUN_WITHOUT_ENCODER, new PIDFCoefficients(
                36,0,0,14
        ));

        double launcherSpeed = 1000.0;
        double hoodAngle = 0.5;

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.dpadLeftWasPressed()) {
                launcherSpeed -= 10;
            }
            if (gamepad1.dpadRightWasPressed()) {
                launcherSpeed += 10;
            }
            if (gamepad1.dpadUpWasPressed() && hoodAngle + 0.1 <= 1.0) {
                hoodAngle += 0.1;
            }
            if (gamepad1.dpadDownWasPressed() && hoodAngle - 0.1 >= 0.0) {
                hoodAngle -= 0.1;
            }

            hoodServo.setPosition(hoodAngle);
            launchMotor1.setVelocity(launcherSpeed);

            telemetry.addData("Launcher Speed: ", launcherSpeed);
            // TODO: Figure scaling between angle and servo values
            telemetry.addData("Hood Angle: ", hoodAngle);

        }
    }
}
