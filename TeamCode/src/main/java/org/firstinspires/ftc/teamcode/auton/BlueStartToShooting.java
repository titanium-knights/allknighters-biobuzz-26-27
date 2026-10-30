package org.firstinspires.ftc.teamcode.auton;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class BlueStartToShooting {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(97.5062, 135.9717, 270);

    private final Pose point1 = poseFactory.of(131.1655, 129.0673, 225);

    public Path path1() {
        return Paths.line(start, point1).linear(start, point1);
    }
}
