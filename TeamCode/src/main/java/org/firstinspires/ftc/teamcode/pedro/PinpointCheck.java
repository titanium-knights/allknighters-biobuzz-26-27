package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.math.Pose;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/** Push the robot by hand and watch the numbers. Run before any tuner that drives itself:
 *  those tuners stop only when the Pinpoint says the robot has gone far enough. */
@TeleOp(name = "PinpointCheck", group = "test")
public class PinpointCheck extends LinearOpMode {
    @Override
    public void runOpMode() {
        PinpointLocalizer localizer = new PinpointLocalizer(hardwareMap, Constants.pinpointConfig);
        telemetry.addLine("Push forward 24in: x should go to ~+24. Push left 24in: y should change ~24. A = reset to 0.");
        telemetry.update();
        waitForStart();
        localizer.setPose(Pose.zero());
        while (opModeIsActive()) {
            if (gamepad1.a) localizer.setPose(Pose.zero());
            localizer.update();
            Pose p = localizer.pose();
            telemetry.addData("x (in, forward +)", "%.1f", p.x());
            telemetry.addData("y (in)", "%.1f", p.y());
            telemetry.addData("heading (deg)", "%.1f", Math.toDegrees(p.heading()));
            telemetry.update();
        }
    }
}
