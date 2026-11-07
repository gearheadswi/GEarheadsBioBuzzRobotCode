package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.Brakes;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
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

    public IntakeTransfer intake;
    public Turret turret;
    public Launcher launcher;
    public Brakes brakes;

    public Robot(OpMode opMode, TeamColor team) {
        hardwareMap = opMode.hardwareMap;
        teamName = team;
    }

    /** Instantiates all subsystems. Call once inside {@code OpMode.init()}. */
    public void initSubsystems() {
        intake = new IntakeTransfer(hardwareMap);
        turret = new Turret(hardwareMap);
        launcher = new Launcher(hardwareMap);
        brakes = new Brakes(hardwareMap);
    }

    public void intiSequencesTeleop(){
    }

    public void intiSequencesAuto(){
    }

    public void startSequenceTele(){
        intake.flowerIntakeArmUp();
        intake.activateIntake();
        intake.stopBallRelease();
        launcher.launchOn();
        brakes.release();
    }
}
