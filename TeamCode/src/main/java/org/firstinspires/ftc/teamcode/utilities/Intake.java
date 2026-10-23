package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public Servo armServoL;
    public Servo armServoR;
    public CRServo intakeServo;

    double openPos = -1; // Placeholder for the servo position for the arm
    double closePos = -1; // Placeholder for the servo position for the intake
    double power = 0.9; // Placeholder
    public Intake(HardwareMap hmap){
        armServoL = hmap.get(Servo.class, "ArmServoLeft");
        armServoR = hmap.get(Servo.class, "ArmServoRight");
        intakeServo = hmap.get(CRServo.class, "Intake Servo");
    }

    public void stopIntake(){ // sets power to 0 - everything stops
        intakeServo.setPower(0);
    }
    public void run() {
        intakeServo.setPower(-power);
    }

    public void outtake() {
        intakeServo.setPower(power);
    }

    public void openIntake(){
        if (openPos >= 0 && openPos <= 1) {
            armServoL.setPosition(openPos);
            armServoR.setPosition(openPos);
        }
    }

    public void closeIntake(){
        if (closePos >= 0 && closePos <= 1) {
            armServoL.setPosition(closePos);
            armServoR.setPosition(closePos);
        }
    }
}
