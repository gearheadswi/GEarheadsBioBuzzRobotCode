package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.EncoderBasedCurrentLimitingMotor;

@TeleOp
public class LauncherPIDFTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "launcher");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double targetSpeed = 0;
        double p = 36;
        double f = 14;

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.aWasPressed()){
                targetSpeed += 100;
            }
            if (gamepad1.bWasPressed()){
                targetSpeed -= 100;
            }

            if (gamepad1.dpadLeftWasPressed()){
                f += 1;
            }
            if (gamepad1.dpadRightWasPressed()){
                f -= 1;
            }
            if (gamepad1.dpadUpWasPressed()){
                p += 1;
            }
            if (gamepad1.dpadDownWasPressed()){
                p -= 1;
            }

            motor.setVelocity(targetSpeed);
            motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(p, 0, 0, f));

            telemetry.addData("Target", targetSpeed);
            telemetry.addData("Actual", motor.getVelocity());
            telemetry.addData("P", p);
            telemetry.addData("F", f);
            telemetry.update();
        }
    }
}
