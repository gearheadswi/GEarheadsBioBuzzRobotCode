package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeTransfer {
    private static final double BLOCKER_CLOSED_POSITION = 0; // TODO Tune
    private static final double BLOCKER_OPEN_POSITION = 0; // TODO Tune

    private static final double FLOWER_RAMP_DOWN = 0; // TODO Tune
    private static final double FLOWER_RAMP_UP = 0; // TODO Tune

    private DcMotor intakeMotor;
    private DcMotor transferMotor;

    private Servo blockerServo;

    private Servo flowerServoLeft;
    private Servo flowerServoRight;

    private int ballCount;

    public IntakeTransfer(HardwareMap hardwareMap){
        // Initializing hardware from hardware map
    }

    public void activateIntake(){

    }

    public void reverseIntake(){

    }

    public void updateBallCount(){
        // Check distance sensor
        // Update Ball Count
    }

    public int ballCount(){
        return 0;
    }

    public void startBallRelease(){

    }

    public void stopBallRelease(){

    }

    public void flowerIntakeArmUp(){

    }

    public void flowerIntakeArmDown(){

    }
}
