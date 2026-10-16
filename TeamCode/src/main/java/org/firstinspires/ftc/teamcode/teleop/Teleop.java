package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ValueStorage;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.AimingCalculator;
import org.firstinspires.ftc.teamcode.utils.TeamColor;
import org.firstinspires.ftc.teamcode.utils.AimingCalculator.TheoreticalLaunchConfiguration;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    private static final PoseFactory poseFactory = PoseFactory.degrees();
    private static final Pose DEFAULT_START_POSE_RED = poseFactory.of(31, 8, 90);
    private static final Pose DEFAULT_START_POSE_BLUE = poseFactory.of(110, 113.5, -90);
    private final TheoreticalLaunchConfiguration defaultLaunchConfig = new TheoreticalLaunchConfiguration(
            0,
            45,
            1
    );

    private Robot robot;
    private TeamColor team = TeamColor.RED;

    private Turret turret;

    private Follower follower;

    private double lastUpdate = 0; // Milliseconds

    // When true, sets launch config to default launch config
    private boolean launchOverride = false;

    @Override
    public void init() {
        Scheduler.reset();

        robot = new Robot(this, team);
        robot.initSubsystems();

        turret = robot.turret;

//        robot.intiSequencesTeleop();

        follower = Constants.create(hardwareMap);

        if (ValueStorage.fromAuto) {
            ValueStorage.fromAuto = false;
            team = ValueStorage.autoTeam;
            robot.teamName = team;
            follower.setPose(ValueStorage.auto_pose);
        } else {
            setStartPos();
        }
        printSetTeam();
    }

    @Override
    public void init_loop() {
//        Scheduler.execute();
        if (gamepad1.aWasPressed() || gamepad2.aWasPressed()){
            team = (team == TeamColor.RED) ? TeamColor.BLUE : TeamColor.RED;
            robot.teamName = team;
            printSetTeam();
        }
        if (gamepad1.yWasPressed() || gamepad2.yWasPressed()){
            setStartPos();
            telemetry.addData("Set default position for ", team);
            telemetry.update();
        }
    }

    @Override
    public void start() {
        getDt();
    }

    @Override
    public void loop() {
        double robotHeading = follower.pose().heading();
        double robotHeadingVel = follower.velocity().omega;
        Pose robotPose = follower.pose();
        double dt = getDt();

//        // Main Controls
//        if (gamepad1.xWasPressed()) {
//            launchOverride = !launchOverride;
//        }
//
//        // Aiming / Ranging Control
//        TheoreticalLaunchConfiguration launchConfig;
//        if (launchOverride){
//            launchConfig = defaultLaunchConfig;
//        } else {
//            launchConfig = AimingCalculator.aimAtTarget(
//                    team,
//                    robotPose
//            );
//        }
//
//        turret.setTurretAimOffset(launchConfig.getTurretAngle());
//
//        turret.updateMovement(
//                dt,
//                Math.toDegrees(robotHeading),
//                Math.toDegrees(robotHeadingVel)
//        );

        // Drivetrain
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading() + ((team == TeamColor.BLUE) ? Math.PI : 0)
        );

//        ManualDrive.driveOrHold(follower, powers);
        follower.manual(powers);

        // Update stuff
        follower.update();
        Scheduler.execute();
    }

    /**
     * Gets dt since last getDt call
     * @return dt (Seconds)
     */
    private double getDt(){
        double curTime = System.currentTimeMillis();
        double dt = (curTime - lastUpdate) / 1000;
        lastUpdate = curTime;
        return dt;
    }

    private void setStartPos(){
        if (team == TeamColor.RED){
            follower.setPose(DEFAULT_START_POSE_RED);
        } else {
            follower.setPose(DEFAULT_START_POSE_BLUE);
        }
    }

    private void printSetTeam(){
        telemetry.addData("Set Team", team);
        telemetry.update();
    }
}