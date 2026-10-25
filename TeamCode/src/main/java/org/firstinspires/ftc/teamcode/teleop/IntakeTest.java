package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@TeleOp(name = "IntakeTest")
public class IntakeTest extends OpMode {
    private static final double FINE = 0.01;
    private static final double COARSE = 0.05;
    private static final double ROLLER_POWER = 0.9;

    private ServoImplEx armL, armR; // position servos, mirrored
    private CRServo roller;

    private double pos = 0.0;  // left arm value; right = 1 - left
    private boolean powered = false;
    private double recordedDown = -1, recordedUp = -1;
    private boolean lastUp, lastDown, lastRB, lastLB, lastA, lastX, lastB;

    @Override
    public void init() {
        armL = hardwareMap.get(ServoImplEx.class, "ArmServoLeft");
        armR = hardwareMap.get(ServoImplEx.class, "ArmServoRight");
        roller = hardwareMap.get(CRServo.class, "Intake Servo");
        send(); // both arms to the zero position at Init (left 0.00, right 1.00 mirrored)
    }

    @Override
    public void start() {
        send(); // power both arms at Start (left = pos, right = 1 - pos)
    }

    @Override
    public void loop() {
        // A = PANIC: both arms limp
        if (gamepad1.a && !lastA) {
            armL.setPwmDisable();
            armR.setPwmDisable();
            powered = false;
        }

        // Move BOTH arms together (mirrored): dpad up/down = 0.01, RB/LB = 0.05
        double d = 0;
        if (gamepad1.dpad_up && !lastUp) d += FINE;
        if (gamepad1.dpad_down && !lastDown) d -= FINE;
        if (gamepad1.right_bumper && !lastRB) d += COARSE;
        if (gamepad1.left_bumper && !lastLB) d -= COARSE;
        if (d != 0) {
            pos = Math.max(0, Math.min(1, pos + d));
            send();
        }

        // X = record DOWN, B = record UP
        if (gamepad1.x && !lastX) recordedDown = pos;
        if (gamepad1.b && !lastB) recordedUp = pos;

        lastUp = gamepad1.dpad_up; lastDown = gamepad1.dpad_down;
        lastRB = gamepad1.right_bumper; lastLB = gamepad1.left_bumper;
        lastA = gamepad1.a; lastX = gamepad1.x; lastB = gamepad1.b;

        // Roller: hold RT = intake, hold LT = outtake
        double rp = 0;
        if (gamepad1.right_trigger > 0.5) rp = ROLLER_POWER;
        else if (gamepad1.left_trigger > 0.5) rp = -ROLLER_POWER;
        roller.setPower(rp);

        telemetry.addLine("Arms: dpad U/D 0.01 | RB/LB 0.05 | A = PANIC limp");
        telemetry.addLine("X = record DOWN | B = record UP | RT/LT = roller");
        telemetry.addData("Left / Right", "%.2f / %.2f (%s)", pos, 1.0 - pos, powered ? "powered" : "limp");
        telemetry.addData("Recorded DOWN", recordedDown < 0 ? "-" : String.format("%.2f", recordedDown));
        telemetry.addData("Recorded UP", recordedUp < 0 ? "-" : String.format("%.2f", recordedUp));
        telemetry.addData("Roller power", rp);
        telemetry.addData("ArmL", armL.getConnectionInfo() + " port " + armL.getPortNumber());
        telemetry.addData("ArmR", armR.getConnectionInfo() + " port " + armR.getPortNumber());
        telemetry.update();
    }

    /** Send both arms to pos (right mirrored) and power them. */
    private void send() {
        armL.setPosition(pos);
        armR.setPosition(1.0 - pos);
        armL.setPwmEnable();
        armR.setPwmEnable();
        powered = true;
    }

    @Override
    public void stop() {
        armL.setPwmDisable();
        armR.setPwmDisable();
        roller.setPower(0);
    }
}