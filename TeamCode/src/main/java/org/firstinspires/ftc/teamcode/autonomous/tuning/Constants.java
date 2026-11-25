package org.firstinspires.ftc.teamcode.autonomous.tuning;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrain;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("fl");
        c.frontRightName.set("fr");
        c.backLeftName.set("bl");
        c.backRightName.set("br");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.manualBrakeMode.set(true); // Teleop usage
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-3.7428187573049954);
        c.yPodOffset.set(-6.076012558824435);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.4057117815770092);
                Controller secondaryTranslationalForward = Controller.proportional(0.149899662474243);
                Controller primaryTranslationalLateral = Controller.proportional(0.8557004620713441);
                Controller secondaryTranslationalLateral = Controller.proportional(0.3161584559979092);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.012875810395750515));
                c.brake.set(Controller.proportionalFeedforward(0.010944438836387937));

                c.headingFeedback.set(Controller.proportional(7.4844571288569055));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.033438693314369446, 0.011318729052093655));

                c.linearBrakeCoefficients.set(Matrix.diag(0.1409194404820246, 0.06352937562407393));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0013093998549490637, 0.0019045878831744619));

                c.maxAchievableForwardVelocity.set(71.51932281253801);
                c.maxAchievableStrafeVelocity.set(56.60595894563372);
                c.naturalForwardDeceleration.set(33.06791783683243);
                c.naturalStrafeDeceleration.set(73.34740889539992);
            }
    );

    public static Follower create(HardwareMap h) {
        // return new Follower(Drivetrain, Localizer, Foresight);
//        return null;
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}