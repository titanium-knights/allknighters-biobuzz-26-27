package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name = "StowAngleFinder")
@Disabled
public class StowAngleFinder extends OpMode {
    private static final double FINE = 0.01;
    private static final double COARSE = 0.05;
    private static final double START_POS = MagazineAngle.INTAKE_POS; // "vertical" angle the servo goes to at Start

    private MagazineAngle magazineAngle;
    private double pos;
    private boolean powered = false;
    private double recorded = -1;
    private boolean lastA, lastX, lastUp, lastDown, lastRB, lastLB;

    @Override
    public void init() {
        magazineAngle = new MagazineAngle(hardwareMap, false); // no command at Init
        pos = START_POS; // nudging starts from the vertical position
    }
    @Override
    public void start() {
        send(); // move to the vertical position at Start (not Init)
    }

    @Override
    public void loop() {
        // A = power the servo at the current value (first check: does it move?)
        if (gamepad1.a && !lastA) send();

        // Nudge: dpad up/down = 0.01, RB/LB = 0.05
        double d = 0;
        if (gamepad1.dpad_up && !lastUp) d += FINE;
        if (gamepad1.dpad_down && !lastDown) d -= FINE;
        if (gamepad1.right_bumper && !lastRB) d += COARSE;
        if (gamepad1.left_bumper && !lastLB) d -= COARSE;
        if (d != 0) {
            pos = Math.max(0, Math.min(1, pos + d));
            send();
        }

        // X = record the current value as the stow angle
        if (gamepad1.x && !lastX) recorded = pos;

        lastA = gamepad1.a; lastX = gamepad1.x;
        lastUp = gamepad1.dpad_up; lastDown = gamepad1.dpad_down;
        lastRB = gamepad1.right_bumper; lastLB = gamepad1.left_bumper;

        telemetry.addLine("A = power at value | dpad U/D = 0.01 | RB/LB = 0.05 | X = record");
        telemetry.addLine("Slides are NOT powered in this OpMode");
        telemetry.addData("Magazine value", "%.2f (%s)", pos, powered ? "powered" : "not powered yet");
        telemetry.addData("Current STOW_POS in code", "%.2f", MagazineAngle.STOW_POS);
        telemetry.addData("Recorded stow", recorded < 0 ? "-" : String.format("%.2f  -> put in STOW_POS", recorded));
        telemetry.update();
    }

    private void send() {
        magazineAngle.angleServo.setPosition(pos);
        powered = true;
    }
}