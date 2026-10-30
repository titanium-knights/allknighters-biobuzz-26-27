package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/** One turret: an independent flywheel plus an aim servo. */
public class Turret {
    private static final double CENTER_POS = 0.5;
    private static final double AIM_GAIN = 0.005; // servo units per degree of tx; tune on robot
    private static final double SHOOT_POWER = 1.0; // placeholder, tune on robot

    private final DcMotor flywheel;
    private final Servo aimServo;
    private double aimPos = CENTER_POS;

    public Turret(HardwareMap hmap, String flywheelName, String aimServoName) {
        flywheel = hmap.get(DcMotor.class, flywheelName);
        aimServo = hmap.get(Servo.class, aimServoName);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        aimServo.setPosition(aimPos);
    }

    public void spinUp() {
        flywheel.setPower(SHOOT_POWER);
    }

    public void stop() {
        flywheel.setPower(0);
    }

    /** Nudges the servo toward the target. tx is degrees from the Limelight; NaN (no target) is ignored. */
    public void aim(double tx) {
        if (Double.isNaN(tx)) return;
        aimPos = Math.max(0, Math.min(1, aimPos + tx * AIM_GAIN));
        aimServo.setPosition(aimPos);
    }
}
