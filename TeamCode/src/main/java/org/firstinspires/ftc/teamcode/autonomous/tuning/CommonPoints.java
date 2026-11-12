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
        // TODO
        startParkTowardsLoading = poseFactory.of(7.2, 130.9, 0);
        startParkTowardsGarden = poseFactory.of(7.2, 78.4, 90);

        startGardenShoot = poseFactory.of(0, 0, 0);
        startLoadingShoot = poseFactory.of(0, 0, 0);
        // TODO

        parkTowardsLoading = poseFactory.of(11, 118.7, 270);
        parkTowardsGarden = poseFactory.of(10.8, 95.4, 90);

        flowerPickupLoading= poseFactory.of(46.8, 129.4, 90);
        flowerPickupGarden= poseFactory.of(13.7, 46.4, 180);

        frontHiveShotLoading= poseFactory.of(58.3, 117.6, 0);
        frontHiveShotGarden= poseFactory.of(58.3, 22.4, 180);
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

    // Flower Pickup
    public Pose flowerPickupLoading;
    public Pose flowerPickupGarden;

    // Front of Hive shooting spots
    public Pose frontHiveShotLoading;
    public Pose frontHiveShotGarden;
}
