package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * TODO Hood is not mechanically done yet
 */
public class Launcher {
    private final DcMotorEx launchMotor1;
//    private final Servo hoodServo;
    private boolean launching = false;
    private double launchSpeed = 1000;

    public Launcher(HardwareMap hardwareMap){
        launchMotor1 = hardwareMap.get(DcMotorEx.class, "launcher");
//        hoodServo = hardwareMap.get(Servo.class, "hood"); // TODO: correct mapping

        launchMotor1.setDirection(DcMotorSimple.Direction.REVERSE);
        launchMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        setPID(36, 14);
    }

    private void setMotorSpeed(double speed){
        launchSpeed = speed;
        launchMotor1.setVelocity(launchSpeed);
    }

    public void setPID(double p, double f){
        launchMotor1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(
                p,0,0,f
        ));
    }

    public void launchOn(){
        if (!launching){
            setMotorSpeed(launchSpeed);
            launching = true;
        }
    }

    public void launchOff(){
        if (launching){
            setMotorSpeed(0);
            launching = false;
        }
    }

    public void setTargetSpeed(double speed){
        launchSpeed = speed;
    }

//    public void setHoodAngle(double angle) {
//        //TODO: convert this to angle
//        hoodServo.setPosition(angle);
//    }
}