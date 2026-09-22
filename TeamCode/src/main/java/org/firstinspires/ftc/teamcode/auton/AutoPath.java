package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "AutoPath", group = "Autonomous")
public class AutoPath extends LinearOpMode {

    private Follower follower;
    private int pathState = 0;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(57, 9, 90);
    private final Pose point1 = poseFactory.of(57, 39, 90);
    private final Pose point2 = poseFactory.of(9, 39, 0);
    private final Pose point3 = poseFactory.of(9, 48, 0);
    private final Pose point4 = poseFactory.of(9, 93, 0);

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.follow(path1());
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    follower.follow(path2());
                    setPathState(2);
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.follow(path3());
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    follower.follow(path4());
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int state) {
        pathState = state;
    }

    @Override
    public void runOpMode() {
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();

        setPathState(0);

        while (opModeIsActive() && !isStopRequested()) {
            follower.update();
            autonomousPathUpdate();

            telemetry.addData("Path State", pathState);
            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path path1() {
        return line(start, point1).linear(Math.toRadians(90), Math.toRadians(90));
    }

    public Path path2() {
        return line(point1, point2).linear(Math.toRadians(90), Math.toRadians(0));
    }

    public Path path3() {
        return line(point2, point3).linear(Math.toRadians(0), Math.toRadians(0));
    }

    public Path path4() {
        return line(point3, point4).linear(Math.toRadians(0), Math.toRadians(0));
    }
}
