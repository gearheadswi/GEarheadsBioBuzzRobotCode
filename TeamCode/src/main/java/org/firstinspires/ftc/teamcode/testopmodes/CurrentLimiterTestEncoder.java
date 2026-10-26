package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.EncoderBasedCurrentLimitingMotor;

public class CurrentLimiterTestEncoder extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        VoltageSensor voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        DcMotorEx actualMotor = hardwareMap.get(DcMotorEx.class, "intake");
        EncoderBasedCurrentLimitingMotor motor = new EncoderBasedCurrentLimitingMotor(
                actualMotor,
                voltageSensor
        );

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.aWasPressed()){
                motor.setCurrent(4.6);
            }
            if (gamepad1.bWasPressed()){
                motor.setCurrent(0.5);
            }
            if (gamepad1.yWasPressed()){
                motor.setCurrent(1.5);
            }
            if (gamepad1.xWasPressed()){
                motor.setCurrent(-1.5);
            }

            telemetry.addData("battery voltage", voltageSensor.getVoltage());
            telemetry.addData("Back-emf", motor.getBackEmf());
            telemetry.addData("current", actualMotor.getCurrent(CurrentUnit.AMPS));
            telemetry.update();
        }
    }
}
