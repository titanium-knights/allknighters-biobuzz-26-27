package org.firstinspires.ftc.teamcode.auton;

import static com.pedropathing.api.Paths.*;
import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

public class RightBlue {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(132, 48, 90);
    private final Pose point1 = poseFactory.of(126, 48, 0);
    private final Pose point2 = poseFactory.of(135, 90, -102.0948);
    private final Pose point3 = poseFactory.of(135, 105, -90);

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
