package org.firstinspires.ftc.teamcode.testopmodes.turrettests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp(name = "TurretEncodersTest")
public class TurretCalibrationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Turret turret = new Turret(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.aWasPressed()){
                turret.startCalibrate();
            }

            if (turret.isCalibrating()){
                turret.updateMovement(
                    0,
                        0,
                        0
                );
            }

            telemetry.addData("Calibrating", turret.isCalibrating());
            telemetry.addData("Absolute", turret.getAbsolutePosition());
            telemetry.addData("Relative", turret.getRelativeEncoderRotation());
            telemetry.update();
        }
    }

}
