package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.teamcode.hardware.EncoderBasedCurrentLimitingMotor;

public class IntakeTransfer {
    private static final double BLOCKER_CLOSED_POSITION = 0.15;
    private static final double BLOCKER_OPEN_POSITION = 0.7;
    private static final double FLOWER_RAMP_DOWN_LEFT = 0.975;
    private static final double FLOWER_RAMP_DOWN_RIGHT = 0;
    private static final double FLOWER_RAMP_UP_LEFT = 0;
    private static final double FLOWER_RAMP_UP_RIGHT = 0.925;

    private static final double DIGITAL_THRESHOLD = 40; // CM-ish
    private static final double MAX_CURRENT = 6;

    private DcMotorEx intakeMotor;
    private DcMotorEx transferMotor;
    private Servo blockerServo;
    private Servo flowerServoLeft;
    private Servo flowerServoRight;
    private AnalogInput distanceSensor;

    private EncoderBasedCurrentLimitingMotor intakeCurrentLimiter;
    private EncoderBasedCurrentLimitingMotor transferCurrentLimiter;

    private int ballCount = 0;
    private boolean isObjectPresent = false;
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

        blockerServo = hardwareMap.get(Servo.class, "blocker");

        flowerServoLeft = hardwareMap.get(Servo.class, "leftRamp");
        flowerServoRight = hardwareMap.get(Servo.class, "rightRamp");

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
     * Stops the intake motor from spinning
     */
    public void disableintake(){
        intakeMotor.setPower(0);
        transferMotor.setPower(0);
        targetCurrent = 0;
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
        boolean isBeamBroken = getCurrentDistance() > DIGITAL_THRESHOLD;
        if (!isObjectPresent && isBeamBroken){
           if(targetCurrent > 0) {
               if (ballCount < 4) {
                   ballCount++;
               }
           } else if (targetCurrent < 0){
               if (ballCount > 0)  {
                   ballCount--;
               }
           }
           isObjectPresent = true;
        }
        if (isObjectPresent && !isBeamBroken) {
           isObjectPresent = false;
        }
    }

    /**
     * Returns current ball count from inside of robot
     *
     * @return ball count
     */
    public int getBallCount() {
        return ballCount;
    }

    /**
     * Sets blocker servo to open and lets balls out
     * Clears ball count
     */
    public void startBallRelease(){
        blockerServo.setPosition(BLOCKER_OPEN_POSITION);
        ballCount = 0;
    }

    /**
     * Sets blocker servo to close and stops letting balls out
     */
    public void stopBallRelease(){
        blockerServo.setPosition(BLOCKER_CLOSED_POSITION);
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
