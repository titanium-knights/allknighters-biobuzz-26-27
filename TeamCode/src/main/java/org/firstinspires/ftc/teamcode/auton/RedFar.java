package org.firstinspires.ftc.teamcode.auton;

import static java.lang.Thread.sleep;

import com.pedropathing.api.PoseFactory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.SimpleMecanumDrive;

//DEFINED AS THE POSITION
@Autonomous(name = "RedFar", group = "Autonomous")
public class RedFar extends LinearOpMode {
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
        moveFor(200, 0,0,0);
        moveFor(0,20,0,0);
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

    }

    private final PoseFactory poseFactory = PoseFactory.degrees();

}
