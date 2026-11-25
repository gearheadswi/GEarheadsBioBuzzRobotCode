package org.firstinspires.ftc.teamcode.autonomous.paths;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.commands.Commands.waitMs;
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

@Autonomous(name = "5) Preloads Loading Side")
public class PreloadsLoadingSideSide extends OpMode {
    private PoseFactory poseFactory = PoseFactory.degrees();
    private Follower follower;
    private TeamColor team = TeamColor.RED;
    private CommonPoints commonPoints = new CommonPoints(poseFactory);

    private long prevTimeStamp = 0;

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

        robot = new Robot(this, team);

        robot.initSubsystems();
        intakeTransfer = robot.intake;
        turret = robot.turret;
        launcher = robot.launcher;
        brakes = robot.brakes;

        buildPaths();
        buildSequence();
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
                waitMs(10 * 1000),
                intakeTransfer.fullAssistedShot,
                waitMs(2000),
                follow(follower, toPark)
        );
    }

    private void buildPaths(){
        commonPoints = new CommonPoints(poseFactory);
        follower.setPose(commonPoints.startLoadingShoot);
        toPark = curve(commonPoints.startLoadingShoot,
                    poseFactory.of(42,96, 0),
                    commonPoints.parkTowardsLoading)
                .linear(commonPoints.startLoadingShoot, commonPoints.parkTowardsLoading);
    }

    @Override
    public void start(){
        robot.startSequenceAuto();
        Scheduler.schedule(autoSequence);
        launcher.setTargetSpeed(1620);
        turret.setTurretAimOffset(-270);
    }

    @Override
    public void loop() {
        double dt = (System.currentTimeMillis() - prevTimeStamp) / 1000.0;
        prevTimeStamp = System.currentTimeMillis();

        intakeTransfer.updateCurrentLimiting();
        intakeTransfer.updateBallCount();
        turret.updateMovement(dt, follower.pose().heading(), 0);

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
