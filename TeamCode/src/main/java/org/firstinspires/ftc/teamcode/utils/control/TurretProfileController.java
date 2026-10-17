package org.firstinspires.ftc.teamcode.utils.control;

import com.qualcomm.robotcore.util.Range;

/**
 * Thanks Victor!
 */
public class TurretProfileController {

    public static double leftLimitDeg = 104.0; // TODO
    public static double rightLimitDeg = -118.0; // TODO
    public static double limitSlowZoneDeg = 12.0;

    // These are static btw now if that ever causes problems
    public static double kP = 0.016; // TODO
    public static double kD = 0.001; // TODO

    public static double kS = 0.01; // TODO
    public static double kV = 0.0006; // TODO
    public static double kA = 0.0;

    public double maxVelDeg = 900.0;
    public double maxAccelDeg = 900.0;

    public double posToleranceDeg = 1.0;
    public double velToleranceDegPerSec = 8.0;

    public double kSVelThresholdDegPerSec = 5.0;
    public double kSPosErrorThresholdDeg = 1.0;

    public double snapPosErrorDeg = 0.2;
    public double snapVelDegPerSec = 5.0;

    public double maxPower = 1.0;

    public double goalAngleDeg;

    private double profPosDeg;
    private double profVelDegPerSec;
    private double profAccelDegPerSec2;

    public double angleDeg;
    private double velocityDegPerSec;

    private boolean fieldLockEnabled = false;
    private double fieldTargetDeg = 0.0;

    private double lastPower = 0.0;
    private double lastPosError = 0.0;
    private double lastVelError = 0.0;
    private double lastFF = 0.0;
    private double lastFB = 0.0;
    private double lastOmegaTargetDegPerSec = 0.0;

    public TurretProfileController() {
        reset(leftLimitDeg);
    }

    public void reset(double currentAngleDeg) {
        angleDeg = clampAngle(currentAngleDeg);
        velocityDegPerSec = 0.0;

        goalAngleDeg = angleDeg;
        profPosDeg = angleDeg;
        profVelDegPerSec = 0.0;
        profAccelDegPerSec2 = 0.0;

        fieldLockEnabled = false;
        fieldTargetDeg = 0.0;

        lastPower = 0.0;
        lastPosError = 0.0;
        lastVelError = 0.0;
        lastFF = 0.0;
        lastFB = 0.0;
        lastOmegaTargetDegPerSec = 0.0;
    }

    /**
     * Logic-only update.
     *
     * @param dt loop time in seconds
     * @param measuredAngleDeg measured turret angle in robot frame
     * @param measuredVelocityDegPerSec measured turret angular velocity
     * @param robotHeadingDeg robot heading, CCW positive
     * @param robotHeadingRateDegPerSec robot angular velocity, CCW positive
     * @return commanded motor power [-maxPower, +maxPower]
     */
    public double update(
            double dt,
            double measuredAngleDeg,
            double measuredVelocityDegPerSec,
            double robotHeadingDeg,
            double robotHeadingRateDegPerSec
    ) {
        if (dt <= 0.0) {
            return lastPower;
        }

        angleDeg = clampAngle(measuredAngleDeg);
        velocityDegPerSec = measuredVelocityDegPerSec;

        double desiredRobotFrameAngleDeg;
        double desiredRobotFrameVelDegPerSec;

        if (fieldLockEnabled) {
            desiredRobotFrameAngleDeg = clampAngle(normalize180(fieldTargetDeg + robotHeadingDeg));
            desiredRobotFrameVelDegPerSec = -robotHeadingRateDegPerSec;
        } else {
            desiredRobotFrameAngleDeg = goalAngleDeg;
            desiredRobotFrameVelDegPerSec = 0.0;
        }

        goalAngleDeg = clampAngle(desiredRobotFrameAngleDeg);

        updateProfile(dt, goalAngleDeg);

        double omegaTargetDegPerSec = profVelDegPerSec + desiredRobotFrameVelDegPerSec;
        double alphaTargetDegPerSec2 = profAccelDegPerSec2;

        double posError = profPosDeg - angleDeg;
        double velError = omegaTargetDegPerSec - velocityDegPerSec;

        double fb = kP * posError + kD * velError;

        double ff = 0.0;
        double motionSign = 0.0;

        if (Math.abs(omegaTargetDegPerSec) > kSVelThresholdDegPerSec) {
            motionSign = Math.signum(omegaTargetDegPerSec);
        } else if (Math.abs(posError) > kSPosErrorThresholdDeg) {
            motionSign = Math.signum(posError);
        }

        if (motionSign != 0.0) {
            ff += kS * motionSign;
        }

        ff += kV * omegaTargetDegPerSec;
        ff += kA * alphaTargetDegPerSec2;

        double power = fb + ff;
        power = applyLimitProtection(power, angleDeg);
        power = Range.clip(power, -maxPower, maxPower);

        lastPosError = posError;
        lastVelError = velError;
        lastFF = ff;
        lastFB = fb;
        lastOmegaTargetDegPerSec = omegaTargetDegPerSec;
        lastPower = power;

        return power;
    }

    private void updateProfile(double dt, double targetDeg) {
        double error = targetDeg - profPosDeg;

        if (Math.abs(error) < 1e-6 && Math.abs(profVelDegPerSec) < 1e-6) {
            profPosDeg = targetDeg;
            profVelDegPerSec = 0.0;
            profAccelDegPerSec2 = 0.0;
            return;
        }

        double maxVelAtDistance = Math.sqrt(
                2.0 * Math.max(maxAccelDeg, 1e-6) * Math.abs(error)
        );

        double targetVel = Math.signum(error) * Math.min(maxVelDeg, maxVelAtDistance);

        double oldVel = profVelDegPerSec;
        double velStep = maxAccelDeg * dt;

        if (profVelDegPerSec < targetVel) {
            profVelDegPerSec = Math.min(profVelDegPerSec + velStep, targetVel);
        } else {
            profVelDegPerSec = Math.max(profVelDegPerSec - velStep, targetVel);
        }

        profAccelDegPerSec2 = (profVelDegPerSec - oldVel) / dt;
        profPosDeg += profVelDegPerSec * dt;

        if (profPosDeg >= leftLimitDeg) {
            profPosDeg = leftLimitDeg;
            if (profVelDegPerSec > 0.0) profVelDegPerSec = 0.0;
        }

        if (profPosDeg <= rightLimitDeg) {
            profPosDeg = rightLimitDeg;
            if (profVelDegPerSec < 0.0) profVelDegPerSec = 0.0;
        }

        if (Math.abs(targetDeg - profPosDeg) < snapPosErrorDeg
                && Math.abs(profVelDegPerSec) < snapVelDegPerSec) {
            profPosDeg = targetDeg;
            profVelDegPerSec = 0.0;
            profAccelDegPerSec2 = 0.0;
        }
    }

    private double applyLimitProtection(double power, double currentAngleDeg) {
        if (currentAngleDeg >= leftLimitDeg && power > 0.0) return 0.0;
        if (currentAngleDeg <= rightLimitDeg && power < 0.0) return 0.0;

        if (currentAngleDeg > leftLimitDeg - limitSlowZoneDeg && power > 0.0) {
            double scale = (leftLimitDeg - currentAngleDeg) / limitSlowZoneDeg;
            scale = Range.clip(scale, 0.15, 1.0);
            power *= scale;
        }

        if (currentAngleDeg < rightLimitDeg + limitSlowZoneDeg && power < 0.0) {
            double scale = (currentAngleDeg - rightLimitDeg) / limitSlowZoneDeg;
            scale = Range.clip(scale, 0.15, 1.0);
            power *= scale;
        }

        return power;
    }

    private double clampAngle(double deg) {
        return Range.clip(deg, rightLimitDeg, leftLimitDeg);
    }

    public double normalize180(double deg) {
        while (deg > 180.0) deg -= 360.0;
        while (deg <= -180.0) deg += 360.0;
        return deg;
    }

    public void setRobotRelativeTargetDeg(double targetDeg) {
        fieldLockEnabled = false;
        goalAngleDeg = clampAngle(targetDeg);
    }

    public void setFieldTargetDeg(double targetFieldDeg) {
        fieldLockEnabled = true;
        fieldTargetDeg = normalize180(targetFieldDeg);
    }

    public boolean isFieldLockEnabled() {
        return fieldLockEnabled;
    }

    public double getFieldTargetDeg() {
        return fieldTargetDeg;
    }

    public double getGoalAngleDeg() {
        return goalAngleDeg;
    }

    public double getProfileAngleDeg() {
        return profPosDeg;
    }

    public double getProfileVelDegPerSec() {
        return profVelDegPerSec;
    }

    public double getProfileAccelDegPerSec2() {
        return profAccelDegPerSec2;
    }

    public double getAngleDeg() {
        return angleDeg;
    }

    public double getVelocityDegPerSec() {
        return velocityDegPerSec;
    }

    public double getLastPower() {
        return lastPower;
    }

    public double getLastPosError() {
        return lastPosError;
    }

    public double getLastVelError() {
        return lastVelError;
    }

    public double getLastFF() {
        return lastFF;
    }

    public double getLastFB() {
        return lastFB;
    }

    public double getLastOmegaTargetDegPerSec() {
        return lastOmegaTargetDegPerSec;
    }

    public boolean atTarget() {
        return Math.abs(goalAngleDeg - angleDeg) < posToleranceDeg
                && Math.abs(lastOmegaTargetDegPerSec - velocityDegPerSec) < velToleranceDegPerSec;
    }
}