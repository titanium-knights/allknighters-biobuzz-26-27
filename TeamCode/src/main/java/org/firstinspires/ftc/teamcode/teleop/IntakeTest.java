package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utilities.Intake;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name = "IntakeTest")
@Disabled
public class IntakeTest extends OpMode {
    private Intake intake;
    private boolean lastRB, lastLB;

    @Override
    public void init() {
        intake = new Intake(hardwareMap);
        intake.armDown(); // start in the DOWN position (left 0.00, right 1.00)
    }

    @Override
    public void loop() {
        // RB = arm UP, LB = arm DOWN
        if (gamepad1.right_bumper && !lastRB) intake.armUp();
        if (gamepad1.left_bumper && !lastLB) intake.armDown();
        lastRB = gamepad1.right_bumper;
        lastLB = gamepad1.left_bumper;

        // Roller: hold RT = in, hold LT = out
        if (gamepad1.right_trigger > 0.5) intake.run();
        else if (gamepad1.left_trigger > 0.5) intake.outtake();
        else intake.stopIntake();

        telemetry.addLine("RB = arm UP | LB = arm DOWN | RT/LT = roller in/out");
        telemetry.addData("Arm", intake.isArmUp()
                ? String.format("UP (L %.2f / R %.2f)", Intake.UP_L, Intake.UP_R)
                : String.format("DOWN (L %.2f / R %.2f)", Intake.DOWN_L, Intake.DOWN_R));
        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stopIntake();
    }
}