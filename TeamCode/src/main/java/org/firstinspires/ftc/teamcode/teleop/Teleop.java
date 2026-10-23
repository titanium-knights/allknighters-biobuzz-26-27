package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Flywheel;
import org.firstinspires.ftc.teamcode.utilities.Intake;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;
import org.firstinspires.ftc.teamcode.utilities.ShootingLogic;
import org.firstinspires.ftc.teamcode.utilities.SubsystemManager;

//Wifi Password: FTCTitaniumA15887!

@TeleOp(name = "TeleOp")
public class Teleop extends OpMode {
    private SubsystemManager subsystemManager;
    private double modifier;
    private OurColorSensor ourColorSensor;
    private Intake intake;
    private MagazineServos magazineServos;
    private MagazineAngle magazineAngle;
    private OuttakeHood outtakeHood;
    private ShootingLogic outtake;
    private LinearSlides linearSlides;
    private Flywheel flywheel;
    private boolean lastLeftBumper;
    private boolean lastRightBumper;
    private boolean lastFlapButton;
    private boolean pollenFlapSelected;

    @Override
    public void init() {
        subsystemManager = new SubsystemManager(hardwareMap, telemetry);
        modifier = 1.0;
        ourColorSensor = subsystemManager.colorSensors;
        intake = subsystemManager.intake;
        magazineServos = subsystemManager.magazineServos;
        magazineAngle = subsystemManager.magazineAngle;
        outtakeHood = subsystemManager.outtakeHood;
        linearSlides = subsystemManager.linearSlides;
        outtake = subsystemManager.shootingLogic;
        flywheel = outtake.getFlywheel();
        outtakeHood.flapNectar();
        telemetry.addData("Drive", "Ready");
        telemetry.update();

    }

    @Override
    public void loop() {
        modifier = (gamepad1.b) ? -0.8 : 1.0;
        OurColorSensor.color detectedColor = ourColorSensor.detectColor(telemetry);
        ourColorSensor.magazineTelemetry(telemetry);

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

        // linear slides
        boolean stepUp = gamepad2.left_bumper && !lastLeftBumper;
        boolean stepDown = gamepad2.right_bumper && !lastRightBumper;
        lastLeftBumper = gamepad2.left_bumper;
        lastRightBumper = gamepad2.right_bumper;

        if (!linearSlides.isSlidesRunning() && stepUp != stepDown) {
            int level = linearSlides.getTargetLevel();
            if (stepUp) {
                if (level==0) linearSlides.toIntake();
                else if (level==1) linearSlides.toShoot();
            } else {
                if (level==2) linearSlides.toIntake();
                else if (level==1) linearSlides.toFlowerOuttake();
            }
        }

        // intake arm
        if (gamepad2.y && !gamepad2.a) {
            intake.closeIntake();
        } else if (gamepad2.a && !gamepad2.y) {
            intake.openIntake();
        }

        // hood flap
        if (gamepad2.x && !lastFlapButton) {
            pollenFlapSelected = !pollenFlapSelected;
            if (pollenFlapSelected) outtakeHood.flapPollen();
            else outtakeHood.flapNectar();
        }
        lastFlapButton = gamepad2.x;

        // magazine servos and intake
        if (linearSlides.isAtCorrectPositionToShoot() || linearSlides.getTargetLevel() == 2 || linearSlides.isSlidesRunning()) {
            intake.stopIntake();
            magazineServos.stopIntake();
        } else if (gamepad1.right_trigger > 0.5) {
            intake.run();
            magazineServos.run();
            if (detectedColor != OurColorSensor.color.EMPTY) {
                ourColorSensor.RegisterBallIntake(detectedColor);
            }
        } else if (gamepad1.left_trigger > 0.5) {
            intake.outtake();
            magazineServos.outtake();
        } else {
            intake.stopIntake();
            magazineServos.stopIntake();
        }

        // magazine angle
        if (linearSlides.isAtCorrectPositionToShoot()) {
            magazineAngle.setShootAngle();
        } else {
            magazineAngle.setStartAngle();
        }
        flywheel.setShooterOn(linearSlides.isAtCorrectPositionToShoot());
        outtake.update();

        telemetry.addData("slide level: ", linearSlides.getTargetLevel());
        telemetry.addData("is slide at top: ", linearSlides.isAtCorrectPositionToShoot());
        telemetry.addData("outtake flap: ", pollenFlapSelected ? "Pollen" : "Nectar");

        telemetry.update();
    }

    @Override
    public void stop() {
        if (subsystemManager != null) {
            subsystemManager.drive.stop();
            subsystemManager.intake.stopIntake();
            subsystemManager.magazineServos.stopIntake();
            flywheel.setShooterOn(false);
            outtake.update();
        }
    }
}
