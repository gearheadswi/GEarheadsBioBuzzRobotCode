package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.EncoderBasedCurrentLimitingMotor;

public class IntakeTransfer {
    private static final double BLOCKER_CLOSED_POSITION = 1; // TODO Tune
    private static final double BLOCKER_OPEN_POSITION = 0; // TODO Tune
    private static final double FLOWER_RAMP_DOWN_LEFT = 0.975;
    private static final double FLOWER_RAMP_DOWN_RIGHT = 0;
    private static final double FLOWER_RAMP_UP_LEFT = 0;
    private static final double FLOWER_RAMP_UP_RIGHT = 0.925;
    private static final double EMPTY_STORAGE = 12.5; // CM
    private static final double BALL_DETECTED_STORAGE = 9; // CM
    private static final double MAX_CURRENT = 6;
    private DcMotorEx intakeMotor;
    private DcMotorEx transferMotor;
    private Servo blockerServo;
    private Servo flowerServoLeft;
    private Servo flowerServoRight;
    private AnalogInput distanceSensor;
    private int ballCount = 0;
    private boolean isObjectPresent = false;

    private EncoderBasedCurrentLimitingMotor intakeCurrentLimiter;
    private EncoderBasedCurrentLimitingMotor transferCurrentLimiter;
    private double targetCurrent = 0;

    /**
     * Initialize hardware for intake/transfer.
     * @param hardwareMap hardware map from opmode
     */
    public IntakeTransfer(HardwareMap hardwareMap){

        distanceSensor = hardwareMap.get(AnalogInput.class,"breakbeam");

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        transferMotor = hardwareMap.get(DcMotorEx.class, "transfer");
        transferMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        blockerServo = hardwareMap.get(Servo.class, "blockerServo");

        flowerServoLeft = hardwareMap.get(Servo.class, "leftRamp");
        flowerServoRight = hardwareMap.get(Servo.class, "rightRamp");

        isObjectPresent = (getCurrentDistance() < BALL_DETECTED_STORAGE);
        // Initializing hardware from hardware map

        VoltageSensor voltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        intakeCurrentLimiter = new EncoderBasedCurrentLimitingMotor(intakeMotor, voltageSensor);
        transferCurrentLimiter = new EncoderBasedCurrentLimitingMotor(transferMotor, voltageSensor);
    }

    /**
     * Spins motors forward on intake and transfer
     */
    public void activateIntake(){
        targetCurrent = MAX_CURRENT;
    }

    /**
     * Spins motors reverse on intake and transfer
     */
    public void reverseIntake(){
        targetCurrent = -MAX_CURRENT;
    }

    /**
     * Updates the motor to achieve target current.
     */
    public void updateCurrentLimiting(){
        intakeCurrentLimiter.controlCurrent(targetCurrent);
        transferCurrentLimiter.controlCurrent(targetCurrent);
    }

    /**
     * When the reverse intake is running, stops taking distance data.
     * Prevents counting 50 objects in one second for just one ball by;
     * 1)Checks if previous distance is larger than/equal to the distance with no balls(If it was smaller, that means there's a ball passing in multiple loops, and it will double count)
     * 2)Checks if current distance is smaller than the empty distance(If it was greater, that means there is no ball passing, therefore would not count)
     * 3)Check for above statments to be true and then makes sure ball detection does not exceed 4 balls.
     * 4)Then adds +1 object.
     */
    public void updateBallCount(){
        double currentDistance = getCurrentDistance();

        if (!isObjectPresent && currentDistance < BALL_DETECTED_STORAGE) {
            if (targetCurrent > 0) {
                if (ballCount < 4) {
                    ballCount++;
                }
                isObjectPresent = true;
            } else if (intakeMotor.getPower() < 0) {
                if (targetCurrent > 0) {
                    ballCount--;
                }
                isObjectPresent = true;
            }
        }
        if (isObjectPresent && currentDistance >= EMPTY_STORAGE) {
            isObjectPresent= false;
        }
    }

    /**
     * Returns current ball count from inside of robot
     *
     * @return ball count
     */
    public int returnBallCount() {
        return ballCount;
    }

    /**
     * Sets blocker servo to open and lets balls out
     * Clears ball count
     */
    public void startBallRelease(){
        blockerServo.setPosition(BLOCKER_OPEN_POSITION);//TODO
        ballCount = 0;
    }

    /**
     * Sets blocker servo to close and stops letting balls out
     */
    public void stopBallRelease(){
        blockerServo.setPosition(BLOCKER_CLOSED_POSITION);//TODO

    }

    /**
     * Sets arm servos to up
     */
    public void flowerIntakeArmUp(){
        flowerServoLeft.setPosition(FLOWER_RAMP_UP_LEFT);
        flowerServoRight.setPosition(FLOWER_RAMP_UP_RIGHT);
    }

    /**
     * Sets arm servos down
     */
    public void flowerIntakeArmDown(){
        flowerServoLeft.setPosition(FLOWER_RAMP_DOWN_LEFT);
        flowerServoRight.setPosition(FLOWER_RAMP_DOWN_RIGHT);
    }

    /**
     * Gets the current distance detected by the distance sensor
     * @return distance (CM)
     */
    public double getCurrentDistance(){
        return (distanceSensor.getVoltage() / 3.3) * 100;
    }
}
