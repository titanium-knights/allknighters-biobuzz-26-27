package org.firstinspires.ftc.teamcode.auton;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.utilities.CONFIG;
import org.firstinspires.ftc.teamcode.utilities.Limelight;
import org.firstinspires.ftc.teamcode.utilities.Turret;

/**
 * Shared auton: start, leave, [shoot from the corner], park. The eight OpModes in Autons only
 * choose the StartConfig and whether to shoot.
 */
public abstract class BaseAuton extends LinearOpMode {
    // TODO: shot count unknown (no feeder yet), so shooting is timed; replace with real feed logic
    private static final double SHOOT_SECONDS = 6.0; // placeholder: time to empty the 4 pre-loaded pollen

    private final StartConfig config;
    private final boolean shouldShoot;

    protected BaseAuton(StartConfig config, boolean shouldShoot) {
        this.config = config;
        this.shouldShoot = shouldShoot;
    }

    @Override
    public void runOpMode() {
        Follower follower = Constants.create(hardwareMap);
        follower.setPose(PoseFactory.degrees().of(config.start[0], config.start[1], config.startHeadingDeg));
        follower.update();
        Turret pollenTurret = new Turret(hardwareMap, CONFIG.POLLEN_FLYWHEEL, CONFIG.POLLEN_AIM_SERVO);
        Limelight limelight = new Limelight(hardwareMap, telemetry);
        Parking nav = new Parking(this, follower);

        telemetry.addData("Auton", "%s, %s", config, shouldShoot ? "leave+shoot+park" : "leave+park");
        telemetry.update();
        waitForStart();

        try {
            boolean ok = nav.driveTo("Leave", config.leave, config.startHeadingDeg);
            if (ok && shouldShoot) {
                ok = nav.driveTo("Shoot spot", config.shoot, config.shootHeadingDeg);
                if (ok) shoot(pollenTurret, limelight);
            }
            if (ok) nav.park(config.alliance);
        } finally {
            pollenTurret.stop();
            limelight.stop();
        }
    }

    private void shoot(Turret pollenTurret, Limelight limelight) {
        ElapsedTime timer = new ElapsedTime();
        pollenTurret.spinUp();
        while (opModeIsActive() && timer.seconds() < SHOOT_SECONDS) {
            limelight.update();
            pollenTurret.aim(limelight.getLimeLightInfoSpecific("tx"));
            telemetry.addData("Shooting", "%.1f / %.1f s", timer.seconds(), SHOOT_SECONDS);
            telemetry.update();
        }
        pollenTurret.stop();
    }
}
