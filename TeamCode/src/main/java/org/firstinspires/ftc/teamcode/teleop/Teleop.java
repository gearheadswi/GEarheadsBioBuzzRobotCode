package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ShotParams;
import org.firstinspires.ftc.teamcode.ValueStorage;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.subsystems.Brakes;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.teamcode.utils.AimingCalculator;
import org.firstinspires.ftc.teamcode.utils.AimingCalculator.TheoreticalLaunchConfiguration;
import org.firstinspires.ftc.teamcode.utils.TeamColor;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    private static final double MAX_BRAKE_SPEED = 3; // In/sec

    private static final PoseFactory poseFactory = PoseFactory.degrees();
    private static final Pose DEFAULT_START_POSE_RED = poseFactory.of(8, 102.7, 0);
    private static final Pose DEFAULT_START_POSE_BLUE = poseFactory.of(133.5, 38.6, 180);
    private static final TheoreticalLaunchConfiguration DEFAULT_LAUNCH_CONFIG = new TheoreticalLaunchConfiguration(
            -90,
            0,
            1500,
            0
    );
    private Robot robot;

    private Follower follower;
    private IntakeTransfer intakeTransfer;
    private Turret turret;
    private Launcher launcher;
    private Brakes brakes;

    private TeamColor team = TeamColor.RED;

    private boolean grabbingFlower = false;
    private boolean aimOverride = true;

    private long prevTimeStamp = 0;

    @Override
    public void init() {
        Scheduler.reset();

        robot = new Robot(this, team);
        robot.initSubsystems();
//        robot.intiSequencesTeleop();

        intakeTransfer = robot.intake;
        turret = robot.turret;
        launcher = robot.launcher;
        brakes = robot.brakes;

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

    private void setStartPos(){
        if (team == TeamColor.RED){
            follower.setPose(DEFAULT_START_POSE_RED);
        } else {
            follower.setPose(DEFAULT_START_POSE_BLUE);
        }
    }

    private void printSetTeam(){
        telemetry.addData("Set Team", team);
        telemetry.addLine("A to switch team");
        telemetry.addLine("Y to reset start pos");
        printPositionInfo();
        telemetry.update();
    }

    private void printPositionInfo(){
        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Rotation", follower.pose().heading());
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
            printSetTeam();
            telemetry.update();
        }
    }

    @Override
    public void start() {
        robot.startSequenceTele();
        prevTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void loop() {
        // Positioning
        Pose robotPose = follower.pose();
        Velocity robotVelocity = follower.velocity();
        double dt = (System.currentTimeMillis() - prevTimeStamp) / 1000.0;
        prevTimeStamp = System.currentTimeMillis();

        // DRIVETRAIN
        double driveForce = gamepad1.right_trigger_pressed ? 0.33 : 1;
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y * driveForce,
                -gamepad1.left_stick_x * driveForce,
                -gamepad1.right_stick_x * driveForce,
                follower.pose().heading() + ((team == TeamColor.BLUE) ? Math.PI : 0)
        );

        if(
                (gamepad1.left_stick_x < 0.05 &&
                gamepad1.left_stick_y < 0.05 &&
                gamepad1.right_stick_x < 0.05 &&
                robotVelocity.toVector().magnitude() < MAX_BRAKE_SPEED)
                || gamepad1.right_bumper
        ) {
            if (!brakes.isBreaking()){
                brakes.brake();
            }
        } else {
            if (brakes.isBreaking()){
                brakes.release();
            }
        }

//        ManualDrive.driveOrHold(follower, powers);
        follower.manual(powers);

        if (gamepad1.psWasPressed()){
            follower.setHeading(team == TeamColor.RED ? DEFAULT_START_POSE_RED.heading() : DEFAULT_START_POSE_BLUE.heading());
        }

        // INTAKE/TRANSFER
        if (gamepad2.leftTriggerWasPressed()){
            intakeTransfer.reverseIntake();
        } else if (gamepad2.leftTriggerWasReleased()){
            intakeTransfer.activateIntake();
        }

        if (gamepad2.bWasPressed()){
            grabbingFlower = !grabbingFlower;
            if (grabbingFlower){
                intakeTransfer.flowerIntakeArmDown();
            } else {
                intakeTransfer.flowerIntakeArmUp();
            }
        }

        if (gamepad2.rightTriggerWasPressed()){
            intakeTransfer.startBallRelease();
        } else if (gamepad2.rightTriggerWasReleased()) {
            intakeTransfer.stopBallRelease();
        }

        // TURRET/AIMING
        if(gamepad1.yWasPressed()){
            aimOverride = !aimOverride;
        }
        if (aimOverride){
            turret.setTurretAimOffset(DEFAULT_LAUNCH_CONFIG.getTurretAngle());
            launcher.setTargetSpeed(DEFAULT_LAUNCH_CONFIG.getLauncherVelocity());
        } else {
            TheoreticalLaunchConfiguration aimingConfig = AimingCalculator.aimAtTarget(team, robotPose);
            turret.setTurretAimOffset(aimingConfig.getTurretAngle());
            double launcherSpeed = ShotParams.Velocity.forDistance(aimingConfig.getDistance());
            launcher.setTargetSpeed(launcherSpeed);
        }

        // Update stuff
        turret.updateMovement(dt, Math.toDegrees(robotPose.heading()), Math.toDegrees(robotVelocity.omega));
        intakeTransfer.updateCurrentLimiting();
        intakeTransfer.updateBallCount();
        follower.update();
        Scheduler.execute();

        // Telemetry
        telemetry.addData("Ball count", intakeTransfer.getBallCount());
        printPositionInfo();
        telemetry.update();
    }
}