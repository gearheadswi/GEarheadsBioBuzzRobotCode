package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flower {
    private static final int DOWN_POSITION = 0; // Int representing number of ticks for the slideMotor
    private static final int UP_POSITION = 0; // Int representing number of ticks for the slideMotor
    private static final double EXTEND_POWER = 1.0; // Double representing the maximum power the slideMotor can use to stay in UP_POSITION
    private static final double RETRACT_POWER = 0.2; // Double representing the power value the slideMotor will use to manually stop and retract instead of relying on FLOAT
    public static final double TICKS_PER_INCH = 45; // Double representing number of ticks the slideMotor travels per every inch the slides move
    public static final double TEST_POWER = 0.5; // Double representing the amount of power the slideMotor will use in testing
    private static final int POSITION_TOLERANCE = 10; // Int representing the possible error in up or down positions before the slides are declared at that position
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
        slideMotor = hardwareMap.get(DcMotor.class, "flowerSlides");
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // Slides will enter down position when not being held up
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Moves the slide according to which position the user wants to move it to, either UP or DOWN.
     *
     * @param targetPosition A FlowerSlidePosition, UP or DOWN, that the user specifies
     */
    public void setSlidePosition(FlowerSlidePosition targetPosition) {
        // TODO Type stellar code here
        // TODO check logic
        if (targetPosition == FlowerSlidePosition.DOWN) {
            // Set position of motor to DOWN_POSITION
            slideMotor.setTargetPosition(DOWN_POSITION);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setPower(RETRACT_POWER);
            currentSlidePosition = FlowerSlidePosition.DOWN;
        } else if (targetPosition == FlowerSlidePosition.UP) {
            // Set position of motor to UP_POSITION
            slideMotor.setTargetPosition(UP_POSITION);
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setPower(EXTEND_POWER);
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
        // Once the slides reach the lower position, the position is set to down
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Resets the motors and their encoders in case of emergency
     */
    public void fullReset() {
        // TODO Write great code
        manualStop();
        // TODO make update logic
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motors back on
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}