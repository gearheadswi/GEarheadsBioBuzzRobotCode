package org.firstinspires.ftc.teamcode;

import java.util.Map;
import java.util.TreeMap;

/* Sample Usage
    double angle = ShotParams.LaunchAngle.forDistance(xxx);
    int velocity = ShotParams.Velocity.forDistance(xxx);
 */
public final class ShotParams {
    private static final TreeMap<Double, ShotEntry> TABLE = new TreeMap<>();
    private ShotParams() {}
    private static final class ShotEntry {
        final double distance;
        final double angle;
        final int velocity;

        ShotEntry(double distance, double angle, int velocity) {
            this.distance = distance;
            this.angle = angle;
            this.velocity = velocity;
        }
    }

    static {
        TABLE.put(20.0, new ShotEntry(20, 0.50, 1620));
        TABLE.put(25.0, new ShotEntry(25, 0.50, 1700));
    }

    public static final class Velocity {
        private Velocity() {}

        public static int forDistance(double distance) {
            InterpolatedShot s = interpolate(distance);
            return (int) Math.round(s.velocity);
        }
    }

    public static final class LaunchAngle {
        private LaunchAngle() {}

        public static double forDistance(double distance) {
            return interpolate(distance).angle;
        }
    }
    private static InterpolatedShot interpolate(double distance) {

        if (distance <= TABLE.firstKey()) {
            ShotEntry e = TABLE.firstEntry().getValue();
            return new InterpolatedShot(e.angle, e.velocity);
        }

        if (distance >= TABLE.lastKey()) {
            ShotEntry e = TABLE.lastEntry().getValue();
            return new InterpolatedShot(e.angle, e.velocity);
        }

        Map.Entry<Double, ShotEntry> lower = TABLE.floorEntry(distance);
        Map.Entry<Double, ShotEntry> upper = TABLE.ceilingEntry(distance);

        if (lower.getKey().equals(upper.getKey())) {
            ShotEntry e = lower.getValue();
            return new InterpolatedShot(e.angle, e.velocity);
        }

        ShotEntry lo = lower.getValue();
        ShotEntry hi = upper.getValue();

        double t = (distance - lo.distance) / (hi.distance - lo.distance);

        double interpAngle =
                lo.angle + t * (hi.angle - lo.angle);

        double interpVelocity =
                lo.velocity + t * (hi.velocity - lo.velocity);

        return new InterpolatedShot(interpAngle, interpVelocity);
    }

    private static final class InterpolatedShot {
        final double angle;
        final double velocity;

        InterpolatedShot(double angle, double velocity) {
            this.angle = angle;
            this.velocity = velocity;
        }
    }
}