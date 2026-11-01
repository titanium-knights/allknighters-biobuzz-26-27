package org.firstinspires.ftc.teamcode.auton.paths;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class BlueUpperPark {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(97.5062, 135.9717, 270);
    private final Pose path1 = poseFactory.of(134, 37, 180);

    public Path path1() { return Paths.line(start, path1).linear(start, path1); }
}
