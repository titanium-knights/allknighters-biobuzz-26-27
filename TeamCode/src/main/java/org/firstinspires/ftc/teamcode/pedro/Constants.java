package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.utilities.CONFIG;

public class Constants {

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set(CONFIG.FRONT_LEFT);
        c.frontRightName.set(CONFIG.FRONT_RIGHT);
        c.backLeftName.set(CONFIG.BACK_LEFT);
        c.backRightName.set(CONFIG.BACK_RIGHT);
        c.frontLeftDirection.set(CONFIG.FL_DIRECTION);
        c.frontRightDirection.set(CONFIG.FR_DIRECTION);
        c.backLeftDirection.set(CONFIG.BL_DIRECTION);
        c.backRightDirection.set(CONFIG.BR_DIRECTION);
    });

    // From the Pinpoint Tuner. Re-run it if the pods are moved.
    public static PinpointConfig pinpointConfig = new PinpointConfig(c -> {
        c.name.set(CONFIG.PINPOINT);
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-3.089297737662248);
        c.yPodOffset.set(7.128120482437255); // sign flipped: was tuned while yPodDirection was wrong; a 90° turn in place drifted (-14.5, -11.0)
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED); // pushing left read y=-24.0 with FORWARD; Pedro wants +y = left
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {});

    public static Follower create(HardwareMap h) {
        Mecanum drivetrain = new Mecanum(h, drivetrainConfig);
        PinpointLocalizer localizer = new PinpointLocalizer(h, pinpointConfig);
        Foresight foresight = new Foresight(foresightConfig);
        return new Follower(localizer, drivetrain, foresight);
    }
}
