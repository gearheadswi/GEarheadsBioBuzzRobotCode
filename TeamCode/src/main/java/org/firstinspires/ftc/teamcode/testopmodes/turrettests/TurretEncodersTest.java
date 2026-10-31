package org.firstinspires.ftc.teamcode.testopmodes.turrettests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp(name = "Turret Encoders Test", group = "turret")
public class TurretEncodersTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Turret turret = new Turret(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("Absolute", turret.getAbsolutePosition());
            telemetry.addData("Relative", turret.getRelativeEncoderRotation());

            telemetry.update();
        }
    }

}
