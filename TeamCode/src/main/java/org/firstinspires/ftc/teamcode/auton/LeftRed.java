package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class LeftRed {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(12, 48, 90);
    private final Pose point1 = poseFactory.of(18, 48, 180);
    private final Pose point2 = poseFactory.of(9, 90, -77.9052);
    private final Pose point3 = poseFactory.of(9, 105, -90);

    public Path path1() {
        return Paths.line(start, point1).reverseTangent();
    }

    public Path path2() {
        return Paths.line(point1, point2).reverseTangent();
    }

    public Path path3() {
        return Paths.line(point2, point3).reverseTangent();
    }
}

