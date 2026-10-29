package org.firstinspires.ftc.teamcode.testopmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.EncoderBasedCurrentLimitingMotor;

@TeleOp
public class CurrentLimiterTestEncoder extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        VoltageSensor voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        DcMotorEx actualMotor = hardwareMap.get(DcMotorEx.class, "transfer");
        actualMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        EncoderBasedCurrentLimitingMotor motor = new EncoderBasedCurrentLimitingMotor(
                actualMotor,
                voltageSensor
        );

        DcMotorEx actualMotor1 = hardwareMap.get(DcMotorEx.class, "intake");
        EncoderBasedCurrentLimitingMotor motor1 = new EncoderBasedCurrentLimitingMotor(
                actualMotor1,
                voltageSensor
        );

        double current = 0;

        waitForStart();
        while (opModeIsActive()) {

            if (gamepad1.aWasPressed()){
                current = 4.6;
            }
            if (gamepad1.bWasPressed()){
                current = 6;
            }
            if (gamepad1.yWasPressed()){
                current = 3;
            }
            if (gamepad1.xWasPressed()){
                current = -3;
            }

            motor.controlCurrent(current);
            motor1.controlCurrent(current);

            telemetry.addData("battery voltage", voltageSensor.getVoltage());
            telemetry.addData("Back-emf", motor.getBackEmf());
            telemetry.addData("current", actualMotor.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("Back-emf", motor1.getBackEmf());
            telemetry.addData("current", actualMotor1.getCurrent(CurrentUnit.AMPS));
            telemetry.update();
        }
    }
}
