package org.firstinspires.ftc.teamcode.autonomous.paths;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.parallel;
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

@Autonomous(name = "4 Preloads Garden Side + Second Tip + Extra")
public class PreloadsGardenSidePlusSecondTipPlusExtra extends OpMode {
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

    private Path toFlower1;
    private Path toShoot1;
    private Path toFlower2;
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
                intakeTransfer.shootCommand,
                intakeTransfer.intakeArmDownCommand,
                follow(follower, toFlower1),
                intakeTransfer.waitUntilFullCommand,
                parallel(
                        sequential(
                                waitMs(1500),
                                intakeTransfer.intakeArmUpCommand
                        ),
                        follow(follower, toShoot1)
                ),
                intakeTransfer.shootCommand,
                intakeTransfer.intakeArmDownCommand,
                follow(follower, toFlower2),
                intakeTransfer.waitUntilFullCommand,
                intakeTransfer.shootCommand,
                parallel(
                        sequential(
                                waitMs(1500),
                                intakeTransfer.intakeArmUpCommand
                        ),
                        follow(follower, toPark)
                )
        );
    }

    private void buildPaths(){
        follower.setPose(commonPoints.startGardenShoot);
        commonPoints = new CommonPoints(poseFactory);
        toFlower1 = curve(commonPoints.startGardenShoot,
                    poseFactory.of(20.59, 104.82, 0),
                    commonPoints.flowerPickupGarden)
                .linear(commonPoints.startGardenShoot, commonPoints.flowerPickupGarden);
        toShoot1 = curve(commonPoints.flowerPickupGarden,
                commonPoints.frontHiveShotLoading)
                .linear(commonPoints.flowerPickupGarden, commonPoints.frontHiveShotLoading);
        toFlower2= curve(commonPoints.frontHiveShotLoading,
                commonPoints.flowerPickupLoading)
                .linear(commonPoints.frontHiveShotLoading, commonPoints.flowerPickupLoading);
        toPark= curve(commonPoints.flowerPickupLoading,
                commonPoints.parkTowardsLoading)
                .linear(commonPoints.flowerPickupLoading, commonPoints.parkTowardsLoading);
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
