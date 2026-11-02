package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ValueStorage;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.subsystems.IntakeTransfer;
import org.firstinspires.ftc.teamcode.utils.TeamColor;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    private static final PoseFactory poseFactory = PoseFactory.degrees();
    private static final Pose DEFAULT_START_POSE_RED = poseFactory.of(8, 102.7, 0);
    private static final Pose DEFAULT_START_POSE_BLUE = poseFactory.of(133.5, 38.6, 180);
    private Robot robot;

    private Follower follower;
    private IntakeTransfer intakeTransfer;

    private TeamColor team = TeamColor.RED;

    private boolean grabbingFlower = false;

    @Override
    public void init() {
        Scheduler.reset();

        robot = new Robot(this, team);
        robot.initSubsystems();
//        robot.intiSequencesTeleop();

        intakeTransfer = robot.intake;

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
    }

    @Override
    public void loop() {
        double driveForce = gamepad1.right_trigger_pressed ? 0.33 : 1;
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y * driveForce,
                -gamepad1.left_stick_x * driveForce,
                -gamepad1.right_stick_x * driveForce,
                follower.pose().heading() + ((team == TeamColor.BLUE) ? Math.PI : 0)
        );

        if (gamepad1.psWasPressed()){
            follower.setHeading(team == TeamColor.RED ? DEFAULT_START_POSE_RED.heading() : DEFAULT_START_POSE_BLUE.heading());
        }

        // INTAKE/TRANSFER Block
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

//        ManualDrive.driveOrHold(follower, powers);
        follower.manual(powers);

        // Update stuff
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