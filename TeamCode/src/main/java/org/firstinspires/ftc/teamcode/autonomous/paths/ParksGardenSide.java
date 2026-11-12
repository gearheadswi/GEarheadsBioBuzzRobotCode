package org.firstinspires.ftc.teamcode.autonomous.paths;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ValueStorage;
import org.firstinspires.ftc.teamcode.autonomous.tuning.CommonPoints;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Brakes;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.TeamColor;

@Autonomous(name = "1a Park Loading Side")
public class ParksGardenSide extends OpMode {
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

        robot.initSubsystems();
        intakeTransfer = robot.intake;
        turret = robot.turret;
        launcher = robot.launcher;
        brakes = robot.brakes;

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
                follow(follower, toPark)
        );
    }

    private void buildPaths(){
        follower.setPose(commonPoints.startGardenShoot);
        commonPoints = new CommonPoints(poseFactory);
        toPark = curve(commonPoints.startParkTowardsGarden,
                    commonPoints.parkTowardsGarden)
                .linear(commonPoints.startParkTowardsGarden, commonPoints.parkTowardsGarden);
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

    @Override
    public void stop() {
        ValueStorage.fromAuto = true;
        ValueStorage.auto_pose = follower.pose();
        ValueStorage.autoTeam = team;
    }
}
