package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.utilities.MagazineServos;
import org.firstinspires.ftc.teamcode.utilities.OuttakeHood;

@TeleOp(name = "MagazineTest")
@Disabled
public class MagazineTest extends OpMode {
    private CRServo belt;
    private OuttakeHood hood;
    private DcMotor flywheel;
    private MagazineServos magWheels;

    private static final double BELT_POWER = 1.0;     // same power MagazineBelt uses
    private static final double HOOD_STEP = 0.01;
    private static final double HOOD_STEP_BIG = 0.05;
    private static final double FLYWHEEL_POWER = 1.0;

    private boolean wheelsOn = false;
    private boolean flywheelOn = false;

    // Belt timing
    private final ElapsedTime holdTimer = new ElapsedTime();
    private final ElapsedTime cycleTimer = new ElapsedTime();
    private boolean beltHeld = false;
    private double lastHoldTime = 0;
    private double cycleTime = 1.5;   // seconds per paddle (one ball). X runs it, L3 copies last hold time
    private boolean cycleRunning = false;

    private boolean lastLeft, lastRight, lastLB, lastRB, lastA, lastY, lastX, lastL3;

    @Override
    public void init() {
        belt = hardwareMap.get(CRServo.class, "Belt Servo");
        hood = new OuttakeHood(hardwareMap); // moves hood to INIT_POS (0.65)
        flywheel = hardwareMap.get(DcMotor.class, "ShootMotor");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setPower(0);
        magWheels = new MagazineServos(hardwareMap);
    }

    @Override
    public void loop() {
        boolean beltAllowed = hood.isSafeForBelt();

        // ===== BELT: hold dpad up/down = manual (timed), X = one timed cycle =====
        double beltPower = 0;
        if (gamepad1.dpad_up || gamepad1.dpad_down) {
            cycleRunning = false; // manual overrides a timed cycle
            if (!beltHeld) { holdTimer.reset(); beltHeld = true; }
            beltPower = gamepad1.dpad_up ? BELT_POWER : -BELT_POWER;
        } else {
            if (beltHeld) { lastHoldTime = holdTimer.seconds(); beltHeld = false; }
            if (gamepad1.x && !lastX && !cycleRunning && beltAllowed) { cycleRunning = true; cycleTimer.reset(); }
            if (cycleRunning) {
                if (cycleTimer.seconds() < cycleTime) beltPower = BELT_POWER;
                else cycleRunning = false;
            }
        }
        if (!beltAllowed) { beltPower = 0; cycleRunning = false; } // hood too low: belt locked
        belt.setPower(beltPower);

        // L3 (press left stick) = use last manual hold time as the cycle time
        if (gamepad1.left_stick_button && !lastL3 && lastHoldTime > 0) cycleTime = lastHoldTime;

        // ===== HOOD: dpad left/right = +/-0.01, LB/RB = +/-0.05 =====
        if (gamepad1.dpad_right && !lastRight) hood.nudge(HOOD_STEP);
        if (gamepad1.dpad_left && !lastLeft) hood.nudge(-HOOD_STEP);
        if (gamepad1.right_bumper && !lastRB) hood.nudge(HOOD_STEP_BIG);
        if (gamepad1.left_bumper && !lastLB) hood.nudge(-HOOD_STEP_BIG);

        // ===== MAGAZINE INTAKE WHEELS: A = toggle in, hold B = reverse =====
        if (gamepad1.a && !lastA) wheelsOn = !wheelsOn;
        if (gamepad1.b) magWheels.outtake();
        else if (wheelsOn) magWheels.run();
        else magWheels.stopIntake();

        // ===== FLYWHEEL: Y = toggle full power =====
        if (gamepad1.y && !lastY) flywheelOn = !flywheelOn;
        flywheel.setPower(flywheelOn ? FLYWHEEL_POWER : 0);

        lastLeft = gamepad1.dpad_left; lastRight = gamepad1.dpad_right;
        lastLB = gamepad1.left_bumper; lastRB = gamepad1.right_bumper;
        lastA = gamepad1.a; lastY = gamepad1.y; lastX = gamepad1.x;
        lastL3 = gamepad1.left_stick_button;

        // ===== TELEMETRY =====
        telemetry.addLine("Belt:dpad U/D X L3 | Hood:dpad L/R LB/RB | A/B wheels | Y fly");
        if (!beltAllowed) telemetry.addLine("BELT LOCKED: hood below " + OuttakeHood.BELT_SAFE_MIN);
        telemetry.addData("Belt power", "%.2f", beltPower);
        telemetry.addData("Belt hold (live)", "%.2f s", beltHeld ? holdTimer.seconds() : 0.0);
        telemetry.addData("Last hold time", "%.2f s", lastHoldTime);
        telemetry.addData("Cycle time (X)", "%.2f s", cycleTime);
        telemetry.addData("Cycle running", cycleRunning);
        telemetry.addData("Hood pos", "%.2f", hood.getPosition());
        telemetry.addData("Mag wheels", gamepad1.b ? "REVERSE" : (wheelsOn ? "IN" : "off"));
        telemetry.addData("Flywheel", flywheelOn ? "ON" : "off");
        telemetry.update();
    }

    @Override
    public void stop() {
        belt.setPower(0);
        magWheels.stopIntake();
        flywheel.setPower(0);
    }
}