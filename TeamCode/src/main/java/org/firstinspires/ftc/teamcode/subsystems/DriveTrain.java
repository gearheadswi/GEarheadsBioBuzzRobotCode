package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.utils.control.PIDF;

/**
 * Field-relative mecanum drivetrain.
 * Motor config names: {@code "fr"}, {@code "fl"}, {@code "br"}, {@code "bl"}.
 */
public class DriveTrain {
    private final DcMotorEx FR, BR, FL, BL;
    public PIDF pid;

    public DriveTrain(HardwareMap hardwareMap) {
        FR = hardwareMap.get(DcMotorEx.class, "fr");
        FL = hardwareMap.get(DcMotorEx.class, "fl");
        BR = hardwareMap.get(DcMotorEx.class, "br");
        BL = hardwareMap.get(DcMotorEx.class, "bl");

        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FL.setDirection(DcMotorSimple.Direction.REVERSE);
        BL.setDirection(DcMotorSimple.Direction.REVERSE);
        FR.setDirection(DcMotorSimple.Direction.FORWARD);
        BR.setDirection(DcMotorSimple.Direction.FORWARD);

        pid = new PIDF(1, 0, 0, 0);
    }

    /**
     * Drives the robot in a field-relative frame using standard mecanum math.
     *
     * @param leftStickX   strafe input  [-1, 1]
     * @param leftStickY   forward input [-1, 1] (negative = forward on most controllers)
     * @param rightStickX  turn input    [-1, 1]
     * @param fov_heading  robot's current yaw in radians (from IMU), used to rotate inputs to field frame
     * @param dampener     {@code true} for full speed; {@code false} for slow/precision mode (1/3 power)
     *
     * @implNote {@code moveMagnitude} currently squares {@code leftStickX} twice — likely a bug;
     *           it should be {@code pow(leftStickX,2) + pow(leftStickY,2)}.
     *           {@code turn} uses {@code rightStickX + abs(rightStickX)}, which doubles positive
     *           turn values and zeros negative ones — verify this is the intended behavior.
     * Probably not a bug, don't question Josh Wang
     */
    public void moveRobot(double leftStickX, double leftStickY, double rightStickX, double fov_heading, boolean dampener) {
        double moveAngle = atan2(-leftStickX, -leftStickY);
        double moveMagnitude = pow(leftStickX, 2) + pow(leftStickY, 2);
        double turn = rightStickX;

        double sinA = sin(PI / 4 + moveAngle - fov_heading);
        double sinB = sin(PI / 4 - moveAngle + fov_heading);
        double scale = dampener ? 1.0 : (1.0 / 3.0);

        FR.setPower(-Range.clip(moveMagnitude * sinA - turn, -1, 1) * scale);
        FL.setPower(-Range.clip(moveMagnitude * sinB + turn, -1, 1) * scale);
        BR.setPower(-Range.clip(moveMagnitude * sinB - turn, -1, 1) * scale);
        BL.setPower(-Range.clip(moveMagnitude * sinA + turn, -1, 1) * scale);
    }

    public double getPosHoldTurnPower(double heading, double target){
        return pid.smoothAngle(heading, target);
    }

}
