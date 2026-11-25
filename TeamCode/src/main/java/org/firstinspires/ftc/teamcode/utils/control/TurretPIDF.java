package org.firstinspires.ftc.teamcode.utils.control;

public class TurretPIDF {
    public static final double leftLimitDeg = 0;
    public static final double rightLimitDeg = -271;
    public static final double deadStart = 15;

    public static double p, i, d, f, kS, kV;

    private double prev = 0;
    private double integral = 0;
    private double filteredDeriv = 0;
    private long prev_time = 0;
    private boolean started = false;

    public static final double epsilon = 1e-2;

    public TurretPIDF(double P, double I, double D, double F){
        set_values(P, I, D, F);
    }

    // reset all running values to zero. If you are using the pid after waiting a while, this is basically a must.
    // Keeps pidf values the same
    public void reset(){
        prev = 0;
        integral = 0;
        prev_time = 0;
        started = false;
    }

    public void set_values(double P, double I, double D, double F){
        p = P;
        i = I;
        d = D;
        f = F;
    }
    public double smooth(double target_reading, double current_reading){
        double error = target_reading - current_reading;

        // System.nanoTime() may be more accurate if needed, but is less performant
        double time_step = (double)(System.currentTimeMillis() - prev_time) / 1000.0;
        prev_time = System.currentTimeMillis();

        // if pid not yet started, it may have been a while since it was initialized
        if (!started || time_step <= 0 || time_step > 1.0){
            time_step = 0;
        }

        // (P)
        double proportianal = error * p;

        // (I)
        integral += error * time_step;
        // Reset integral when we have reached target
        if (Math.abs(error) < epsilon){
            integral = 0;
        }
        double integ = i * integral;

        // can't divide by zero (D)
        double deriv = 0;
        if(time_step != 0){
            deriv = -d * (current_reading - prev) / time_step;
        }

        double alpha = 0.8;
        filteredDeriv = alpha * filteredDeriv + (1.0 - alpha) * deriv;

        prev = current_reading;
        started = true;
        return proportianal + integ + filteredDeriv + f;
    }

    /**
     * Degrees
     * @param target_reading
     * @param current_reading
     * @return
     */
    public double smoothAngle(double target_reading, double current_reading){
        if (angle_difference(target_reading, leftLimitDeg) > 0 && angle_difference(target_reading, rightLimitDeg) < 0) {
            if (Math.abs(angle_difference(current_reading, leftLimitDeg)) < Math.abs(angle_difference(current_reading, rightLimitDeg))) {
                target_reading = leftLimitDeg;
            } else {
                target_reading = rightLimitDeg;
            }
        }
        double error = pathError(target_reading, current_reading);

        // System.nanoTime() may be more accurate if needed, but is less performant
        double time_step = (double)(System.currentTimeMillis() - prev_time) / 1000.0;
        prev_time = System.currentTimeMillis();

        // if pid not yet started, it may have been a while since it was initialized
        if (!started || time_step <= 0 || time_step > 1.0){
            time_step = 0;
        }

        // (P)
        double proportianal = error * p;

        // (I)
        integral += error * time_step;
        // Reset integral when we have reached target
        if (Math.abs(error) < epsilon){
            integral = 0;
        }
        double integ = i * integral;

        // can't divide by zero (D)
        double deriv = 0;
        if(time_step != 0){
            deriv = -d * angle_difference(current_reading, prev) / time_step;
        }

        double alpha = 0.8;
        filteredDeriv = alpha * filteredDeriv + (1.0 - alpha) * deriv;

        prev = current_reading;
        started = true;
        return proportianal + integ + filteredDeriv + f;
    }

    /**
     * Find the signed difference between two angles
     * @param a degrees
     * @param b degrees
     * @return degrees
     */
    public static double angle_difference(double a, double b){
        return ((a - b + 540) % 360 + 360) % 360 - 180;
    }

    private boolean inDeadZone(double angle) {
        return angle_difference(angle, leftLimitDeg) > 0
                && angle_difference(angle, rightLimitDeg) < 0;
    }

    /** Signed error current -> target that never sweeps through the dead zone. */
    private double pathError(double target, double current) {
        // already inside the zone: nothing to avoid, just take the short way
        if (inDeadZone(current)) return angle_difference(target, current);

        double ccw = mod360(target - current);            // counterclockwise distance to target
        double zoneStart = mod360(leftLimitDeg - current); // counterclockwise distance to the zone

        // if we reach the target before the zone, go ccw, otherwise go clockwise the long way
        return ccw <= zoneStart ? ccw : ccw - 360.0;
    }

    public static double angleDiffAroundDeadZone(double from, double to) {
        double ccw = angle_difference(to, from);          // counterclockwise distance to target
        double zone = angle_difference(deadStart, from);  // counterclockwise distance to the zone

        return ccw <= zone ? ccw : ccw - 360.0;  // else go clockwise the long way
    }

    private static double mod360(double a) {
        return ((a % 360.0) + 360.0) % 360.0;
    }
}
