package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.PIDF;

public class Flower {
    private static final int DOWN_POSITION = 40; // Int representing number of ticks for the slideMotor
    private static final int UP_POSITION = 470; // Int representing number of ticks for the slideMotor
    private static final double EXTEND_POWER = 1.0; // Double representing the maximum power the slideMotor can use to stay in UP_POSITION
    private static final double RETRACT_POWER = 0.2; // Double representing the power value the slideMotor will use to manually stop and retract instead of relying on FLOAT
    public static final double TICKS_PER_INCH = 45; // Double representing number of ticks the slideMotor travels per every inch the slides move
    public static final double TEST_POWER = 0.5; // Double representing the amount of power the slideMotor will use in testing
    private static final int POSITION_TOLERANCE = 10; // Int representing the possible error in up or down positions before the slides are declared at that position
    private static final double P = 0.01; // Double representing the proportional gain of the slide PIDF, in power per tick of error (tune this)
    private static final double I = 0; // Double representing the integral gain of the slide PIDF. Keep at 0 unless integral clamping is added to PIDF.java (it has no anti-windup)
    private static final double D = 0; // Double representing the derivative gain of the slide PIDF (tune this)
    private static final double HOLD_POWER = 0.1; // Double representing the power the slideMotor needs to hold UP_POSITION against gravity (tune this)
    public enum FlowerSlidePosition {
        UP, DOWN
    }

    private final DcMotor slideMotor;
    private final PIDF slidePidf = new PIDF(P, I, D, 0); // f is 0 because HOLD_POWER is only applied at UP_POSITION
    private FlowerSlidePosition currentSlidePosition;

    /**
     * Initialize hardware for flower subsystem.
     *
     * @param hardwareMap Map that bridges physical robot components to software
     */
    public Flower(HardwareMap hardwareMap) {
        // Initializing hardware from HardwareMap
        // The encoder zero is set here, so the slides must be fully lowered when this runs
        slideMotor = hardwareMap.get(DcMotor.class, "flowerSlides");
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // Slides will enter down position when not being held up
        slideMotor.setDirection(DcMotorSimple.Direction.REVERSE);
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
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setTargetPosition(DOWN_POSITION);
            slideMotor.setPower(RETRACT_POWER);
            currentSlidePosition = FlowerSlidePosition.DOWN;
        } else if (targetPosition == FlowerSlidePosition.UP) {
            // Set position of motor to UP_POSITION
            slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            slideMotor.setTargetPosition(UP_POSITION);
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
     * @return the current slide motor postion in ticks
     */
    public int getCurrentSlidePositionTicks() {
        return slideMotor.getCurrentPosition();
    }
    public void FLowerSlideTestSetup() {
        slideMotor.setDirection(DcMotor.Direction.FORWARD); // flip if up is down
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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