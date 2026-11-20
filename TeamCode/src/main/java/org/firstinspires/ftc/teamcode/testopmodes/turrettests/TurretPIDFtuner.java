package org.firstinspires.ftc.teamcode.testopmodes.turrettests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.control.TurretPIDF;
import org.firstinspires.ftc.teamcode.utils.control.TurretProfileController;

@TeleOp(name = "Turret PIDF tuner", group = "turret")
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
//                turret.startCalibrate();
            }
            if (gamepad1.yWasPressed()){
                targetDeg -= 20;
            }
            if (gamepad1.xWasPressed()){
                targetDeg += 20;
            }
            if (gamepad1.bWasPressed()){
                targetDeg = -90;
            }

            if(gamepad2.bWasPressed()){
                stepIndex = (stepIndex + 1) % stepSizes.length;
            }
            if (gamepad2.dpadUpWasPressed()){
                TurretPIDF.p += stepSizes[stepIndex];
            }
            if (gamepad2.dpadDownWasPressed()){
                TurretPIDF.p -= stepSizes[stepIndex];
            }
            if (gamepad2.dpadRightWasPressed()){
                TurretPIDF.d += stepSizes[stepIndex];
            }
            if (gamepad2.dpadLeftWasPressed()){
                TurretPIDF.d -= stepSizes[stepIndex];
            }
            if (gamepad2.aWasPressed()){
                TurretPIDF.kS += stepSizes[stepIndex];
            }
            if (gamepad2.backWasPressed()){
                TurretPIDF.kS -= stepSizes[stepIndex];
            }
            if (gamepad2.xWasPressed()){
                TurretPIDF.kV += stepSizes[stepIndex];
            }
            if (gamepad2.yWasPressed()){
                TurretPIDF.kV -= stepSizes[stepIndex];
            }

            turret.setTurretAimOffset(targetDeg);

            turret.updateMovement(
                dt,
                    0,
                    0
            );

            telemetry.addData("kP (Down Up)", TurretPIDF.p);
            telemetry.addData("kD (Left Right)", TurretPIDF.d);
            telemetry.addData("kS (A B)", TurretPIDF.kS);
            telemetry.addData("kV (X Y)", TurretPIDF.kV);
            telemetry.addData("Target Angle", targetDeg);
            telemetry.addData("Error", TurretPIDF.angle_difference(targetDeg, turret.getAbsolutePosition()));
            telemetry.addLine();
            telemetry.addData("Absolute", turret.getAbsolutePosition());
            telemetry.addData("Calibrating", turret.isCalibrating());
            telemetry.addLine();
            telemetry.addData("Step Size","%.4f (B Button)",stepSizes[stepIndex]);
            telemetry.update();
        }
    }

}
