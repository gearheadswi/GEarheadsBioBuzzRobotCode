package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.utils.TeamColor;

/**
 * Static bridge for passing the robot's final autonomous pose into teleop.
 * FTC OpModes are separate instances, so statics are the only way to carry
 * state across the auto → teleop boundary within a single power cycle.
 */
public class ValueStorage {
    public static boolean fromAuto = false;
    public static Pose auto_pose = new Pose(0, 0, 0);
    public static TeamColor autoTeam = TeamColor.RED;
}
