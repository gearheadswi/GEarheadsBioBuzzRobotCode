package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.ValueStorage;
import org.firstinspires.ftc.teamcode.autonomous.tuning.Constants;
import org.firstinspires.ftc.teamcode.robot.Robot;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Teleop extends OpMode {
    private static final PoseFactory poseFactory = PoseFactory.degrees();
    private static final Pose DEFAULT_START_POSE_RED = poseFactory.of(31, 8, 90);
    private static final Pose DEFAULT_START_POSE_BLUE = poseFactory.of(110, 113.5, -90);
    private Robot robot;

    private Follower follower;

    @Override
    public void init() {
        Scheduler.reset();

        robot = new Robot(this, false);
        robot.initSubsystems();
//        robot.intiSequencesTeleop();

        follower = Constants.create(hardwareMap);

        if (ValueStorage.fromAuto) {
            ValueStorage.fromAuto = false;
            follower.setPose(ValueStorage.auto_pose);
        } else {
            follower.setPose(DEFAULT_START_POSE_RED); // TODO Alliance
        }
    }

    @Override
    public void init_loop() {
//        Scheduler.execute();
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading() // TODO make work with dif alliances
        );

//        ManualDrive.driveOrHold(follower, powers);
        follower.manual(powers);

        // Update stuff
        follower.update();
        Scheduler.execute();
    }
}