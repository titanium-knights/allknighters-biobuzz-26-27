package org.firstinspires.ftc.teamcode.auton;

import static java.lang.Thread.sleep;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.SimpleMecanumDrive;

//DEFINED AS THE POSITION
@Autonomous(name = "RedClose", group = "Autonomous")
public class RedClose extends LinearOpMode {
    private SimpleMecanumDrive drive;;
    private MagazineAngle magazineAngle;
    private LinearSlides linearSlides;

    private void moveFor(double x, double y, double turn, long milliseconds)
            throws InterruptedException {

        drive.move(x, y, turn);

        try {
            sleep(milliseconds);
        } finally {
            drive.stop();
        }
    }
    @Override
    public void runOpMode() throws InterruptedException{
        drive = new SimpleMecanumDrive(hardwareMap);
        magazineAngle = new MagazineAngle(hardwareMap);
        linearSlides = new LinearSlides(hardwareMap, telemetry, magazineAngle);
        waitForStart();

        linearSlides.startInitialManeuver();
        while (linearSlides.isMoving()){
            linearSlides.update();
        }
        moveFor(20,0,0,0);

    }

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose point1 = poseFactory.of(9.7108, 74.0981, -54.996);
    private final Pose point2 = poseFactory.of(9.6116, 106.2285, -89.8232);

    public Path path1() {
        return Paths.line(start, point1).reverseTangent();
    }

    public Path path2() {
        return Paths.line(point1, point2).reverseTangent();
    }
}
