package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Flywheel;
import org.firstinspires.ftc.teamcode.utilities.Intake;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;
import org.firstinspires.ftc.teamcode.utilities.ShootingLogic;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;

//Wifi Password: FTCTitaniumA15887!

@TeleOp(name = "PushBotTeleop")

public class PushBotTeleop extends OpMode{
    private SubsystemManager subsystemManager;
    double modifier = 1.0;

    @Override
    public void init() {
        telemetry.addData("Drive", "Ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        // chassis
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
    }

    @Override
    public void stop() {
        if (subsystemManager != null) {
            subsystemManager.drive.stop();
            subsystemManager.intake.stopIntake();
            subsystemManager.magazineServos.stopIntake();
        }
    }
}
