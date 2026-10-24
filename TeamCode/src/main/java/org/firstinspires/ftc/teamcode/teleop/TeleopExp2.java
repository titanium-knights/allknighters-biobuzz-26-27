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

@TeleOp(name = "TeleopExp2")
public class TeleopExp2 extends OpMode {
    public static int PRELOAD_COUNT = 3;          // balls in the magazine at match start
    public static double AXON_WAKE_TIME = 1.0;    // seconds after Start before the first RB is accepted

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

    private final ElapsedTime startTimer = new ElapsedTime();
    private boolean startOk = false;
    private boolean intakeOn = false;
    private boolean cameFromShoot = false;
    private boolean launchPending = false;
    private boolean lastIntakeToggle, lastSlideUp, lastSlideDown, lastLaunch, lastAtShoot;

    @Override
    public void init() {
        // Nothing is commanded at Init: no robot motion before Start
        magazineAngle = new MagazineAngle(hardwareMap, false);
        linearSlides = new LinearSlides(hardwareMap, telemetry, magazineAngle);
        drive = new SimpleMecanumDrive(hardwareMap);
        intake = new Intake(hardwareMap);
        magazineServos = new MagazineServos(hardwareMap);
        belt = new MagazineBelt(hardwareMap);
        belt.setTotalRun(PRELOAD_COUNT * belt.timeToRotateBall); // preloaded = belt is 3 cycles into its loop
        hood = new OuttakeHood(hardwareMap, false);
        colorSensor = new OurColorSensor(hardwareMap, telemetry);
        indexer = new BallIndexer(colorSensor, belt, hood, PRELOAD_COUNT);
        shooter = new ShootingLogic(hardwareMap, telemetry, colorSensor, belt, hood);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Start LS pressed", linearSlides.isStartLimitSwitchHit());
        telemetry.addLine("Slides must be on StartLS. Nothing moves until Start.");
        telemetry.update();
    }

    @Override
    public void start() {
        if (!linearSlides.isStartLimitSwitchHit()) {
            telemetry.addLine("ABORTED: StartLS not pressed at Start");
            telemetry.update();
            requestOpModeStop();
            return;
        }
        startOk = true;
        magazineAngle.toStow(); // powers the Axon, holds the angle it already sits at
        hood.goToInit();        // hood to INIT_POS
        startTimer.reset();
    }

    @Override
    public void loop() {
        if (!startOk) return; // aborted, do nothing until the OpMode stops

        // ===== GAMEPAD 1: drive =====
        drive.move(gamepad1.left_stick_x, -gamepad1.left_stick_y, -gamepad1.right_stick_x);

        // ===== GAMEPAD 2: slides =====
        // First RB = tilt + lift to intake (after Axon wake time). Then RB = flower, shoot. LB = down, never below intake.
        boolean deployed = linearSlides.getLevel() != -1;
        boolean up = gamepad2.right_bumper;
        boolean down = gamepad2.left_bumper;
        if (up && !lastSlideUp && (deployed || startTimer.seconds() >= AXON_WAKE_TIME)) linearSlides.levelUp();
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
        if (!slidesDown || indexer.isFull()) intakeOn = false; // slides up or magazine full: stop
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

        // ===== Auto indexing: pollen seen -> one belt cycle =====
        indexer.update(atIntake && intakeOn);

        // ===== Shooting (gamepad 2 X = launch sequence) =====
        if (atShoot && !lastAtShoot) magazineAngle.toShoot();
        if (!atShoot && lastAtShoot) magazineAngle.toIntake();
        lastAtShoot = atShoot;
        shooter.setSpinning(atShoot);

        boolean launch = gamepad2.x;
        if (launch && !lastLaunch && atShoot && !shooter.isShooting) {
            shooter.launch();
            launchPending = true;
        }
        lastLaunch = launch;

        // Launch finished: magazine is empty and the first paddle is back on the mark
        if (launchPending && !shooter.isShooting) {
            if (shooter.wasLaunchCompleted()) indexer.setCount(0);
            launchPending = false;
        }

        // ===== Updates + telemetry =====
        linearSlides.update();
        shooter.update(); // also runs belt timing

        if (!deployed) {
            telemetry.addLine(startTimer.seconds() < AXON_WAKE_TIME
                    ? "Waking magazine servo..." : "Gamepad 2 RB = deploy (tilt + lift)");
        }
        indexer.addTelemetry(telemetry);
        shooter.addTelemetry(telemetry);
        telemetry.addData("Intake", intakeOn ? "ON" : "off");
        linearSlides.addTelemetry(telemetry);
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