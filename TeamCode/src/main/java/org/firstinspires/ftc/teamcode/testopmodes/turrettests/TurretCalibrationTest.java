package org.firstinspires.ftc.teamcode.testopmodes.turrettests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp(name = "Turret Calibration Test")
public class TurretCalibrationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Turret turret = new Turret(hardwareMap);

        waitForStart();

        long lastUpdate = 0;

        while (opModeIsActive()) {
            long curTime = System.currentTimeMillis();
            double dt = (curTime - lastUpdate) / 1000.0;
            lastUpdate = curTime;

            if (gamepad1.aWasPressed()){
                turret.startCalibrate();
            }

            if (turret.isCalibrating()){
                turret.updateMovement(
                    dt,
                        0,
                        0
                );
            }

            telemetry.addData("Calibrating", turret.isCalibrating());
            telemetry.addData("Absolute", turret.getAbsolutePosition());
            telemetry.update();
        }
    }

}
