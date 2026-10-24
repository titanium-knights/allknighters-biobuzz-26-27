package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;

@TeleOp(name = "MagazineTest2")
public class MagazineTest2 extends OpMode {
    public static double SPINUP_TIME = 1.0;  // seconds after Start before the belt may run
    public static double EMPTY_TIME = 5.0;   // Y runs the belt this long
    public static double FLYWHEEL_POWER = 1.0;

    private MagazineBelt belt;
    private OuttakeHood hood;
    private MagazineServos magWheels;
    private DcMotor flywheel;

    private final ElapsedTime spinTimer = new ElapsedTime();
    private boolean lastA, lastB, lastY;
    private String lastAction = "none";

    @Override
    public void init() {
        belt = new MagazineBelt(hardwareMap);
        hood = new OuttakeHood(hardwareMap); // hood to 0.65 at Init
        magWheels = new MagazineServos(hardwareMap);
        flywheel = hardwareMap.get(DcMotor.class, "ShootMotor");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setPower(0);
    }

    @Override
    public void init_loop() {
        telemetry.addLine("Start = flywheel + magazine wheels ON. Keep hands clear.");
        telemetry.addData("Hood pos", "%.2f", hood.getPosition());
        telemetry.update();
    }

    @Override
    public void start() {
        flywheel.setPower(FLYWHEEL_POWER);
        magWheels.run();
        spinTimer.reset();
    }

    @Override
    public void loop() {
        boolean ready = spinTimer.seconds() >= SPINUP_TIME;
        boolean safe = hood.isSafeForBelt();

        // B = one paddle (1.5 s)
        if (gamepad1.b && !lastB && ready && safe && !belt.isRunning) {
            belt.startCycle();
            lastAction = "1 cycle";
        }
        // Y = empty the magazine (5 s)
        if (gamepad1.y && !lastY && ready && safe) {
            belt.runFor(EMPTY_TIME);
            lastAction = "empty (5 s)";
        }
        // A = stop the belt now
        if (gamepad1.a && !lastA) {
            belt.stop();
            lastAction = "stopped";
        }
        if (!safe) belt.stop(); // hood too low: belt locked
        belt.update();

        lastA = gamepad1.a; lastB = gamepad1.b; lastY = gamepad1.y;

        telemetry.addLine("B = 1 cycle | Y = empty 5 s | A = stop belt");
        if (!ready) telemetry.addLine("Flywheel spinning up...");
        if (!safe) telemetry.addLine("BELT LOCKED: hood below " + OuttakeHood.BELT_SAFE_MIN);
        telemetry.addData("Belt", belt.isRunning ? String.format("RUNNING (%.1f s left)", belt.timeLeft()) : "stopped");
        telemetry.addData("Last action", lastAction);
        telemetry.addData("Hood pos", "%.2f", hood.getPosition());
        telemetry.addData("Flywheel", "ON (%.2f)", FLYWHEEL_POWER);
        telemetry.addData("Magazine wheels", "ON");
        telemetry.update();
    }

    @Override
    public void stop() {
        belt.stop();
        magWheels.stopIntake();
        flywheel.setPower(0);
    }
}