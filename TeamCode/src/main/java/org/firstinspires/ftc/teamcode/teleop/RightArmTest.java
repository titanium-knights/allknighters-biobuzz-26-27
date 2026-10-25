package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@TeleOp(name = "RightArmTest")
public class RightArmTest extends OpMode {
    private static final double FINE = 0.01;
    private static final double COARSE = 0.05;

    private ServoImplEx armL, armR;
    private double pos = 0.5;
    private boolean powered = false;
    private boolean lastUp, lastDown, lastRB, lastLB, lastA;

    @Override
    public void init() {
        armL = hardwareMap.get(ServoImplEx.class, "ArmServoLeft");
        armR = hardwareMap.get(ServoImplEx.class, "ArmServoRight");
        armL.setPwmDisable(); // left stays limp the whole time
        armR.setPwmDisable(); // right limp until first press
    }

    @Override
    public void loop() {
        // Dpad up/down = 0.01, RB/LB = 0.05 (right arm only)
        double d = 0;
        if (gamepad1.dpad_up && !lastUp) d += FINE;
        if (gamepad1.dpad_down && !lastDown) d -= FINE;
        if (gamepad1.right_bumper && !lastRB) d += COARSE;
        if (gamepad1.left_bumper && !lastLB) d -= COARSE;
        if (d != 0) {
            pos = Math.max(0, Math.min(1, pos + d)); // full servo range 0 to 1
            armR.setPosition(pos);
            armR.setPwmEnable();
            powered = true;
        }

        // A = right arm limp
        if (gamepad1.a && !lastA) { armR.setPwmDisable(); powered = false; }

        lastUp = gamepad1.dpad_up; lastDown = gamepad1.dpad_down;
        lastRB = gamepad1.right_bumper; lastLB = gamepad1.left_bumper;
        lastA = gamepad1.a;

        telemetry.addLine("RIGHT arm only | dpad U/D 0.01 | RB/LB 0.05 | A = limp");
        telemetry.addData("Right pos", "%.2f (%s)", pos, powered ? "powered" : "limp");
        telemetry.addData("Left", "limp (no signal)");
        telemetry.addData("ArmR", armR.getConnectionInfo() + " port " + armR.getPortNumber());
        telemetry.update();
    }

    @Override
    public void stop() {
        armL.setPwmDisable();
        armR.setPwmDisable();
    }
}