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

    public static PinpointConfig pinpointConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.xPodOffset.set(0.0);
        c.yPodOffset.set(0.0);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {});

    public static Follower create(HardwareMap h) {
        Mecanum drivetrain = new Mecanum(h, drivetrainConfig);
        PinpointLocalizer localizer = new PinpointLocalizer(h, pinpointConfig);
        Foresight foresight = new Foresight(foresightConfig);
        return new Follower(localizer, drivetrain, foresight);
    }
}
