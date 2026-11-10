package org.firstinspires.ftc.teamcode.autonomous.tuning;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.utils.TeamColor;

/**
 * All points are assumed to be on red side by default.
 * Operations can be applied to the factory
 */
public class CommonPoints {
    public CommonPoints(PoseFactory poseFactory){
        startParkTowardsLoading = poseFactory.of(0, 0, 0);
        startParkTowardsGarden = poseFactory.of(0, 0, 0);
        startGardenShoot = poseFactory.of(0, 0, 0);
        startLoadingShoot = poseFactory.of(0, 0, 0);
        parkTowardsLoading = poseFactory.of(0, 0, 0);
        parkTowardsGarden = poseFactory.of(0, 0, 0);
    }

    // Parking start positions
    public Pose startParkTowardsLoading;
    public Pose startParkTowardsGarden;

    // Shooting start positions
    public Pose startGardenShoot;
    public Pose startLoadingShoot;

    // Park positions
    public Pose parkTowardsLoading;
    public Pose parkTowardsGarden;
}
