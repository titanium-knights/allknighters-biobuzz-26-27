package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class RedShooting {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(46.4938, 8.0283, 90);
    private final Pose path1 = poseFactory.of(46.5, 12, 90);
    private final Pose point2Start = poseFactory.of(46.5, 12, 90);
    private final Pose point2 = poseFactory.of(12.2933, 13.8547, 45);

    public Path path1() {
        return Paths.line(start, path1).constant(path1);
    }

    public Path path2() {
        return Paths.line(point2Start, point2).linear(point2Start, point2);
    }
}
