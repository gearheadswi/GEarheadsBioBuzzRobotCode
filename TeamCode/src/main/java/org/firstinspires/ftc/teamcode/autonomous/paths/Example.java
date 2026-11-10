package org.firstinspires.ftc.teamcode.autonomous.paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import static com.pedropathing.api.Paths.*;
import com.pedropathing.paths.Path;

import static com.pedropathing.ivy.groups.Groups.*;
import static com.pedropathing.ivy.pedro.PedroCommands.*;

import org.firstinspires.ftc.teamcode.autonomous.tuning.CommonPoints;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Brakes;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.TeamColor;

@Autonomous(name = "Preloads Garden Side")
public class Example extends OpMode {
    private PoseFactory poseFactory = PoseFactory.degrees();
    private Follower follower;
    private TeamColor team = TeamColor.RED;
    private CommonPoints commonPoints = new CommonPoints(poseFactory);

    private Robot robot;
    private IntakeTransfer intakeTransfer;
    private Turret turret;
    private Launcher launcher;
    private Brakes brakes;

    private Command autoSequence;

    private Path toPark;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(poseFactory.of(0, 0, 0));
        buildSequence();
        buildPaths();
        printSetTeam();
        Scheduler.reset();
    }

    private void printSetTeam(){
        telemetry.addData("Set Team", team);
        telemetry.addLine("A to switch team");
        telemetry.update();
    }

    @Override
    public void init_loop(){
        if (gamepad1.aWasPressed() || gamepad2.aWasPressed()){
            team = (team == TeamColor.RED) ? TeamColor.BLUE : TeamColor.RED;
            robot.teamName = team;
            printSetTeam();
            if (team == TeamColor.RED) {
                poseFactory = PoseFactory.degrees();
            } else {
                poseFactory = PoseFactory.degrees().mirrorAroundPoint(70.75, 70.75); // Mirrors around center
            }
            buildPaths();
            buildSequence();
        }
    }

    private void buildSequence(){
        autoSequence = sequential(
                intakeTransfer.shootCommand,
                follow(follower, toPark)
        );
    }

    private void buildPaths(){
        follower.setPose(commonPoints.startGardenShoot);
        commonPoints = new CommonPoints(poseFactory);
        toPark = curve(commonPoints.startGardenShoot,
                    poseFactory.of(19, 30, 0),
                    commonPoints.parkTowardsGarden)
                .linear(commonPoints.startGardenShoot, commonPoints.parkTowardsGarden);
    }

    @Override
    public void start(){
        robot.startSequenceAuto();
        autoSequence.execute();
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
    }
}
