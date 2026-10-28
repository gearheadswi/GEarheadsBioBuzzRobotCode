package org.firstinspires.ftc.teamcode.subsystems;

import com.pedropathing.ivy.Command;
import static com.pedropathing.ivy.commands.Commands.*;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.utils.AimingCalculator;
import org.firstinspires.ftc.teamcode.utils.control.TurretProfileController;

public class Turret {
    private static final double CALIBRATING_POWER = 0.1; // TODO tune
    private static final double CALIBRATION_DELAY = 0.5; // Seconds // TODO tune
    private static final double CALIBRATION_MIN_VEL = 1; // Deg per second // TODO tune
    private static double ABSOLUTE_ENCODER_OFFSET = 1; // Degrees // TODO tune for absolute encoder

    private final CRServo powerServo1;
    private final CRServo powerServo2;

    private final AnalogInput absoluteEncoder;

    private TurretProfileController turretController;

    private boolean calibrating = false;
    private double calibrationStart;

    private double prevAbsolutePosition;

    public Turret(HardwareMap hardwareMap){
        powerServo1 = hardwareMap.get(CRServo.class, "turretBase1");
        powerServo2 = hardwareMap.get(CRServo.class, "turretBase2");
        powerServo1.setDirection(DcMotorSimple.Direction.FORWARD);
        powerServo2.setDirection(DcMotorSimple.Direction.FORWARD); // TODO determine correct directions

        absoluteEncoder = hardwareMap.get(AnalogInput.class, "turretEncoder");
    }

    /**
     * Gets current absolute encoder position in degrees where 0 degrees is forward and CCW is positive
     * @return rotation (degrees)
     */
    public double getAbsolutePosition(){ // TODO figure out which direction is positive, correct to be Counter-Clockwise
        return (absoluteEncoder.getVoltage() / 3.2 * 360 + ABSOLUTE_ENCODER_OFFSET + 360) % 360;
    }

    private void setServoPower(double power){
        powerServo1.setPower(power);
        powerServo2.setPower(power);
    }

    /**
     * Updates the turret controller and power
     * @param dt (Seconds)
     * @param robotHeading (Degrees) (CCW Positive)
     * @param robotRotationalVelocity (Degrees / Sec) (CCW Positive)
     */
    public void updateMovement(double dt, double robotHeading, double robotRotationalVelocity){
        double currentRotation = getAbsolutePosition();
        double turretVelocity = (currentRotation - prevAbsolutePosition) / dt; // TODO Confirm direction
        prevAbsolutePosition = currentRotation;

        if (calibrating) {
            double calibrationDuration = (System.currentTimeMillis() - calibrationStart) / 1000.0;
            if (calibrationDuration > CALIBRATION_DELAY && Math.abs(turretVelocity) < CALIBRATION_MIN_VEL){
                ABSOLUTE_ENCODER_OFFSET += TurretProfileController.rightLimitDeg - currentRotation;
                setServoPower(0);
                calibrating = false;
            }
        } else {
            double power = turretController.update(dt, currentRotation, turretVelocity, robotHeading, robotRotationalVelocity);
            setServoPower(power);
        }
    }

    /**
     * Starts calibration using endstop. Finishes in updateMovement method once turret stops moving
     */
    public void startCalibrate() {
        calibrating = true;
        calibrationStart = System.currentTimeMillis();
        setServoPower(CALIBRATING_POWER);
    }

    public boolean isCalibrating(){
        return calibrating;
    }

    /**
     * Sets angle turret tries to aim towards with 0 degrees being forward and positive being CCW
     * @param angleOffset of turret
     */
    public void setTurretAimOffset(double angleOffset) {
        turretController.setRobotRelativeTargetDeg(angleOffset);
    }

    // Ivy

    public Command setAngleCommand(double target){
        return instant(() -> setTurretAimOffset(target));
    }
}
