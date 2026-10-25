package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;

@TeleOp(name = "PreloadMagazine")
public class PreloadMagazine extends OpMode {
    private static final double JOG_POWER = 0.3; // slow jog to line the first paddle up on the mark
    private static final int CAPACITY = 3;

    private MagazineBelt belt;
    private OuttakeHood hood;
    private int loaded = 0;
    private boolean lastB;

    @Override
    public void init() {
        belt = new MagazineBelt(hardwareMap);
        hood = new OuttakeHood(hardwareMap, false); // nothing moves at Init
    }

    @Override
    public void init_loop() {
        telemetry.addLine("Press Start, then line paddle 1 up on the mark.");
        telemetry.update();
    }

    @Override
    public void start() {
        hood.goToInit(); // hood to 0.625 so the belt is allowed to run
    }

    @Override
    public void loop() {
        belt.update(); // stops each cycle when its time is up
        boolean safe = hood.isSafeForBelt();

        // B = ball loaded on the current paddle: move one cycle
        if (gamepad1.b && !lastB && safe && !belt.isRunning && loaded < CAPACITY) {
            belt.startCycle();
            loaded++;
        }
        lastB = gamepad1.b;

        // Before the first ball only: dpad up/down jogs the belt slowly to line paddle 1 up on the mark
        if (!belt.isRunning) {
            if (loaded == 0 && safe && gamepad1.dpad_up) belt.beltServo.setPower(JOG_POWER);
            else if (loaded == 0 && safe && gamepad1.dpad_down) belt.beltServo.setPower(-JOG_POWER);
            else belt.beltServo.setPower(0);
        }

        if (loaded == 0) telemetry.addLine("1) Dpad up/down: put paddle 1 on the mark");
        if (loaded < CAPACITY) telemetry.addLine("2) Place a ball, then press B");
        else telemetry.addLine("DONE: Stop this OpMode, then run TeleopExp2");
        if (!safe) telemetry.addLine("BELT LOCKED: hood below " + OuttakeHood.BELT_SAFE_MIN);
        telemetry.addData("Loaded", "%d / %d%s", loaded, CAPACITY, belt.isRunning ? "  (moving)" : "");
        telemetry.update();
    }

    @Override
    public void stop() {
        belt.stop();
    }
}