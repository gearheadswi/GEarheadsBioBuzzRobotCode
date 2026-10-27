package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.ftccommon.internal.manualcontrol.parameters.SetServoEnableParameters;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class IntakeTransfer {
    private static final double BLOCKER_CLOSED_POSITION = 1; // TODO Tune
    private static final double BLOCKER_OPEN_POSITION = 0; // TODO Tune

    private static final double FLOWER_RAMP_DOWN_LEFT = 0; // TODO Tune
    private static final double FLOWER_RAMP_DOWN_RIGHT = 0; // TODO Tune
    private static final double FLOWER_RAMP_UP_LEFT = 1; // TODO Tune
    private static final double FLOWER_RAMP_UP_RIGHT = 1; //TODO Tune

    private DcMotor intakeMotor;
    private DcMotor transferMotor;

    private Servo blockerServo;

    private Servo flowerServoLeft;
    private Servo flowerServoRight;

    private DistanceSensor distanceSensor;

    private int ballCount;

    private double previousDistance;

    /**
     * Initialize hardware for intake/transfer.
     * @param hardwareMap hardware map from opmode
     */
    public IntakeTransfer(HardwareMap hardwareMap){
        distanceSensor = hardwareMap.get(DistanceSensor.class,"");//TODO
        intakeMotor = hardwareMap.get(DcMotor.class, "");//TODO
        transferMotor = hardwareMap.get(DcMotor.class, "");//TODO
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD); //TODO
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);//TODO
        blockerServo = hardwareMap.get(Servo.class, ""); //TODO
        previousDistance = distanceSensor.getDistance(DistanceUnit.CM);
        ballCount = 0;
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
        if (intakeMotor.getPower() == -1) {
            return;
        }
        double currentDistance = distanceSensor.getDistance(DistanceUnit.CM);
        double emptyStorage = 25.0;
        double ballDetectedStorage = 20.0;
        if (previousDistance >= emptyStorage && currentDistance < ballDetectedStorage){
            if (ballCount < 4) {
                ballCount++;
            }
        }
        previousDistance = currentDistance;
    }



    /**
     * Returns current ball count from inside of robot
     * @return ball count
     */
    public void ballCount() {
        if (ballCount == 4) {
            //Connect to light on robot and set it turned on (Red)
        } else if (ballCount == 3) {
            //light color to orange
        } else if (ballCount == 2) {
            //light color to yellow
        } else if (ballCount == 1) {
            //light color to green
        } else if (ballCount == 0) {
            //light color neutral or white
        }
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
