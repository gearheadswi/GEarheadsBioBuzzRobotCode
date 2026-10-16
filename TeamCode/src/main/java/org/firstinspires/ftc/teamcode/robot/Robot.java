package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.hardware.Imu;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.utils.TeamColor;

import static com.pedropathing.ivy.groups.Groups.repeat;

/**
 * Owns all hardware subsystems for the robot.
 * Constructor is lightweight (no hardware I/O); call {@link #initSubsystems()} inside
 * {@code OpMode.init()} to actually bring up hardware.
 */
public class Robot {
    public TeamColor teamName;
    public HardwareMap hardwareMap;
    public Imu imu;

    public Robot(OpMode opMode, TeamColor team) {
        hardwareMap = opMode.hardwareMap;
        teamName = team;
    }

    /** Instantiates all subsystems. Call once inside {@code OpMode.init()}. */
    public void initSubsystems() {
        imu = new Imu(hardwareMap);
    }

    public void intiSequencesTeleop(){
    }

    public void intiSequencesAuto(){
    }
}
