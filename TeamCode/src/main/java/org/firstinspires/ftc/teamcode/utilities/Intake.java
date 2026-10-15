package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public Servo armServo;
    public CRServo intakeServo;;

    double openPos = -1; // Placeholder for the servo position for the arm
    double closePos = -1; // Placeholder for the servo position for the intake
    double power = 0.9; // Placeholder
    public Intake(HardwareMap hmap){
        armServo = hmap.get(Servo.class, "Arm Servo");
        intakeServo = hmap.get(CRServo.class, "Intake Servo");
    }

    public void stopIntake(){ // sets power to 0 - everything stops
        intakeServo.setPower(0);
    }
    public void run() {
        intakeServo.setPower(-power);
    }

    public void openIntake(){
        armServo.setPosition(openPos);
    }

    public void closeIntake(){
        armServo.setPosition(closePos);
    }
}
