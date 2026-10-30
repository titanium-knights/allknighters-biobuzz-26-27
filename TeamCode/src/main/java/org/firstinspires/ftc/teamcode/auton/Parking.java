package org.firstinspires.ftc.teamcode.auton;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utilities.ParkingRoute;

import static com.pedropathing.api.Paths.line;

/**
 * Drives from the follower's current pose to the alliance Loading Zone, detouring around the
 * central HIVE/FLOWERS keep-out zone. Uses Pedro field coordinates (inches, 0-144, same as the autons).
 *
 * Call from the end of any auton: new Parking(this, follower).park(Alliance.RED);
 *
 * Field constants come from BIOBUZZ manual TU07 section 9 (+/- 1 in). Red alliance is on the left,
 * origin is the bottom-left corner (audience side at the bottom), x right, y up.
 */
public class Parking {
    // LOADING ZONEs are 11 in deep by 23 in wide against the wall: red = tile A5 (x 0-11, y 96-120),
    // blue = tile F2 (x 133-144, y 24-48). Targets sit 12 in from the wall so the robot overlaps the zone.
    private static final double[] RED_PARK = {12, 108};
    private static final double[] BLUE_PARK = {132, 36};
    private static final double PARK_HEADING_DEG = 90;

    // Floor obstacles as {xMin, yMin, xMax, yMax}, from manual section 9 (+/- 1 in).
    // HIVE frame base: 49.46 in wide (x) by 38.95 in deep (y), centered on the field. The HIVE cells
    // sit 30.6 in above the tiles, higher than any legal robot, so only the frame footprint matters.
    private static final double[] HIVE_FRAME = {47.3, 52.5, 96.7, 91.5};
    // FLOWERS are mounted on the perimeter wall, about 6 in. square at the floor.
    // Wall positions estimated from figure 9-4: left wall y=48, top wall x=48, right wall y=96, bottom wall x=96.
    private static final double[][] FLOWERS = {
            {0, 45, 5, 51}, {45, 139, 51, 144}, {139, 93, 144, 99}, {93, 0, 99, 5}};
    private static final double ROBOT_RADIUS = 11; // half the robot's diagonal; measure and set
    private static final double MARGIN = 3;
    private static final double FIELD_SIZE = 144;

    private static final double TIMEOUT_SECONDS = 8;

    private final LinearOpMode opMode;
    private final Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    public Parking(LinearOpMode opMode, Follower follower) {
        this.opMode = opMode;
        this.follower = follower;
    }

    /** Drives to the alliance Loading Zone. Returns true if parked. */
    public boolean park(Alliance alliance) {
        return driveTo("Park " + alliance, alliance == Alliance.RED ? RED_PARK : BLUE_PARK, PARK_HEADING_DEG);
    }

    /**
     * Drives to any field point around the HIVE frame and FLOWERS. Blocks until it arrives, times out,
     * or the OpMode stops. Motors are always stopped. Returns true if it arrived.
     */
    public boolean driveTo(String label, double[] target, double headingDeg) {
        double clearance = ROBOT_RADIUS + MARGIN;
        double[][] boxes = new double[FLOWERS.length + 1][];
        boxes[0] = inflate(HIVE_FRAME, clearance);
        for (int i = 0; i < FLOWERS.length; i++) boxes[i + 1] = inflate(FLOWERS[i], clearance);
        boolean arrived = false;
        try {
            Pose start = follower.pose();
            double[][] route = ParkingRoute.plan(start.x(), start.y(), target[0], target[1],
                    boxes, ROBOT_RADIUS, FIELD_SIZE - ROBOT_RADIUS);
            if (route == null) {
                report(label, target, -1, "FAILED: no clear route (robot inside a keep-out zone?)");
                return false;
            }
            arrived = driveRoute(label, target, headingDeg, route, start);
        } finally {
            follower.stop();
        }
        report(label, target, -1, arrived ? "ARRIVED" : "STOPPED before reaching target");
        return arrived;
    }

    private boolean driveRoute(String label, double[] target, double headingDeg, double[][] route, Pose start) {
        ElapsedTime timer = new ElapsedTime();
        double fromX = start.x(), fromY = start.y();
        double fromHeading = Math.toDegrees(start.heading());

        for (int i = 0; i < route.length; i++) {
            double toX = clamp(route[i][0]), toY = clamp(route[i][1]);
            boolean isLast = i == route.length - 1;
            double toHeading = isLast ? headingDeg : fromHeading; // turn only on the final leg

            Pose from = poseFactory.of(fromX, fromY, fromHeading);
            Pose to = poseFactory.of(toX, toY, toHeading);
            follower.follow(line(from, to).linear(Math.toRadians(fromHeading), Math.toRadians(toHeading)));

            while (opMode.opModeIsActive() && follower.isBusy()) {
                if (timer.seconds() > TIMEOUT_SECONDS) return false;
                follower.update();
                report(label, target, i + 1, "DRIVING (" + (i + 1) + "/" + route.length + ")");
            }
            if (!opMode.opModeIsActive()) return false;
            fromX = toX; fromY = toY; fromHeading = toHeading;
        }
        return true;
    }

    private static double[] inflate(double[] b, double by) {
        return new double[] {b[0] - by, b[1] - by, b[2] + by, b[3] + by};
    }

    private static double clamp(double v) {
        return Math.max(ROBOT_RADIUS, Math.min(FIELD_SIZE - ROBOT_RADIUS, v));
    }

    private void report(String label, double[] target, int waypoint, String status) {
        Pose p = follower.pose();
        opMode.telemetry.addData("Nav goal", label);
        opMode.telemetry.addData("Nav target", "(%.1f, %.1f)", target[0], target[1]);
        opMode.telemetry.addData("Nav pose", "(%.1f, %.1f)", p.x(), p.y());
        opMode.telemetry.addData("Nav waypoint", waypoint);
        opMode.telemetry.addData("Nav status", status);
        opMode.telemetry.update();
    }
}
