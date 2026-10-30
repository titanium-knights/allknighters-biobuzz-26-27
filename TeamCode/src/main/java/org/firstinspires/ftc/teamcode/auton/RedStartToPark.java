package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class RedStartToPark {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(46.4938, 8.0283, 90);
    private final Pose path1 = poseFactory.of(10, 107, 0);

    public Path path1() {
        return Paths.line(start, path1).linear(start, path1);
    }
}
