package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public Servo armServoL;
    public Servo armServoR;
    public CRServo intakeServo;

    // Flopper arm positions (measured). Left and right are mounted mirrored.
    public static double UP_L = 0.35, UP_R = 0.63;
    public static double DOWN_L = 0.0, DOWN_R = 1.0;
    public static double ROLLER_POWER = 0.9;

    private boolean armUp = false;

    public Intake(HardwareMap hmap){
        armServoL = hmap.get(Servo.class, "ArmServoLeft");
        armServoR = hmap.get(Servo.class, "ArmServoRight");
        intakeServo = hmap.get(CRServo.class, "Intake Servo");
        // No arm command here: nothing moves at Init
    }

    // ===== Arm =====
    public void armUp() {
        armServoL.setPosition(UP_L);
        armServoR.setPosition(UP_R);
        armUp = true;
    }
    public void armDown() {
        armServoL.setPosition(DOWN_L);
        armServoR.setPosition(DOWN_R);
        armUp = false;
    }
    public boolean isArmUp() { return armUp; }

    // ===== Roller =====
    public void run()        { intakeServo.setPower(ROLLER_POWER); }
    public void outtake()    { intakeServo.setPower(-ROLLER_POWER); }
    public void stopIntake() { intakeServo.setPower(0); }

    // ===== Legacy names (old TeleOp) =====
    public void openIntake()  { armDown(); }
    public void closeIntake() { armUp(); }
}