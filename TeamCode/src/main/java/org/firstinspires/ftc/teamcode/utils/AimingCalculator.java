package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.math.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.math.Velocity;

public class AimingCalculator {
    // Left and right are according to the standard Pedro field orientation. You can look at the field using the pedro pathing visualizer
    public static final Pose BLUE_LEFT = new Pose(52.17, 83.5);
    public static final Pose BLUE_RIGHT = new Pose(89.33, 83.5);
    public static final Pose RED_LEFT = new Pose(52.17, 58);
    public static final Pose RED_RIGHT = new Pose(89.33, 58);

    public static final double FIELD_WIDTH = 144;

    /**
     * Y height of the center of the goal in the up position in Inches
     */
    public static final double UP_GOAL_HEIGHT = 59.55;

    /**
     * Height ball stops contacting robot in Inches
     */
    public static final double BALL_LEAVE_HEIGHT = 1; // TODO TUNE

    public static final double GRAVITY = 32.2; // In/s^2

    /**
     * Represents how much further the ball would hit the ground given distance to goal
     * Ex: If goal is 1 meter away, velocity and angle will target (1, goal height) and (1 * strength, 0)
     * If this ends up not working very well, change to use setpoint
     */
    public static final double SHOT_STRENGTH = 2; // TODO TUNE?

    public static class TheoreticalLaunchConfiguration {
        private final double turretAngle;
        private final double hoodAngle;
        private final double launcherVelocity;

        public TheoreticalLaunchConfiguration(double turretAngle, double hoodAngle, double launcherVelocity) {
            this.turretAngle = turretAngle;
            this.hoodAngle = hoodAngle;
            this.launcherVelocity = launcherVelocity;
        }

        public double getLauncherVelocity() {
            return launcherVelocity;
        }

        public double getHoodAngle() {
            return hoodAngle;
        }

        public double getTurretAngle() {
            return turretAngle;
        }
    }

    public static Pose getTargetPose(TeamColor team, Pose robotPose){
        boolean inLeftField = robotPose.x() < FIELD_WIDTH / 2;
        if (team == TeamColor.RED) {
            return inLeftField ? RED_LEFT : RED_RIGHT;
        }
        return inLeftField ? BLUE_LEFT : BLUE_RIGHT;
    }

    public static double getTargetDistance(TeamColor team, Pose robotPose) {
        Pose targetPose = getTargetPose(team, robotPose);
        return robotPose.distance(targetPose);
    }

    /**
     * Returns the offset from the heading of the robot that will aim towards the goal.
     * @param robotPose Units: (Inches, Radians)
     * @param target Position of target
     * @return Angle offset from robot heading (Radians)
     * positive -> Right of robot heading
     * negative -> Left of robot heading
     */
    public static double getAimOffsetFromHeading(Pose robotPose, Pose target) {
        double targetAngleFieldSpace = Math.atan2(
                target.y() - robotPose.y(),
                target.x() - robotPose.x()
        );

        return robotPose.heading() - targetAngleFieldSpace;
    }

    /**
     * Returns ideal aim angle given position
     * Needs to be converted to servo position to actually use
     * @param distance distance from target (inches)
     * @return Aim angle (Radians)
     */
    public static double getHoodAngle(double distance){
        return Math.atan(
                (UP_GOAL_HEIGHT - BALL_LEAVE_HEIGHT) * SHOT_STRENGTH
                        / ( distance * (SHOT_STRENGTH - 1) )
        );
    }

    /**
     * Returns ideal ball speed upon leaving launcher
     * Need to actual turn into motor velocity to use
     * @param aimAngle in Radians given by getHoodAngle
     * @param distance distance from target (inches)
     * @return Speed in In/s
     */
    public static double getTargetSpeed(double aimAngle, double distance){
        return Math.sqrt( SHOT_STRENGTH * GRAVITY * distance / (Math.sin(aimAngle) * Math.cos(aimAngle) * 2) );
    }

    public static TheoreticalLaunchConfiguration aimAtTarget(TeamColor team, Pose robotPose) {
        Pose target = getTargetPose(team, robotPose);
        double distance = getTargetDistance(team, robotPose);

        double hoodAngle = getHoodAngle(distance);
        double velocity = getTargetSpeed(hoodAngle, distance);
        double turretOffset = getAimOffsetFromHeading(robotPose, target);

        return new TheoreticalLaunchConfiguration(
                turretOffset,
                hoodAngle,
                velocity
        );
    }

    public static TheoreticalLaunchConfiguration aimAtTarget(TeamColor team, Pose robotPose, Velocity robotVelocity) {
        Pose target = getTargetPose(team, robotPose);
        Vector displacement = new Vector (
                target.x() - robotPose.x(),
                target.y() - robotPose.y()
        );
        double distance = displacement.magnitude();

        // + if going towards target, - for away
        double robotRadialVelocity = robotVelocity.toVector().dot(displacement) / distance;
        Vector perpindicularDisplacement = displacement.toVector2D().rotate(-Math.PI / 2).toVector(); // IDK if this uses radians because whoever made pedropathing is a idiot
        // If looking at target, + if it goes right, - for left
        double robotTangentialVelocity = robotVelocity.toVector().dot(perpindicularDisplacement) / distance;

        // Angle and velocity not compensating
        double basicHoodAngle = getHoodAngle(distance);
        double basicVelocity = getTargetSpeed(basicHoodAngle, distance);

        double targetVX = basicVelocity * Math.cos(basicHoodAngle) - robotRadialVelocity;
        double targetVY = robotTangentialVelocity;
        double targetVZ = basicVelocity * Math.sin(basicHoodAngle); // Vertical

        double targetVFlat = Math.sqrt( targetVX * targetVX + targetVY * targetVY );

        // Angle and velocity compensating
        double compensatedAngle = Math.atan( targetVZ / targetVFlat );
        double compensatedVel = Math.sqrt(
                targetVX * targetVX +
                targetVY * targetVY +
                targetVZ * targetVZ
        );

        Pose compensatedTarget = target.toVector2D().toVector().plus(
                robotVelocity.toVector().projectOnto(perpindicularDisplacement)
        ).toVector2D().toPose();

        double compensatedTurretOffset = getAimOffsetFromHeading(robotPose, compensatedTarget);

        return new TheoreticalLaunchConfiguration(
                compensatedTurretOffset,
                compensatedAngle,
                compensatedVel
        );
    }
}
