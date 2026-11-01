package org.firstinspires.ftc.teamcode.auton.paths;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class BlueLowerPark {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(111.5555, 9.1209, 90);
    private final Pose path1 = poseFactory.of(111.3474, 35.2752, 90);
    private final Pose point2 = poseFactory.of(131.9764, 35.2488, 90);

    public Path path1() {
        return Paths.line(start, path1).linear(start, path1);
    }

    public Path path2() {
        return Paths.line(path1, point2).constant(point2);
    }
}
