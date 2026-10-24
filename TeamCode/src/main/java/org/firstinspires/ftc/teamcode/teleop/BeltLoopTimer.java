package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.utilities.MagazineBelt;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;

@TeleOp(name = "BeltLoopTimer")
@Disabled
public class BeltLoopTimer extends OpMode {
    private static final double ALIGN_POWER = 0.3; // slow manual jog for lining up

    private MagazineBelt belt;
    private OuttakeHood hood;
    private VoltageSensor battery;
    private final ElapsedTime timer = new ElapsedTime();

    private boolean timing = false;
    private double lastLoop = 0, sum = 0;
    private int trials = 0;
    private boolean lastA, lastB;

    @Override
    public void init() {
        belt = new MagazineBelt(hardwareMap);
        hood = new OuttakeHood(hardwareMap); // hood to 0.65 so the belt is allowed to run
        battery = hardwareMap.voltageSensor.iterator().next();
    }

    @Override
    public void loop() {
        boolean safe = hood.isSafeForBelt();

        // A = start timing (belt runs at full power) / stop timing when the paddle is back
        if (gamepad1.a && !lastA) {
            if (!timing) {
                if (safe) { timing = true; timer.reset(); }
            } else {
                lastLoop = timer.seconds();
                sum += lastLoop;
                trials++;
                timing = false;
            }
        }
        // B = clear results
        if (gamepad1.b && !lastB) { lastLoop = 0; sum = 0; trials = 0; }
        lastA = gamepad1.a; lastB = gamepad1.b;

        // Belt power: full while timing, slow jog with dpad up/down for lining up
        double power = 0;
        if (timing) power = MagazineBelt.BELT_POWER;
        else if (gamepad1.dpad_up) power = ALIGN_POWER;
        else if (gamepad1.dpad_down) power = -ALIGN_POWER;
        if (!safe) { power = 0; timing = false; }
        belt.beltServo.setPower(power);

        telemetry.addLine("Dpad U/D = jog to mark | A = start/stop timing | B = clear");
        if (!safe) telemetry.addLine("BELT LOCKED: hood below " + OuttakeHood.BELT_SAFE_MIN);
        telemetry.addData("Timing", timing ? String.format("RUNNING %.2f s", timer.seconds()) : "stopped");
        telemetry.addData("Last loop", "%.2f s", lastLoop);
        telemetry.addData("Average", trials > 0 ? String.format("%.2f s (%d trials)", sum / trials, trials) : "-");
        telemetry.addData("Battery", "%.2f V", battery.getVoltage());
        telemetry.update();
    }

    @Override
    public void stop() {
        belt.stop();
    }
}