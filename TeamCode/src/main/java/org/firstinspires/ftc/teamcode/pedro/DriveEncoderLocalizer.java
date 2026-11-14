package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.localization.Localizer;
import com.pedropathing.localization.MotionState;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.pedropathing.revhub.localizers.CustomIMU;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.utils.Angle;
import com.pedropathing.utils.Timer;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utilities.CONFIG;

/**
 * Tracks the robot's position using the four mecanum drive-motor encoders, plus the Control Hub's
 * built-in IMU for heading. Used because the robot has no odometry pods or Pinpoint.
 *
 * Limitation: mecanum wheels slip, so position drifts a few inches over a match. Fine for short
 * leave/shoot/park moves; add odometry pods (and a pod localizer) if autons need precision.
 */
public class DriveEncoderLocalizer implements Localizer {
    // CALIBRATE: these assume goBILDA 5203 312 RPM motors (537.7 ticks/rev) and 104 mm mecanum wheels.
    // To check: push the robot straight forward 48 in and compare with the Odometry Test tuner; scale
    // FORWARD_INCHES_PER_TICK by (48 / reported). Do the same sideways for STRAFE_INCHES_PER_TICK.
    private static final double TICKS_PER_REV = 537.7;
    private static final double WHEEL_DIAMETER_IN = 104.0 / 25.4;
    private static final double FORWARD_INCHES_PER_TICK = Math.PI * WHEEL_DIAMETER_IN / TICKS_PER_REV;
    private static final double STRAFE_INCHES_PER_TICK = FORWARD_INCHES_PER_TICK; // usually a bit less; calibrate

    // CALIBRATE: which way the Control Hub's logo and USB ports face on the robot.
    private static final RevHubOrientationOnRobot HUB_ORIENTATION = new RevHubOrientationOnRobot(
            RevHubOrientationOnRobot.LogoFacingDirection.UP,
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);

    private final DcMotorEx fl, fr, bl, br;
    private final CustomIMU imu = new RevHubIMU(HUB_ORIENTATION);
    private final Timer timer = new Timer();

    private int prevFl, prevFr, prevBl, prevBr;
    private double prevImuHeading;
    private Pose pose = Pose.zero();
    private MotionState motionState = MotionState.ofVelocity(pose, Velocity.zero());

    /** Create the Mecanum drivetrain first so the motor directions are already set. */
    public DriveEncoderLocalizer(HardwareMap map) {
        fl = map.get(DcMotorEx.class, CONFIG.FRONT_LEFT);
        fr = map.get(DcMotorEx.class, CONFIG.FRONT_RIGHT);
        bl = map.get(DcMotorEx.class, CONFIG.BACK_LEFT);
        br = map.get(DcMotorEx.class, CONFIG.BACK_RIGHT);
        imu.initialize(map, CONFIG.IMU);
        resetReadings();
    }

    @Override
    public void update() {
        double dtSeconds = timer.nanoseconds() / 1e9;
        timer.reset();

        int curFl = fl.getCurrentPosition(), curFr = fr.getCurrentPosition();
        int curBl = bl.getCurrentPosition(), curBr = br.getCurrentPosition();
        double dFl = curFl - prevFl, dFr = curFr - prevFr, dBl = curBl - prevBl, dBr = curBr - prevBr;
        prevFl = curFl; prevFr = curFr; prevBl = curBl; prevBr = curBr;

        // Inverse of Mecanum.computeWheelPowersUnnormalized: fl=f-s-t, fr=f+s+t, bl=f+s-t, br=f-s+t
        double forward = (dFl + dFr + dBl + dBr) / 4.0 * FORWARD_INCHES_PER_TICK;
        double strafe = (-dFl + dFr + dBl - dBr) / 4.0 * STRAFE_INCHES_PER_TICK;

        double imuHeading = Angle.normalize(imu.getHeading());
        double dHeading = Angle.turnDirection(prevImuHeading, imuHeading)
                * Angle.smallestDifference(imuHeading, prevImuHeading);
        prevImuHeading = imuHeading;

        // Rotate the robot-relative move into field coordinates using the mid-step heading.
        double mid = pose.heading() + dHeading / 2.0;
        double dx = forward * Math.cos(mid) - strafe * Math.sin(mid);
        double dy = forward * Math.sin(mid) + strafe * Math.cos(mid);

        pose = pose.plus(new Pose(dx, dy, dHeading));
        Velocity velocity = dtSeconds > 0
                ? new Velocity(dx / dtSeconds, dy / dtSeconds, dHeading / dtSeconds)
                : Velocity.zero();
        motionState = MotionState.ofVelocity(pose, velocity);
    }

    @Override
    public void setPose(Pose newPose) {
        pose = newPose;
        resetReadings();
        motionState = motionState.withPose(newPose);
    }

    @Override
    public MotionState state() {
        return motionState;
    }

    @Override
    public void reset() {
        imu.resetYaw();
        setPose(Pose.zero());
    }

    private void resetReadings() {
        prevFl = fl.getCurrentPosition();
        prevFr = fr.getCurrentPosition();
        prevBl = bl.getCurrentPosition();
        prevBr = br.getCurrentPosition();
        prevImuHeading = Angle.normalize(imu.getHeading());
    }
}
