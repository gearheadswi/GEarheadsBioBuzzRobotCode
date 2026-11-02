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
    private int targetTicks = DOWN_POSITION; // Target position of the slides in ticks
    private boolean active = false; // False when the slides should be left alone, such as after manualStop

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
            targetTicks = DOWN_POSITION;
            currentSlidePosition = FlowerSlidePosition.DOWN;
        } else if (targetPosition == FlowerSlidePosition.UP) {
            targetTicks = UP_POSITION;
            currentSlidePosition = FlowerSlidePosition.UP;
        }
        active = true;
    }

    /**
     * Runs the PIDF controller for the slides. Must be called once every loop.
     */
    public void update() {
        if (!active) return;

        int ticks = getCurrentSlidePositionTicks();
        double power;
        power = -slidePidf.smooth(targetTicks, ticks) + HOLD_POWER;
        power = Math.max(-EXTEND_POWER, Math.min(EXTEND_POWER, power));
        slideMotor.setPower(power);
    }

    /**
     * @return true if the slides are within POSITION_TOLERANCE of the current target position
     */
    public boolean isAtPosition() {
        int ticks = getCurrentSlidePositionTicks();
        if (currentSlidePosition == FlowerSlidePosition.DOWN) {
            return ticks <= DOWN_POSITION + POSITION_TOLERANCE;
        }
        return Math.abs(ticks - UP_POSITION) <= POSITION_TOLERANCE;
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
        active = false; // update() must not override manual test power
        slideMotor.setDirection(DcMotor.Direction.FORWARD); // flip if up is down
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Cuts all power to the motors, making them float down to lower position.
     */
    public void manualStop() {
        // TODO check code
        active = false; // Stops update() from reapplying power
        slideMotor.setPower(0);
        // Once the slides reach the lower position, the position is set to down
        currentSlidePosition = FlowerSlidePosition.DOWN;
    }

    /**
     * Resets the motors and their encoders in case of emergency.
     * The encoder is re-zeroed wherever the slides are, so they must be manually lowered
     * to the bottom before calling setSlidePosition() again.
     */
    public void fullReset() {
        // TODO Write great code
        active = false; // Encoder zero is wiped, so update() must not drive toward the old tick targets
        manualStop();
        // TODO make update logic
        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Turn the motors back on
        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}