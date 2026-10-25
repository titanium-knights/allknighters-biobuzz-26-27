package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utilities.BallIndexer;
import org.firstinspires.ftc.teamcode.utilities.Intake;
import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OurColorSensor;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;
import org.firstinspires.ftc.teamcode.utilities.ShootingLogic;
import org.firstinspires.ftc.teamcode.utilities.SimpleMecanumDrive;

@TeleOp(name = "RecoveryTeleop")
public class RecoveryTeleop extends OpMode {
    private MagazineAngle magazineAngle;
    private LinearSlides linearSlides;
    private SimpleMecanumDrive drive;
    private Intake intake;
    private MagazineServos magazineServos;
    private MagazineBelt belt;
    private OuttakeHood hood;
    private OurColorSensor colorSensor;
    private BallIndexer indexer;
    private ShootingLogic shooter;

    private boolean magazineSet = false;
    private boolean intakeOn = false;
    private boolean cameFromShoot = false;
    private boolean launchPending = false;
    private boolean lastIntakeToggle, lastSlideUp, lastSlideDown, lastLaunch, lastAtShoot;
    private boolean lastMagUp, lastMagDown, lastForceIntake, lastCountUp, lastCountDown;
    private final ElapsedTime loopTimer = new ElapsedTime();
    private boolean lastTrimUp, lastTrimDown;

    @Override
    public void init() {
        // Nothing is commanded at Init
        magazineAngle = new MagazineAngle(hardwareMap, false);
        linearSlides = new LinearSlides(hardwareMap, telemetry, magazineAngle);
        drive = new SimpleMecanumDrive(hardwareMap);
        intake = new Intake(hardwareMap);
        magazineServos = new MagazineServos(hardwareMap);
        belt = new MagazineBelt(hardwareMap);              // assumes paddle on the mark
        hood = new OuttakeHood(hardwareMap, false);
        colorSensor = new OurColorSensor(hardwareMap, telemetry);
        indexer = new BallIndexer(colorSensor, belt, hood, 0); // ball count unknown: start at 0, fix with dpad
        shooter = new ShootingLogic(hardwareMap, telemetry, colorSensor, belt, hood);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("RECOVERY: on Start, slides go down slowly to find IntakeLS.");
        telemetry.addData("Intake LS pressed", linearSlides.isIntakeSwitchHit());
        telemetry.addData("Start LS pressed", linearSlides.isStartLimitSwitchHit());
        telemetry.update();
    }

    @Override
    public void start() {
        hood.goToInit();
        linearSlides.startRecoveryHoming();
    }

    @Override
    public void loop() {
        // ===== GAMEPAD 1: drive =====
        drive.move(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);

        // ===== Magazine to intake angle once homing has found IntakeLS =====
        if (!magazineSet && linearSlides.getLevel() >= 0 && !linearSlides.isHoming()) {
            magazineAngle.toIntake();
            magazineSet = true;
        }

        // ===== GAMEPAD 2: slides (ignored while homing) =====
        boolean up = gamepad2.right_bumper;
        boolean down = gamepad2.left_bumper;
        if (up && !lastSlideUp) linearSlides.levelUp();
        if (down && !lastSlideDown) linearSlides.levelDown();
        lastSlideUp = up;
        lastSlideDown = down;

        boolean slidesDown = linearSlides.getLevel() == 0;
        boolean atIntake = slidesDown && !linearSlides.isMoving() && !linearSlides.isStartManeuverRunning();
        boolean atShoot = linearSlides.getLevel() == 2 && !linearSlides.isMoving();

        // ===== Belt offset after returning from shoot (only when empty) =====
        if (atShoot) cameFromShoot = true;
        if (atIntake && cameFromShoot) {
            indexer.runReturnOffset();
            cameFromShoot = false;
        }

        // ===== GAMEPAD 1: intake (RB toggle, hold LT reverse) =====
        if (!slidesDown || indexer.isFull()) intakeOn = false;
        boolean toggle = gamepad1.right_bumper;
        if (toggle && !lastIntakeToggle && slidesDown && !indexer.isFull()) intakeOn = !intakeOn;
        lastIntakeToggle = toggle;

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

        // ===== Auto indexing + override =====
        indexer.update(atIntake && intakeOn);
        boolean force = gamepad2.y;
        if (force && !lastForceIntake && atIntake) indexer.forceIntake();
        lastForceIntake = force;
        // Gamepad 2 dpad up/down AT INTAKE: trim slide height (e.g. lift off the ground)
        if (atIntake) {
            if (gamepad2.dpad_up && !lastTrimUp) linearSlides.trimIntake(LinearSlides.TRIM_TICKS);
            if (gamepad2.dpad_down && !lastTrimDown) linearSlides.trimIntake(-LinearSlides.TRIM_TICKS);
        }
        lastTrimUp = gamepad2.dpad_up;
        lastTrimDown = gamepad2.dpad_down;

        // ===== Gamepad 2 dpad right/left: fix ball count (not at shoot) =====
        if (!atShoot) {
            if (gamepad2.dpad_right && !lastCountUp) indexer.setCount(indexer.getCount() + 1);
            if (gamepad2.dpad_left && !lastCountDown) indexer.setCount(indexer.getCount() - 1);
        }
        lastCountUp = gamepad2.dpad_right;
        lastCountDown = gamepad2.dpad_left;

        // ===== Shooting =====
        if (atShoot && !lastAtShoot) magazineAngle.toShoot();
        if (!atShoot && lastAtShoot) magazineAngle.toIntake();
        lastAtShoot = atShoot;
        shooter.setSpinning(atShoot);
        if (atShoot) {
            if (gamepad2.dpad_up && !lastMagUp) magazineAngle.nudgeShoot(MagazineAngle.NUDGE_STEP);
            if (gamepad2.dpad_down && !lastMagDown) magazineAngle.nudgeShoot(-MagazineAngle.NUDGE_STEP);
        }
        lastMagUp = gamepad2.dpad_up;
        lastMagDown = gamepad2.dpad_down;

        boolean launch = gamepad2.x;
        if (launch && !lastLaunch && atShoot && !shooter.isShooting) {
            shooter.launch();
            launchPending = true;
        }
        lastLaunch = launch;
        if (launchPending && !shooter.isShooting) {
            if (shooter.wasLaunchCompleted()) indexer.setCount(0);
            launchPending = false;
        }

        // ===== Updates + telemetry =====
        linearSlides.update();
        shooter.update();

        telemetry.addLine("RECOVERY MODE");
        indexer.addTelemetry(telemetry);
        shooter.addTelemetry(telemetry);
        telemetry.addData("Intake", intakeOn ? "ON" : "off");
        linearSlides.addTelemetry(telemetry);
        telemetry.addData("Mag shoot pos", "%.2f", MagazineAngle.SHOOT_POS);
        telemetry.addData("Loop time (ms)", "%.0f", loopTimer.milliseconds());
        loopTimer.reset();
        telemetry.update();
    }

    @Override
    public void stop() {
        drive.stop();
        intake.stopIntake();
        magazineServos.stopIntake();
        belt.stop();
        shooter.stop();
    }
}