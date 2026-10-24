package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flower {
    private static final double DOWN_POSITION = 0.0; // TODO Check type and tune
    private static final double UP_POSITION = 0.0; // TODO Check type and tune

    public enum FlowerSlidePosition {
        UP, DOWN
    }

    private DcMotor leftSlideMotor;
    private DcMotor rightSlideMotor;
    private FlowerSlidePosition currentSlidePosition;

    /**
     * Initialize hardware for flower subsystem.
     *
     * @param hardwareMap Map that bridges physical robot components to software
     */
    public Flower(HardwareMap hardwareMap) {
        // Initializing hardware from HardwareMap
        leftSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // TODO verify this works
        leftSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightSlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // So that the slides can catch a break when we force stop them
    }

    /**
     * Moves the slide according to which position the user wants to move it to, either UP or DOWN.
     *
     * @param position A FlowerSlidePosition, UP or DOWN, that the user specifies
     */
    public void setSlidePosition(FlowerSlidePosition position) {
        // TODO Type stellar code here
        if (position == FlowerSlidePosition.DOWN) {
            // Set position of motor to UP_POSITION
        } else if (position == FlowerSlidePosition.UP) {
            // Set position of motor to UP_POSITION
        }
    }

    /**
     * Accessor method for the current slide position as UP or DOWN
     *
     * @return the current slide position as a FlowerSlidePosition enum constant
     */
    public FlowerSlidePosition getExtendedState() {
        return currentSlidePosition;
    }

    /**
     * Cuts all power to the motors, making them float down to lower position.
     */
    public void manualStop() {
        // TODO check code
        leftSlideMotor.setPower(0);
        rightSlideMotor.setPower(0);
        // Once it reaches lower bound,
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Resets the motors and their encoders in case of emerg
     */
    public void fullReset() {
        // TODO Write great code
        manualStop();
        // Reset encoders
        leftSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motors back on
        leftSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightSlideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}