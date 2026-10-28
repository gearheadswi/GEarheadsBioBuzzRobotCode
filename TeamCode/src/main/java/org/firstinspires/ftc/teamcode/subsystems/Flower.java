package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flower {
    private static final double DOWN_POSITION = 0.0; // TODO Check type and tune
    private static final double UP_POSITION = 0.0; // TODO Check type and tune

    public enum FlowerSlidePosition {
        UP, DOWN
    }

    private final DcMotor slideMotor;
    private FlowerSlidePosition currentSlidePosition;

    /**
     * Initialize hardware for flower subsystem.
     *
     * @param hardwareMap Map that bridges physical robot components to software
     */
    public Flower(HardwareMap hardwareMap) {
        // Initializing hardware from HardwareMap
        slideMotor = hardwareMap.get(DcMotor.class, ""); // TODO Change deviceName
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // Slides will enter down position when not being held up
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Moves the slide according to which position the user wants to move it to, either UP or DOWN.
     *
     * @param position A FlowerSlidePosition, UP or DOWN, that the user specifies
     */
    public void setSlidePosition(FlowerSlidePosition position) {
        // TODO Type stellar code here
        if (position == FlowerSlidePosition.DOWN) {
            // Set position of motor to UP_POSITION - this set is just a test for the tester
            currentSlidePosition = FlowerSlidePosition.DOWN;
        } else if (position == FlowerSlidePosition.UP) {
            // Set position of motor to UP_POSITION
            currentSlidePosition = FlowerSlidePosition.UP;
        }
    }

    /**
     * Accessor method for the current slide position as UP or DOWN
     *
     * @return the current slide position as a FlowerSlidePosition enum constant
     */
    public FlowerSlidePosition getCurrentSlidePosition() {
        return currentSlidePosition;
    }

    /**
     * Cuts all power to the motors, making them float down to lower position.
     */
    public void manualStop() {
        // TODO check code
        slideMotor.setPower(0);
        // Once it reaches lower bound,
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Resets the motors and their encoders in case of emergency
     */
    public void fullReset() {
        // TODO Write great code
        manualStop();
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motors back on
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}