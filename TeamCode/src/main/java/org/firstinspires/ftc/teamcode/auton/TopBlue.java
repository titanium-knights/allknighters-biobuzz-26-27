package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class TopBlue {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(88, 8, 90);
    private final Pose point1 = poseFactory.of(134.2892, 74.0981, -125.004);
    private final Pose point2 = poseFactory.of(134.3884, 106.2285, -90.1768);

    public Path path1() {
        return Paths.line(start, point1).reverseTangent();
    }

    public Path path2() {
        return Paths.line(point1, point2).reverseTangent();
    }
}

