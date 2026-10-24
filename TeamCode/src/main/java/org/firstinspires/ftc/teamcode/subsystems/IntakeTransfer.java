package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
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

    private DistanceSensor distanceSensor;

    private int ballCount;

    /**
     * Initialize hardware for intake/transfer.
     * @param hardwareMap hardware map from opmode
     */
    public IntakeTransfer(HardwareMap hardwareMap){
        // Initializing hardware from hardware map TODO
    }

    /**
     * Spins motors forward on intake and transfer
     */
    public void activateIntake(){
        // TODO
    }

    /**
     * Spins motors reverse on intake and transfer
     */
    public void reverseIntake(){
        // TODO
    }

    /**
     * Reads current distance sensor data and updates the ball count
     */
    public void updateBallCount(){
        // Check distance sensor
        // Update Ball Count
        // TODO
    }

    /**
     * Returns current ball count from inside of robot
     * @return ball count
     */
    public int ballCount(){
        return ballCount;
    }

    /**
     * Sets blocker servo to open and lets balls out
     * Clears ball count
     */
    public void startBallRelease(){

        // TODO
    }

    /**
     * Sets blocker servo to close and stops letting balls out
     */
    public void stopBallRelease(){

        // TODO
    }

    /**
     * Sets arm servos to up
     */
    public void flowerIntakeArmUp(){

        // TODO
    }

    /**
     * Sets arm servos down
     */
    public void flowerIntakeArmDown(){

        // TODO
    }
}
