package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name = "IntakeTest")
@Disabled
public class IntakeTest extends OpMode {
    private ServoImplEx armL, armR;
    private CRServo roller;

    private double posL = 0.5, posR = 0.5;
    private boolean enabledL = false, enabledR = false;
    private int selected = 0; // 0 = left, 1 = right
    private boolean lastUp, lastDown, lastRB, lastLB, lastX, lastB, lastY, lastA;

    private static final double FINE = 0.01;
    private static final double COARSE = 0.05;
    private static final double ROLLER_POWER = 0.9;

    @Override
    public void init() {
        armL = hardwareMap.get(ServoImplEx.class, "ArmServoLeft");
        armR = hardwareMap.get(ServoImplEx.class, "ArmServoRight");
        roller = hardwareMap.get(CRServo.class, "Intake Servo");
        armL.setPwmDisable(); // no signal = limp, nothing moves at Init
        armR.setPwmDisable();
    }

    @Override
    public void loop() {
        // X = work on LEFT (right goes limp), B = work on RIGHT (left goes limp)
        if (gamepad1.x && !lastX) { selected = 0; armR.setPwmDisable(); enabledR = false; }
        if (gamepad1.b && !lastB) { selected = 1; armL.setPwmDisable(); enabledL = false; }

        // Y = power BOTH at their current values (only after you know they agree)
        if (gamepad1.y && !lastY) {
            armL.setPosition(posL); armL.setPwmEnable(); enabledL = true;
            armR.setPosition(posR); armR.setPwmEnable(); enabledR = true;
        }

        // A = PANIC: both arm servos limp
        if (gamepad1.a && !lastA) {
            armL.setPwmDisable(); enabledL = false;
            armR.setPwmDisable(); enabledR = false;
        }

        // Nudge selected servo: dpad up/down = 0.01, RB/LB = 0.05
        double delta = 0;
        if (gamepad1.dpad_up && !lastUp) delta += FINE;
        if (gamepad1.dpad_down && !lastDown) delta -= FINE;
        if (gamepad1.right_bumper && !lastRB) delta += COARSE;
        if (gamepad1.left_bumper && !lastLB) delta -= COARSE;
        if (delta != 0) {
            if (selected == 0) {
                posL = Math.max(0, Math.min(1, posL + delta));
                armL.setPosition(posL); armL.setPwmEnable(); enabledL = true;
            } else {
                posR = Math.max(0, Math.min(1, posR + delta));
                armR.setPosition(posR); armR.setPwmEnable(); enabledR = true;
            }
        }

        lastX = gamepad1.x; lastB = gamepad1.b; lastY = gamepad1.y; lastA = gamepad1.a;
        lastUp = gamepad1.dpad_up; lastDown = gamepad1.dpad_down;
        lastRB = gamepad1.right_bumper; lastLB = gamepad1.left_bumper;

        // Roller: hold RT = intake, hold LT = outtake (same directions as Intake.java)
        double rp = 0;
        if (gamepad1.right_trigger > 0.5) rp = ROLLER_POWER;
        else if (gamepad1.left_trigger > 0.5) rp = -ROLLER_POWER;
        roller.setPower(rp);

        telemetry.addLine("X=select L  B=select R  Y=power both  A=PANIC limp");
        telemetry.addLine("dpad up/down = +/-0.01   RB/LB = +/-0.05   RT/LT = roller");
        telemetry.addData("Selected", selected == 0 ? "LEFT" : "RIGHT");
        telemetry.addData("Left pos", "%.2f  (%s)", posL, enabledL ? "powered" : "limp");
        telemetry.addData("Right pos", "%.2f  (%s)", posR, enabledR ? "powered" : "limp");
        telemetry.addData("Roller power", rp);
        telemetry.update();
    }

    @Override
    public void stop() {
        armL.setPwmDisable();
        armR.setPwmDisable();
        roller.setPower(0);
    }
}