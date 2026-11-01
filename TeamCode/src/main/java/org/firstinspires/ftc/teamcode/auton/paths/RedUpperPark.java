package org.firstinspires.ftc.teamcode.auton.paths;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class RedUpperPark {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(31.4781, 131.8938, 90);
    private final Pose path1 = poseFactory.of(31.63, 107, 270);
    private final Pose point2 = poseFactory.of(9.1551, 106.9756, 270);

    public Path path1() {
        return Paths.line(start, path1).constant(path1);
    }

    public Path path2() {
        return Paths.line(path1, point2).constant(point2);
    }
}
