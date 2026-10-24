package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.utilities.LinearSlides;
import org.firstinspires.ftc.teamcode.utilities.MagazineAngle;

@TeleOp(name = "SlideMeasure")
public class SlideMeasure extends OpMode {
    private LinearSlides slides;
    private static final double POWER = 0.3; // raise if slides cannot lift the magazine

    private boolean zeroed = false;
    private boolean measured = false;
    private int measuredL = 0;
    private int measuredR = 0;
    private boolean lastStartHit = false;
    private boolean lastIntakeHit = false;

    @Override
    public void init() {
        slides = new LinearSlides(hardwareMap, telemetry, new MagazineAngle(hardwareMap));
    }

    @Override
    public void init_loop() {
        telemetry.addData("Start LS pressed", slides.isStartLimitSwitchHit());
        telemetry.addLine("Slides must be on StartLS before pressing Start");
        telemetry.update();
    }

    @Override
    public void start() {
        if (slides.isStartLimitSwitchHit()) {
            resetEncoders();
            zeroed = true;
        }
        lastStartHit = true;
    }

    @Override
    public void loop() {
        boolean startHit = slides.isStartLimitSwitchHit();
        boolean intakeHit = slides.isIntakeSwitchHit();

        if (!zeroed) {
            setSlidePower(0);
            telemetry.addLine("NOT ZEROED: slides were not on StartLS at Start. Stop and restart.");
            telemetry.update();
            return;
        }

        // Back on StartLS: re-zero so you can run another trial
        // Back on StartLS: allow another trial (no re-zero, zero only once at Start)
        if (startHit && !lastStartHit) {
            measured = false;
        }
        lastStartHit = startHit;

        // Record the first IntakeLS press of each trip up
        if (intakeHit && !lastIntakeHit && !measured) {
            measuredL = slides.slideMotorLeft.getCurrentPosition();
            measuredR = slides.slideMotorRight.getCurrentPosition();
            measured = true;
        }
        lastIntakeHit = intakeHit;

        // Hold A = up (stops at IntakeLS), hold B = down (stops at StartLS)
        double power = 0;
        if (gamepad1.a && !intakeHit) power = POWER;
        else if (gamepad1.b && !startHit) power = -POWER;
        setSlidePower(power);

        telemetry.addData("Start LS pressed", startHit);
        telemetry.addData("Intake LS pressed", intakeHit);
        telemetry.addData("L position", slides.slideMotorLeft.getCurrentPosition());
        telemetry.addData("R position", slides.slideMotorRight.getCurrentPosition());
        telemetry.addData("Measured", measured);
        telemetry.addData("L ticks Start->Intake", measuredL);
        telemetry.addData("R ticks Start->Intake", measuredR);
        telemetry.addData("Avg magnitude", (Math.abs(measuredL) + Math.abs(measuredR)) / 2);
        telemetry.update();
    }

    @Override
    public void stop() {
        setSlidePower(0);
    }

    private void setSlidePower(double p) {
        slides.slideMotorRight.setPower(-p);  // right is master, counts negative going up
        // left follows right's encoder (mirrored)
        slides.slideMotorLeft.setTargetPosition(-slides.slideMotorRight.getCurrentPosition());
        slides.slideMotorLeft.setPower(p == 0 && !slides.slideMotorLeft.isBusy() ? 0 : 0.9);
    }
    private void resetEncoders() {
        slides.slideMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slides.slideMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slides.slideMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slides.slideMotorLeft.setTargetPosition(0);
        slides.slideMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}