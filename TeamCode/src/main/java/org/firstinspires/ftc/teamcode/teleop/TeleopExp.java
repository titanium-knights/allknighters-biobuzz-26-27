package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Intake;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;
import org.firstinspires.ftc.teamcode.utilities.ShootingLogic;
import org.firstinspires.ftc.teamcode.utilities.SimpleMecanumDrive;

@TeleOp(name = "TeleopExp")
public class TeleopExp extends OpMode {
    private MagazineAngle magazineAngle;
    private LinearSlides linearSlides;
    private SimpleMecanumDrive drive;
    private Intake intake;
    private MagazineServos magazineServos;
    private ShootingLogic shooter;

    private boolean startOk = false;
    private boolean intakeOn = false;
    private boolean lastIntakeToggle = false;
    private boolean lastSlideUp = false;
    private boolean lastSlideDown = false;
    private boolean lastShootBtn = false;
    private boolean lastAtShoot = false;

    @Override
    public void init() {
        magazineAngle = new MagazineAngle(hardwareMap);
        linearSlides = new LinearSlides(hardwareMap, telemetry, magazineAngle);
        drive = new SimpleMecanumDrive(hardwareMap);
        intake = new Intake(hardwareMap);
        magazineServos = new MagazineServos(hardwareMap);
        shooter = new ShootingLogic(hardwareMap, telemetry, null,
                new MagazineBelt(hardwareMap), new OuttakeHood(hardwareMap));
    }

    @Override
    public void init_loop() {
        telemetry.addData("Start LS pressed", linearSlides.isStartLimitSwitchHit());
        telemetry.addLine("Slides must be on StartLS before pressing Start");
        telemetry.update();
    }

    @Override
    public void start() {
        startOk = linearSlides.startInitialManeuver();
        if (!startOk) {
            telemetry.addLine("ABORTED: StartLS not pressed at Start");
            telemetry.update();
            requestOpModeStop();
        }
    }

    @Override
    public void loop() {
        if (!startOk) return; // aborted, do nothing until the OpMode stops

        // ===== GAMEPAD 1: drive + intake =====
        // Drive: left stick = move, right stick x = turn (turn reversed)
        drive.move(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);

        // Intake allowed only at intake level (slides down)
        boolean slidesDown = linearSlides.getLevel() == 0;
        if (!slidesDown) intakeOn = false; // slides went up: stop and reset toggle

        // RB = toggle intake on/off
        boolean toggle = gamepad1.right_bumper;
        if (toggle && !lastIntakeToggle && slidesDown) intakeOn = !intakeOn;
        lastIntakeToggle = toggle;

        // Hold LT = reverse (unjam), overrides toggle
        if (slidesDown && gamepad1.left_trigger > 0.5) {
            intake.outtake();
            magazineServos.outtake();
        } else if (intakeOn) {
            intake.run();
            magazineServos.run();
        } else {
            intake.stopIntake();
            magazineServos.stopIntake();
        }

        // ===== GAMEPAD 2: slides + shooting =====
        // RB = up one level, LB = down one level (ignored during start maneuver)
        boolean up = gamepad2.right_bumper;
        boolean down = gamepad2.left_bumper;
        if (up && !lastSlideUp) linearSlides.levelUp();
        if (down && !lastSlideDown) linearSlides.levelDown();
        lastSlideUp = up;
        lastSlideDown = down;

        // At shoot level (arrived): magazine to shoot angle, flywheel on
        boolean atShoot = linearSlides.getLevel() == 2 && !linearSlides.isMoving();
        if (atShoot && !lastAtShoot) magazineAngle.toShoot();
        if (!atShoot && lastAtShoot) magazineAngle.toIntake();
        lastAtShoot = atShoot;
        shooter.setSpinning(atShoot);

        // B = shoot (only at shoot level; belt feeds only when flywheel is at speed)
        boolean shootBtn = gamepad2.b;
        if (shootBtn && !lastShootBtn && atShoot) shooter.shoot();
        lastShootBtn = shootBtn;

        linearSlides.update();
        shooter.update();

        linearSlides.addTelemetry(telemetry);
        shooter.addTelemetry(telemetry);
        telemetry.addData("Intake", intakeOn ? "ON" : "off");
        telemetry.update();
    }

    @Override
    public void stop() {
        drive.stop();
        intake.stopIntake();
        magazineServos.stopIntake();
        shooter.stop();
    }
}