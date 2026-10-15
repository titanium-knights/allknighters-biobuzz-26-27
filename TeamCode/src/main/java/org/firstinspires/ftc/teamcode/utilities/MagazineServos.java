package org.firstinspires.ftc.teamcode.utilities;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;

public class MagazineServos {
    public CRServo subIntakeServo;
    double power = 0.9;
    public MagazineServos(HardwareMap hmap){
        subIntakeServo = hmap.get(CRServo.class, "Sub Intake Servo");
    }
    public void stopIntake(){ // sets power to 0 - everything stops
        subIntakeServo.setPower(0);
    }
    public void run() {
        subIntakeServo.setPower(-power);
    }
}
