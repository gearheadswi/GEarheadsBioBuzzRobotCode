package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Brakes {
    private static final double LEFT_BRAKE_DOWN_POS = 0; // TODO tune
    private static final double LEFT_BRAKE_UP_POS = 0; // TODO tune

    private static final double RIGHT_BRAKE_DOWN_POS = 0; // TODO tune
    private static final double RIGHT_BRAKE_UP_POS = 0; // TODO tune

    private Servo leftBrake;
    private Servo rightBrake;

    public Brakes(HardwareMap hardwareMap){
        leftBrake = hardwareMap.get(Servo.class, "leftBrake");
        rightBrake = hardwareMap.get(Servo.class, "rightBrake");
    }

    public void brake(){
        leftBrake.setPosition(LEFT_BRAKE_DOWN_POS);
        rightBrake.setPosition(RIGHT_BRAKE_DOWN_POS);
    }

    public void release(){
        leftBrake.setPosition(LEFT_BRAKE_UP_POS);
        rightBrake.setPosition(RIGHT_BRAKE_UP_POS);
    }
}
