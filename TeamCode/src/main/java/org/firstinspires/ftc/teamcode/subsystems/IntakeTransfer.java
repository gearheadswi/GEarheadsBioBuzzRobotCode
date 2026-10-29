package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class IntakeTransfer {
    private static final double BLOCKER_CLOSED_POSITION = 1; // TODO Tune
    private static final double BLOCKER_OPEN_POSITION = 0; // TODO Tune
    private static final double FLOWER_RAMP_DOWN_LEFT = 0.95; // TODO Tune
    private static final double FLOWER_RAMP_DOWN_RIGHT = 0; // TODO Tune
    private static final double FLOWER_RAMP_UP_LEFT = 0.025; // TODO Tune
    private static final double FLOWER_RAMP_UP_RIGHT = 0.925; //TODO Tune
    private static final double EMPTY_STORAGE = 25.0;
    private static final double BALL_DETECTED_STORAGE = 20.0;
    private DcMotor intakeMotor;
    private DcMotor transferMotor;
    private Servo blockerServo;
    private RevBlinkinLedDriver light;
    private Servo flowerServoLeft;
    private Servo flowerServoRight;
    private DistanceSensor distanceSensor;
    private int ballCount = 0;
    private boolean isObjectPresent = false;

    /**
     * Initialize hardware for intake/transfer.
     * @param hardwareMap hardware map from opmode
     */
    public IntakeTransfer(HardwareMap hardwareMap){

        distanceSensor = hardwareMap.get(DistanceSensor.class,"");//TODO

        intakeMotor = hardwareMap.get(DcMotor.class, "intake");//TODO
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD); //TODO

        transferMotor = hardwareMap.get(DcMotor.class, "transfer");//TODO
        transferMotor.setDirection(DcMotorSimple.Direction.REVERSE);//TODO

        blockerServo = hardwareMap.get(Servo.class, "blockerServo"); //TODO

        flowerServoLeft = hardwareMap.get(Servo.class, "leftRamp");
        flowerServoRight = hardwareMap.get(Servo.class, "rightRamp");

        isObjectPresent = (distanceSensor.getDistance(DistanceUnit.CM) < BALL_DETECTED_STORAGE);
        // Initializing hardware from hardware map TODO
    }

    /**
     * Spins motors forward on intake and transfer
     */
    public void activateIntake(){
        intakeMotor.setPower(1);
        transferMotor.setPower(1);
        // TODO
    }

    /**
     * Spins motors reverse on intake and transfer
     */
    public void reverseIntake(){
        intakeMotor.setPower(-1);
        transferMotor.setPower(-1);
        // TODO
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
        double currentDistance = distanceSensor.getDistance(DistanceUnit.CM);

        if (!isObjectPresent && currentDistance < BALL_DETECTED_STORAGE) {
            if (intakeMotor.getPower() > 0) {
                if (ballCount < 4) {
                    ballCount++;
                }
                isObjectPresent = true;
            } else if (intakeMotor.getPower() < 0) {
                if (ballCount > 0) {
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
        flowerServoLeft.setPosition(FLOWER_RAMP_UP_LEFT);//TODO
        flowerServoRight.setPosition(FLOWER_RAMP_UP_RIGHT);//TODO
    }

    /**
     * Sets arm servos down
     */
    public void flowerIntakeArmDown(){
        flowerServoLeft.setPosition(FLOWER_RAMP_DOWN_LEFT);//TODO
        flowerServoRight.setPosition(FLOWER_RAMP_DOWN_RIGHT);// TODO
    }
}
