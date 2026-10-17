package org.firstinspires.ftc.teamcode.testopmodes.turrettests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.control.TurretProfileController;

@TeleOp(name = "Turret PIDF tuner")
public class TurretPIDFtuner extends LinearOpMode {

    double[] stepSizes = {10.0, 1.0, 0.1, 0.01, 0.001, 0.0001};
    int stepIndex = 1;

    double targetDeg = 0;

    private long lastUpdate = 0; // Milliseconds

    @Override
    public void runOpMode() throws InterruptedException {
        Turret turret = new Turret(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            double dt = (System.currentTimeMillis() - lastUpdate) / 1000.0;
            lastUpdate = System.currentTimeMillis();

            if (gamepad1.aWasPressed()){
                turret.startCalibrate();
            }
            if (gamepad1.yWasPressed()){
                turret.setTurretAimOffset(0);
            }
            if (gamepad1.xWasPressed()){
                turret.setTurretAimOffset(-90);
            }
            if (gamepad1.bWasPressed()){
                turret.setTurretAimOffset(90);
            }

            if(gamepad2.bWasPressed()){
                stepIndex = (stepIndex + 1) % stepSizes.length;
            }
            if (gamepad2.dpadUpWasPressed()){
                TurretProfileController.kP += stepSizes[stepIndex];
            }
            if (gamepad2.dpadDownWasPressed()){
                TurretProfileController.kP -= stepSizes[stepIndex];
            }
            if (gamepad2.dpadRightWasPressed()){
                TurretProfileController.kD += stepSizes[stepIndex];
            }
            if (gamepad2.dpadLeftWasPressed()){
                TurretProfileController.kD -= stepSizes[stepIndex];
            }
            if (gamepad2.aWasPressed()){
                TurretProfileController.kS += stepSizes[stepIndex];
            }
            if (gamepad2.backWasPressed()){
                TurretProfileController.kS -= stepSizes[stepIndex];
            }
            if (gamepad2.xWasPressed()){
                TurretProfileController.kV += stepSizes[stepIndex];
            }
            if (gamepad2.yWasPressed()){
                TurretProfileController.kV -= stepSizes[stepIndex];
            }



            turret.updateMovement(
                dt,
                    0,
                    0
            );

            telemetry.addData("kP (Down Up)", TurretProfileController.kP);
            telemetry.addData("kD (Left Right)", TurretProfileController.kD);
            telemetry.addData("kS (A B)", TurretProfileController.kS);
            telemetry.addData("kV (X Y)", TurretProfileController.kV);
            telemetry.addData("Target Angle", targetDeg);
            telemetry.addData("Error", targetDeg - turret.getRelativeEncoderRotation());
            telemetry.addLine();
            telemetry.addData("Relative", turret.getRelativeEncoderRotation());
            telemetry.addData("Absolute", turret.getAbsolutePosition());
            telemetry.addData("Calibrating", turret.isCalibrating());
            telemetry.addLine();
            telemetry.addData("Step Size","%.4f (B Button)",stepSizes[stepIndex]);
            telemetry.update();
        }
    }

}
