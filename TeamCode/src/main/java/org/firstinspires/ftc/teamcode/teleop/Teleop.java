package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Limelight;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;

//Wifi Password: FTCTitaniumA15887!

@TeleOp(name = "TeleOp")
public class Teleop extends OpMode {
    private SubsystemManager subsystemManager;
    private double modifier;
    private OurColorSensor ourColorSensor;

    @Override
    public void init() {
        subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        modifier = 1.0;
        telemetry.addData("Drive", "Ready");
        telemetry.update();
         ourColorSensor = new OurColorSensor(hardwareMap, telemetry);

    }

    @Override
    public void loop() {
        modifier = (gamepad1.b) ? -0.8 : 1.0;
        ourColorSensor.detectColor(telemetry);
        ourColorSensor.magazineTelemetry(telemetry);
        telemetry.update();

        if (!gamepad1.a) {
            subsystemManager.drive.move(
                    gamepad1.left_stick_x * modifier,
                    -gamepad1.left_stick_y * modifier, // negated bc y value is opposite on the stick
                    gamepad1.right_stick_x);
        } else {
            double xDpadDir = 0;
            if (gamepad1.dpad_left) { xDpadDir--; }
            if (gamepad1.dpad_right) { xDpadDir++; }
            xDpadDir *= modifier;

            double yDpadDir = 0;
            if (gamepad1.dpad_up) { yDpadDir++; }
            if (gamepad1.dpad_down) { yDpadDir--; }
            yDpadDir *= modifier;

            subsystemManager.drive.move(
                    xDpadDir, yDpadDir, gamepad1.right_stick_x * 0.8
            );
        }

        if (gamepad1.right_trigger > 0.5) {
            ourColorSensor.startIntake();
            if (ourColorSensor.detectColor(telemetry) != OurColorSensor.color.EMPTY) {
                ourColorSensor.RegisterBallIntake(ourColorSensor.detectColor(telemetry));
            }
        } else {
            ourColorSensor.stopIntake();
        }
        /*
        if (gamepad2.x) {
            ourColorSensor.
        } else {
            ourColorSensor.s
        }
         */
    }

    @Override
    public void stop() {
        if (subsystemManager != null) {
            subsystemManager.drive.stop();
        }
    }
}
