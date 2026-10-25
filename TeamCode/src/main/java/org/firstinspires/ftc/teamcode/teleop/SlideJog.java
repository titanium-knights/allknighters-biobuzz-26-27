package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name = "SlideJog")
@Disabled
public class SlideJog extends OpMode {
    private static final double JOG_POWER = 0.4;  // speed while a bumper is held
    private static final double HOLD_POWER = 0.9; // holding strength when released

    private LinearSlides slides;
    private DcMotor right, left;
    private boolean holding = false;

    @Override
    public void init() {
        // No encoder reset, nothing moves at Init (magazine servo gets no command)
        slides = new LinearSlides(hardwareMap, telemetry, new MagazineAngle(hardwareMap, false));
        right = slides.slideMotorRight;
        left = slides.slideMotorLeft;
    }

    @Override
    public void start() {
        holdHere(); // hold wherever the slides are right now
    }

    @Override
    public void loop() {
        boolean atBottom = slides.isStartLimitSwitchHit();
        boolean up = gamepad1.right_bumper && !gamepad1.left_bumper;
        boolean down = gamepad1.left_bumper && !gamepad1.right_bumper && !atBottom; // never push past StartLS

        if (up) jog(JOG_POWER);
        else if (down) jog(-JOG_POWER);
        else if (!holding) holdHere();

        // Left always follows right (mirrored)
        left.setTargetPosition(-right.getCurrentPosition());

        telemetry.addLine("Hold RB = up | Hold LB = down | release = hold position");
        telemetry.addData("State", up ? "UP" : down ? "DOWN" : "HOLDING");
        telemetry.addData("R pos", right.getCurrentPosition());
        telemetry.addData("L pos", left.getCurrentPosition());
        telemetry.addData("Start LS pressed", atBottom);
        telemetry.update();
    }

    /** Move the right (master) motor at a set power; left follows by encoder. */
    private void jog(double power) {
        holding = false;
        if (right.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) {
            right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        right.setPower(-power); // right counts negative going up
        if (left.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
            left.setTargetPosition(-right.getCurrentPosition());
            left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        left.setPower(HOLD_POWER);
    }

    /** Lock both slides at the current position. */
    private void holdHere() {
        int pos = right.getCurrentPosition();
        right.setTargetPosition(pos);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setPower(HOLD_POWER);
        left.setTargetPosition(-pos);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setPower(HOLD_POWER);
        holding = true;
    }

    @Override
    public void stop() {
        right.setPower(0);
        left.setPower(0);
    }
}